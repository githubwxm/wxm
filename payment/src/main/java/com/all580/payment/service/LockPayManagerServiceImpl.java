package com.all580.payment.service;

import com.all580.order.api.service.PaymentCallbackService;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.LockPayManagerService;
import com.all580.payment.dao.CapitalMapper;
import com.all580.payment.dao.CapitalSerialMapper;
import com.all580.payment.entity.Capital;
import com.all580.payment.entity.CapitalSerial;
import com.all580.payment.exception.BusinessException;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import com.framework.common.event.MnsEventManager;
import com.framework.common.mns.TopicPushManager;
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
 * Created by wxming on 2017/1/10 0010.
 */
@Service("lockPayManagerService")
public class LockPayManagerServiceImpl implements LockPayManagerService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${mns.topic}")
    private String topicName;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Autowired
    private TopicPushManager topicPushManager;
    @Autowired
    private CapitalMapper capitalMapper;

    @Autowired
    private CapitalSerialMapper capitalSerialMapper;

    @Autowired
    private MnsEventAspect eventAspect;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @MnsEvent
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type,final String serialNum) {
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
            List<Map<String,Object>> tempList= new ArrayList<>();
            for(Capital c:capitals){
                Map<String,Object> map = new HashMap();
                map.put("ep_id",c.getEp_id());
                map.put("core_ep_id",c.getCore_ep_id());
                map.put("balance",c.getBalance());
                tempList.add(map);
            }
            eventAspect.addEvent("BALANCE_CHANGE", tempList);
            //fireBalanceChangedEvent(capitals);

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
            result.setError(e.getMessage());
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
                capitalSerial.setBalance_type(balanceChangeInfo.getBalance_type());
                capitalSerial.setCan_cash_type(balanceChangeInfo.getCan_cash_type());
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
}
