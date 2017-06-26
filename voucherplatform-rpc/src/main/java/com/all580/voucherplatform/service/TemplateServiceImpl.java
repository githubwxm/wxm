package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.TemplateService;
import com.all580.voucherplatform.dao.TemplateMapper;
import com.all580.voucherplatform.entity.Template;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public Result create(Map map) {
        Result result = new Result(false);
        Template template = JsonUtils.map2obj(map, Template.class);
        template.setId(null);
        if (templateMapper.getTemplate(template.getSupply_id(), template.getSupplyprod_id()) != null) {
            result.setError("对同一商户和产品不能重复添加模版");
        } else {
            template.setStatus(true);
            template.setDefaultOption(false);
            templateMapper.insertSelective(template);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public Result update(Map map) {
        Template template = new Template();
        template.setId(CommonUtil.objectParseInteger(map.get("id")));
        template.setSms(CommonUtil.objectParseString(map.get("sms")));
        template.setPrintText(CommonUtil.objectParseString(map.get("printText")));
        templateMapper.updateByPrimaryKeySelective(template);
        return new Result(true);
    }

    @Override
    public Result delete(Integer id) {
        Template template = new Template();
        template.setId(id);
        template.setStatus(false);
        templateMapper.updateByPrimaryKeySelective(template);
        return new Result(true);
    }

    @Override
    public Result get(Integer id) {
        Template template = templateMapper.selectByPrimaryKey(id);
        Result result = new Result(true);
        if (template != null) {
            result.put(JsonUtils.obj2map(template));
        }
        return result;
    }

    @Override
    public Result get(Integer supplierId, Integer prodId) {
        Template template = templateMapper.getTemplate(supplierId, prodId);
        Result result = new Result(true);
        if (template != null) {
            result.put(JsonUtils.obj2map(template));
        }
        return result;
    }


    @Override
    public int getCount(String name, Integer supplyId, Integer prodId, Boolean defaultOption) {
        return templateMapper.getCount(name, supplyId, prodId, defaultOption);
    }

    @Override
    public List<Map> getList(String name, Integer supplyId, Integer prodId, Boolean defaultOption, Integer recordStart, Integer recordCount) {
        return templateMapper.getList(name, supplyId, prodId, defaultOption, recordStart, recordCount);
    }
}