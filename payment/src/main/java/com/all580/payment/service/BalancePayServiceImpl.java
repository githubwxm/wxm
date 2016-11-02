package com.all580.payment.service;

import com.all580.order.api.service.PaymentCallbackService;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.dao.CapitalMapper;
import com.all580.payment.dao.CapitalSerialMapper;
import com.all580.payment.entity.Capital;
import com.all580.payment.entity.CapitalSerial;
import com.all580.payment.exception.BusinessException;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.TopicPushManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.*;

/**
 * 余额支付的实现类
 *
 * @author Created by panyi on 2016/9/28.
 */
@Service("balancePayService")
public class BalancePayServiceImpl implements BalancePayService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mns.topic}")
    private String topicName;
    @Value("${mns.access.id}")
    private String mnsAccessId;
    @Value("${mns.access.key}")
    private String mnsAccessKey;
    @Value("${mns.endpoint}")
    private String mnsAccountEndpoint;

    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CapitalSerialMapper capitalSerialMapper;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Autowired
    private TopicPushManager topicPushManager;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type,
                                                   final String serialNum) {
        Assert.notNull(balanceChangeInfoList, "参数【balanceChangeInfoList】不能为空");
        Assert.notNull(type, "参数【type】不能为空");
        Assert.notNull(serialNum, "参数【serialNum】不能为空");
        logger.info(MessageFormat.format("开始 -> 余额变更：type={0}|serialNum={1}", type.toString(), serialNum));
        Result<BalanceChangeRsp> result = new Result<>();
        try {
            // 批量锁定
            List<Capital> capitals = capitalMapper.selectForUpdateByEpList(balanceChangeInfoList);
            Assert.notNull(capitals, "余额账号为空");
            // 检查余额账户，并生成流水记录
            List<CapitalSerial> capitalSerialList = checkBalanceAccount(balanceChangeInfoList, capitals, type, serialNum);
            // 保存流水记录
            recodeSerial(capitalSerialList);
            // 变更余额
            changeBalances(capitals);
            // 发布余额变更事件
            fireBalanceChangedEvent(capitals);

            // 回调订单模块-余额支付
            if (PaymentConstant.BalanceChangeType.BALANCE_PAY.equals(type)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        paymentCallbackService.payCallback(Long.parseLong(serialNum), serialNum, null);
                    }
                }).start();
            }
            // 回调订单模块-余额退款
            if (PaymentConstant.BalanceChangeType.BALANCE_REFUND.equals(type)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Result rst = paymentCallbackService.refundCallback(Long.parseLong(serialNum), serialNum, null,
                                true);
                        logger.debug("余额退款回调成功：" + rst.isSuccess());
                    }
                }).start();
            }
            result.setSuccess();
            logger.info("完成 -> 余额变更");
        } catch (BusinessException e) {
            logger.error("失败 -> 余额变更出现异常，原因：" + e.getMessage(), e);
            result.setFail();
            result.put((BalanceChangeRsp) e.getData());
        }

        return result;
    }

    /**
     * 检查余额账户是否存在；检查账户的余额是否足够
     */
    private List<CapitalSerial> checkBalanceAccount(List<BalanceChangeInfo> balanceChangeInfoList,
                                                    List<Capital> capitals, Integer type, String serialNum) {
        List<CapitalSerial> capitalSerials = new ArrayList<>();
        Map<String, Capital> capitalMap = convertCapitalList(capitals);
        for (BalanceChangeInfo balanceChangeInfo : balanceChangeInfoList) {
            String key = balanceChangeInfo.getEpId() + "|" + balanceChangeInfo.getCoreEpId();
            if (capitalMap.containsKey(key)) {
                Capital capital = capitalMap.get(key);
                int newBalance = capital.getBalance() + balanceChangeInfo.getBalance();
                int newCanCash = capital.getCanCash() + balanceChangeInfo.getCanCash();
                // 生成流水记录
                CapitalSerial capitalSerial = new CapitalSerial();
                capitalSerial.setCapitalId(capital.getId());
                capitalSerial.setRefId(serialNum);
                capitalSerial.setRefType(type);
                capitalSerial.setOldBalance(capital.getBalance());
                capitalSerial.setOldCanCash(capital.getCanCash());
                capitalSerial.setNewBalance(newBalance);
                capitalSerial.setNewCanCash(newCanCash);
                capitalSerials.add(capitalSerial);

                capital.setBalance(newBalance);
                capital.setCanCash(newCanCash);
            } else {
                BalanceChangeRsp changeRsp = new BalanceChangeRsp(balanceChangeInfo.getEpId(), balanceChangeInfo
                        .getCoreEpId(), null);
                throw new BusinessException("余额账号不存在", changeRsp);
            }
        }
        for (Capital capital : capitalMap.values()) {
            if (capital.getBalance() + capital.getCredit() < 0) {
                BalanceChangeRsp changeRsp = new BalanceChangeRsp(capital.getEpId(), capital.getCoreEpId(), null);
                throw new BusinessException("余额不足", changeRsp);
            }
        }
        return capitalSerials;
    }

    private Map<String, Capital> convertCapitalList(List<Capital> capitals) {
        Map<String, Capital> capitalMap = new HashMap<>();
        for (Capital capital : capitals) {
            capitalMap.put(capital.getEpId() + "|" + capital.getCoreEpId(), capital);
        }
        return capitalMap;
    }

    /**
     * 记录余额变更流水
     */
    private void recodeSerial(List<CapitalSerial> capitalSerialList) {
        capitalSerialMapper.insertBatch(capitalSerialList);
    }

    /**
     * 变更各账户的余额
     */
    private void changeBalances(List<Capital> capitalList) {
        capitalMapper.batchUpdateById(capitalList);
    }

    /**
     * 发布余额变更事件
     */
    private void fireBalanceChangedEvent(List<Capital> capitals) {
        logger.info("余额变更事件----->开始");
        String tag = "core";
        Map<String, Object> data = new HashMap<>();
        data.put("action", PaymentConstant.EVENT_NAME_BALANCE_CHANGE);
        data.put("createTime", DateFormatUtils.converToStringDate(new Date()));
        data.put("content", capitals);
        topicPushManager.setAccessId(mnsAccessId);
        topicPushManager.setAccessKey(mnsAccessKey);
        topicPushManager.setAccountEndpoint(mnsAccountEndpoint);
        topicPushManager.pushAsync(topicName, tag, JsonUtils.toJson(data));
        logger.info("余额变更事件----->成功");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result createBalanceAccount(Integer epId, Integer coreEpId) {
        Result result = new Result();
        Capital capital = new Capital();
        capital.setEpId(epId);
        capital.setCoreEpId(coreEpId);
        capitalMapper.insertSelective(capital);
        result.setSuccess();
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result setCredit(Integer epId, Integer coreEpId, Integer credit) {
        Result result = new Result();
        try {
            Capital capital = new Capital();
            capital.setEpId(epId);
            capital.setCoreEpId(coreEpId);
            capital.setCredit(credit);
            capitalMapper.updateByEpIdAndCoreEpId(capital);
            result.setSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setFail();
            result.setError(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId) {
        Result<Map<String, String>> result = new Result<>();
        Capital capital = capitalMapper.selectByEpIdAndCoreEpId(epId, coreEpId);
        try {
            Map<String, String> map = BeanUtils.describe(capital);
            Map<String, Object> map1 = PropertyUtils.describe(capital);
            System.out.println(JsonUtils.toJson(map1));
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError("转换出错：Capital -> Map");
        }
        return result;
    }

    @Override
    public Result<List<Map<String, String>>> getBalanceList(List<Integer> epIdList, Integer coreEpId) {
        Result<List<Map<String, String>>> result = new Result<>();
        if (epIdList.size() > 100) {
            epIdList = epIdList.subList(0, 100);
        }
        List<Map<String, String>> list = capitalMapper.listByEpIdAndCoreEpId(epIdList, coreEpId);
        result.setSuccess();
        result.put(list);
        return result;
    }

    @Override
    public Result<List<Map<String, String>>> getBalanceSerialList(Integer epId, Integer coreEpId, int startRecord, int maxRecords) {
        Result<List<Map<String, String>>> result = new Result<>();
        Capital capital = capitalMapper.selectByEpIdAndCoreEpId(epId, coreEpId);
        if (capital == null) {
            result.setFail();
            result.setError("余额账户不存在。");
            logger.error(MessageFormat.format("余额账户不存在，epId={0},coreEpId={1}", epId, coreEpId));
        } else {
            List<Map<String, String>> capitalSerials = capitalSerialMapper.listByCapitalId(capital.getId(),
                    startRecord, maxRecords);
            result.setSuccess();
            result.put(capitalSerials);
        }
        return result;
    }
}
