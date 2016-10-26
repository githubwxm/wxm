package com.all580.payment.service;


import com.all580.payment.api.service.EpPaymentConfService;
import com.all580.payment.dao.EpPaymentConfMapper;
import com.all580.payment.entity.EpPaymentConf;
import com.framework.common.Result;
import com.framework.common.validate.ValidRule;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("epPaymentConfService")
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class EpPaymentConfServiceImpl implements EpPaymentConfService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EpPaymentConfMapper epPaymentConfMapper;

    @Override
    public Result create(Map<String, Object> map) {
        logger.info("开始 -> 创建企业收款方式配置");
        Result result = new Result();
        try {
            EpPaymentConf conf = new EpPaymentConf();
            BeanUtils.populate(conf, map);
            // 检查同一企业下是否已经存在相同支付类型的配置
            checkExistSameTypeRecord(conf.getCoreEpId(), conf.getPaymentType());
            epPaymentConfMapper.insertSelective(conf);
            result.setSuccess();
            logger.info("完成 -> 创建企业收款方式配置");
        } catch (Exception e) {
            logger.error("失败 -> 创建企业收款方式配置：" + e.getMessage(), e);
            result.setFail();
            result.setError(Result.DB_FAIL, "新增失败" + e.getMessage());
        }
        return result;
    }

    private void checkExistSameTypeRecord(Integer coreEpId, Integer paymentType) {
        System.out.println(coreEpId + "|" + paymentType);
        int count = epPaymentConfMapper.countByEpIdAndType(coreEpId, paymentType);
        if (count > 0) {
            throw new RuntimeException("该企业下已经存在相同类型的支付账号");
        }
    }


    @Override
    public Result update(Map<String, Object> map) {
        Result result = new Result();
        try {
            EpPaymentConf conf = new EpPaymentConf();
            BeanUtils.populate(conf, map);
            // 检查同一企业下是否已经存在相同支付类型的配置
            checkExistSameTypeRecord(conf.getCoreEpId(), conf.getPaymentType());
            epPaymentConfMapper.updateByPrimaryKeySelective(conf);
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "修改失败:" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<Map<String, Object>>> listByEpId(Integer core_ep_id) {
        Result<List<Map<String, Object>>> result = new Result<>();
        try {
            List<Map<String, Object>> conf = epPaymentConfMapper.listByEpId(core_ep_id);
            result.setSuccess();
            result.put(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String[], ValidRule[]> generateCreatePaymentValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "access_id", //
                "payment_type", //
                "conf_data", //
                "status",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "payment_type", // 订单子产品ID
                "status",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

}
