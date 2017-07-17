package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.QrService;
import com.all580.voucherplatform.dao.QrRuleMapper;
import com.all580.voucherplatform.entity.QrRule;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/17.
 */

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class QrServiceImpl implements QrService {
    @Autowired
    private QrRuleMapper qrRuleMapper;

    @Override
    public Result create(Map map) {
        Result result = new Result(false);
        QrRule qrRole = JsonUtils.map2obj(map, QrRule.class);
        qrRole.setId(null);
        if (qrRuleMapper.getQrRule(qrRole.getSupply_id(), qrRole.getSupplyprod_id()) != null) {
            result.setError("对同一商户和产品不能重复添加模版");
        } else {
            qrRole.setCreateTime(new Date());
            qrRole.setStatus(true);
            qrRole.setDefaultOption(false);
            qrRuleMapper.insertSelective(qrRole);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public Result update(Map map) {
        QrRule qrRole = new QrRule();
        qrRole.setId(CommonUtil.objectParseInteger(map.get("id")));
        qrRole.setName(CommonUtil.objectParseString(map.get("name")));
        qrRole.setLen(CommonUtil.objectParseInteger(map.get("len")));
        qrRole.setSize(CommonUtil.objectParseInteger(map.get("size")));

        qrRole.setPrefix(CommonUtil.objectParseString(map.get("prefix")));
        qrRole.setPostfix(CommonUtil.objectParseString(map.get("postfix")));
        qrRole.setErrorRate(CommonUtil.objectParseString(map.get("errorRate")));
        qrRole.setForeColor(CommonUtil.objectParseString(map.get("foreColor")));
        qrRuleMapper.updateByPrimaryKeySelective(qrRole);
        return new Result(true);
    }

    @Override
    public Result delete(Integer id) {
        QrRule qrRole = new QrRule();
        qrRole.setId(id);
        qrRole.setStatus(false);
        qrRuleMapper.updateByPrimaryKeySelective(qrRole);
        return new Result(true);
    }

    @Override
    public Result get(Integer id) {

        QrRule qrRule = qrRuleMapper.selectByPrimaryKey(id);
        Result result = new Result(true);
        if (qrRule != null) {
            result.put(JsonUtils.obj2map(qrRule));
        }
        return result;
    }

    @Override
    public Result get(Integer supplyId, Integer prodId) {
        QrRule qrRule = qrRuleMapper.getQrRule(supplyId, prodId);
        Result result = new Result(true);
        if (qrRule != null) {
            result.put(JsonUtils.obj2map(qrRule));
        }
        return result;
    }


    @Override
    public Result<PageRecord<Map>> selectQrList(String name, Integer len, String prefix, String postfix, Integer supplyId, Integer prodId, Boolean defaultOption, Integer recordStart, Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = qrRuleMapper.selectQrCount(name, len, prefix, postfix, supplyId, prodId, defaultOption);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(qrRuleMapper.selectQrList(name, len, prefix, postfix, supplyId, prodId, defaultOption, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;

    }
}
