package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.dao.CapitalMapper;
import com.all580.payment.dao.CapitalSerialMapper;
import com.all580.payment.entity.Capital;
import com.all580.payment.entity.CapitalSerial;
import com.all580.payment.exception.BusinessException;
import com.framework.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 余额支付的实现类
 *
 * @author Created by panyi on 2016/9/28.
 */
@Service("balancePayService")
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class BalancePayServiceImpl implements BalancePayService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CapitalSerialMapper capitalSerialMapper;

    @Override
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type, String
            serialNum) {
        logger.info(MessageFormat.format("开始 -> 余额变更：type={0}|serialNum={1}", type.toString(), serialNum));
        Result<BalanceChangeRsp> result = new Result<>();
        try {
            // 批量锁定
            List<Capital> capitals = capitalMapper.selectForUpdateByEpList(balanceChangeInfoList);
            // 检查余额账户
            List<CapitalSerial> capitalSerialList = checkBalanceAccount(balanceChangeInfoList, capitals, type, serialNum);
            // 记录流水
            recodeSerial(capitalSerialList);
            // 变更余额
            changeBalances(capitals);
            // 发布余额变更事件
            fireBalanceChangedEvent();
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
        Assert.notNull(balanceChangeInfoList);
        Assert.notNull(capitals);
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
    private void fireBalanceChangedEvent() {
        // TODO panyi
        logger.info("余额变更事件----->");
    }

    @Override
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
            logger.error(e.getMessage(),e);
            result.setFail();
            result.setError(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId) {
        return null;
    }

    @Override
    public Result<List<Map<String, String>>> getBalanceList(List<Integer> epIdList, Integer coreEpId) {
        return null;
    }

    @Override
    public Result<Map<String, String>> getBalanceSerialList(Integer epId, Integer coreEpId, int startRecord, int maxRecords) {
        return null;
    }
}
