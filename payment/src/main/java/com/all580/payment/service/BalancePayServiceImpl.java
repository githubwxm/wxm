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
import com.framework.common.vo.PageRecord;
import org.apache.commons.beanutils.BeanUtils;
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
                        Result rst = paymentCallbackService.payCallback(Long.parseLong(serialNum), serialNum, null);
                        logger.debug("余额支付回调成功：" + rst.isSuccess());
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
            String key = balanceChangeInfo.getEp_id()  + "|" + balanceChangeInfo.getCore_ep_id();
            if (capitalMap.containsKey(key)) {
                Capital capital = capitalMap.get(key);
                int newBalance = capital.getBalance() + balanceChangeInfo.getBalance();
                int newCanCash = capital.getCan_cash() + balanceChangeInfo.getCan_cash();
                // 生成流水记录
                CapitalSerial capitalSerial = new CapitalSerial();
                capitalSerial.setCapital_id(capital.getId());
                capitalSerial.setRef_id(serialNum);
                capitalSerial.setRef_type(type);
                capitalSerial.setOld_balance(capital.getBalance());
                capitalSerial.setOld_can_cash(capital.getCan_cash());
                capitalSerial.setNew_balance(newBalance);
                capitalSerial.setNew_can_cash(newCanCash);
                capitalSerials.add(capitalSerial);

                capital.setBalance(newBalance);
                capital.setCan_cash(newCanCash);
            } else {
                BalanceChangeRsp changeRsp = new BalanceChangeRsp(balanceChangeInfo.getEp_id(), balanceChangeInfo
                        .getCore_ep_id(), null);
                throw new BusinessException("余额账号不存在", changeRsp);
            }
        }
        for (Capital capital : capitalMap.values()) {
            if (capital.getBalance() + capital.getCredit() < 0) {
                BalanceChangeRsp changeRsp = new BalanceChangeRsp(capital.getEp_id(), capital.getCore_ep_id(), null);
                throw new BusinessException("余额不足", changeRsp);
            }
        }
        return capitalSerials;
    }

    private Map<String, Capital> convertCapitalList(List<Capital> capitals) {
        Map<String, Capital> capitalMap = new HashMap<>();
        for (Capital capital : capitals) {
            capitalMap.put(capital.getEp_id() + "|" + capital.getCore_ep_id(), capital);
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
        topicPushManager.asyncFireEvent(topicName, tag, PaymentConstant.EVENT_NAME_BALANCE_CHANGE, capitals);
        logger.info("余额变更事件----->成功");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result createBalanceAccount(Integer epId, Integer coreEpId) {
        Assert.notNull(epId, "参数【epId】不能为空");
        Assert.notNull(coreEpId, "参数【coreEpId】不能为空");
        Result result = new Result();
        Capital capital = new Capital();
        capital.setEp_id(epId);
        capital.setCore_ep_id(coreEpId);
        capitalMapper.insertSelective(capital);
        result.setSuccess();
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result setCredit(Integer epId, Integer coreEpId, Integer credit) {
        Assert.notNull(epId, "参数【epId】不能为空");
        Assert.notNull(coreEpId, "参数【coreEpId】不能为空");
        Assert.notNull(credit, "参数【credit】不能为空");
        Result result = new Result();
        try {
            Capital capital = new Capital();
            capital.setEp_id(epId);
            capital.setCore_ep_id(coreEpId);
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
        Assert.notNull(capital, MessageFormat.format("没有找到余额账户:epId={0}|coreEpId={1}", epId, coreEpId));
        try {
            Map<String, String> map = BeanUtils.describe(capital);
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setFail();
            result.setError("转换出错：Capital -> Map");
        }
        return result;
    }

    @Override
    public Result<List<Map<String, String>>> getBalanceList(List<Integer> epIdList, Integer coreEpId) {
        logger.debug("开始 -> 批量获取余额账户");
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
    public Result<PageRecord<Map<String, String>>> getBalanceSerialList(Integer epId, Integer coreEpId,
                                                                        String balanceSatatus,String startDate,String endDate,String ref_id,
                                                                        Integer export ,    Integer startRecord, Integer maxRecords) {
        Result<PageRecord<Map<String, String>>> result = new Result<>();
        Capital capital = capitalMapper.selectByEpIdAndCoreEpId(epId, coreEpId);
        Assert.notNull(capital, MessageFormat.format("没有找到余额账户:epId={0}|coreEpId={1}", epId, coreEpId));
        List<Map<String, String>> capitalSerials=null;
        if(null==export){
           capitalSerials = capitalSerialMapper.listByCapitalId(capital.getId(),
                    balanceSatatus ,startDate,endDate,ref_id,startRecord, maxRecords);
        }else{
            capitalSerials = capitalSerialMapper.listByCapitalIdExport(capital.getId(),
                    balanceSatatus ,startDate,endDate,ref_id,startRecord, maxRecords);
        }

        int count = capitalSerialMapper.countByCapitalId(capital.getId(), balanceSatatus ,startDate,endDate,ref_id);
        PageRecord<Map<String, String>> record = new PageRecord<>(count, capitalSerials);
        result.setSuccess();
        result.put(record);
        return result;
    }

    @Override
    public Result<Integer> updateSummary(int id,String summary){
        Result<Integer> result = new Result<>();
        int ref= capitalSerialMapper.updateSummary(id,summary);
        result.put(ref);
        result.setSuccess();
        return result;
    }
}
