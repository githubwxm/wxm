package com.all580.payment.service;


import com.all580.payment.api.service.EpPaymentConfService;
import com.all580.payment.dao.EpPaymentConfMapper;
import com.all580.payment.entity.EpPaymentConf;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.validate.ParamsMapValidate;
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
public class EpPaymentConfServiceImpl implements EpPaymentConfService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EpPaymentConfMapper epPaymentConfMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result create(Map<String, Object> map) {
        logger.info("开始 -> 创建企业收款方式配置");
        Result result = new Result();
        try {
            ParamsMapValidate.validate(map, genValidateOfCreate());
            EpPaymentConf conf = new EpPaymentConf();
            map.put("confData",JsonUtils.toJson(map.get("confData")));
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
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result update(Map<String, Object> map) {
        Result result = new Result();
        try {
            EpPaymentConf conf = new EpPaymentConf();
            map.put("confData",JsonUtils.toJson(map.get("confData")));
            BeanUtils.populate(conf, map);
            // 检查同一企业下是否已经存在相同支付类型的配置    --   修改的话修改本身是已经存在的
            //checkExistSameTypeRecord(conf.getCoreEpId(), conf.getPaymentType());
            epPaymentConfMapper.updateByPrimaryKeySelective(conf);//修改方式不允许修改
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError("修改失败:" + e.getMessage());
        }
        return result;
    }//


    @Override
    public Result selectByPrimaryKey(Integer id) {
        Result result = new Result<>();
        try {
            EpPaymentConf e=  epPaymentConfMapper.selectByPrimaryKey(id);
            result.setSuccess();
            result.put(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result<List<Map<String, String>>> listByEpId(Integer core_ep_id) {
        Result<List<Map<String, String>>> result = new Result<>();
        try {
            List<Map<String, String>> conf = epPaymentConfMapper.listByEpId(core_ep_id);
            result.setSuccess();
            result.put(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String[], ValidRule[]> genValidateOfCreate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "paymentType", // 支付类型
                "confData", // 支付配置
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

}
