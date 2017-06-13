package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.*;
import com.all580.voucherplatform.utils.sign.voucher.VoucherGenerate;
import com.all580.voucherplatform.utils.sign.voucher.VoucherUrlGenerate;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-24.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class CreateGroupOrderManager {
    @Autowired
    private GroupOrderMapper orderMapper;
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;
    @Autowired
    private PlatformProductMapper prodMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;
    @Autowired
    private QrRuleMapper qrRuleMapper;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private VoucherGenerate voucherGenerate;
    @Autowired
    private VoucherUrlGenerate voucherUrlGenerate;


    private PlatformProduct platformProduct;
    private SupplyProduct supplyProduct;
    private String dataUrl = null;
    private GroupOrder groupOrder;
    private List<GroupVisitor> visitorList;
    private QrRule qrRule;
    private Template template;

    public void setProd(Integer platformId, Map map) throws Exception {

        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        groupOrder = new GroupOrder();
        groupOrder.setPlatformOrderId(orderId);
        if (orderMapper.getOrderCount(platformId, null, null, orderId, null, null, null, null, null, null) > 0) {
            log.warn("订单号为{}的订单已存在", orderId);
            throw new Exception("订单已处理！");
        }
        groupOrder.setFormAreaCode(CommonUtil.objectParseString(map.get("formAreaCode")));
        groupOrder.setFormAreaCode(CommonUtil.objectParseString(map.get("formAddr")));
        groupOrder.setTravelName(CommonUtil.objectParseString(map.get("travelName")));
        groupOrder.setManager(CommonUtil.objectParseString(map.get("manager")));
        groupOrder.setGroupNumber(CommonUtil.objectParseString(map.get("groupNumber")));
        groupOrder.setGuideName(CommonUtil.objectParseString(map.get("guideName")));
        groupOrder.setGuideMobile(CommonUtil.objectParseString(map.get("guideName")));
        groupOrder.setGuideIdNumber(CommonUtil.objectParseString(map.get("guideIdNumber")));
        groupOrder.setPayment(CommonUtil.objectParseInteger(map.get("payment"), 0));
        if (map.containsKey("payTime")) {
            groupOrder.setPayTime(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("payTime"))));
        }
        groupOrder.setSendType(CommonUtil.objectParseInteger(map.get("sendType"), 0));

        groupOrder.setValidTime(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("validTime"))));
        groupOrder.setInvalidTime(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("invalidTime"))));

        Map mapProd = (Map) map.get("products");
        String prodId = CommonUtil.objectParseString(mapProd.get("prodId"));
        platformProduct = prodMapper.getProdByPlatform(platformId, null, prodId);
        if (platformProduct == null) {
            log.error("产品号为{}的产品不存在，请检查是否导入，订单号{}", prodId, orderId);
            throw new Exception("订单产品不存在！");
        }
        if (platformProduct.getSupplyprod_id() != null && !platformProduct.getSupplyprod_id().equals(0)) {
            supplyProduct = supplyProductMapper.selectByPrimaryKey(platformProduct.getSupplyprod_id());
        } else {
            log.warn("订单号为{}的票务产品未配置", orderId);
        }

        loadTemplate(platformProduct.getSupplyprod_id(), supplyProduct == null ? null : supplyProduct.getId());
        loadQrRule(platformProduct.getSupplyprod_id(), supplyProduct == null ? null : supplyProduct.getId());
        groupOrder.setOrderCode(String.valueOf(UUIDGenerator.generateUUID()));//生成订单号
        groupOrder.setPlatform_id(platformId);
        groupOrder.setPlatformOrderId(orderId);
        groupOrder.setPlatformprod_id(platformProduct.getPlatform_id());
        groupOrder.setSupply_id(platformProduct.getSupply_id());
        groupOrder.setTicketsys_id(null);
        if (supplyProduct != null) {
            groupOrder.setSupplyProdId(supplyProduct.getId());
        }
        groupOrder.setPrice(BigDecimal.valueOf(Long.valueOf(CommonUtil.objectParseString(mapProd.get("price")))));
        groupOrder.setNumber(CommonUtil.objectParseInteger(mapProd.get("number")));
        groupOrder.setCreateTime(new Date());
        String voucherNumber = voucherGenerate.getVoucher(qrRule.getSize(), qrRule.getPrefix(), qrRule.getPostfix());
        groupOrder.setVoucherNumber(voucherNumber);
        String voucherImgUrl = voucherUrlGenerate.getVoucherUrl(voucherNumber, qrRule.getErrorRate(), qrRule.getSize(), qrRule.getForeColor());
        groupOrder.setImgUrl(voucherImgUrl);
        setVisitor(map);
    }

    private void loadTemplate(Integer supplyId, Integer supplyProdId) {
        if (supplyProdId != null) {
            template = templateMapper.getTemplate(null, supplyProdId);//根据产品读取当前配置
        }
        if (template == null && supplyId != null) {
            template = templateMapper.getTemplate(supplyId, null);//根据商户读取当前配置
        }
        if (template == null) {
            template = templateMapper.getDefaultTemplate();//读取默认配置
        }
        if (template == null) {
            log.error("supplyId={},supplyProdId={}，短信模板不存在", supplyId, supplyProdId);
        }
    }

    private void loadQrRule(Integer supplyId, Integer supplyProdId) {
        if (supplyProdId != null) {
            qrRule = qrRuleMapper.getQrRule(null, supplyProdId);//根据产品读取当前配置
        }
        if (qrRule == null & supplyId != null) {
            qrRule = qrRuleMapper.getQrRule(supplyId, null);//根据商户读取当前配置
        }
        if (qrRule == null) {
            qrRule = qrRuleMapper.getDefaultQrRule(); //读取默认配置
        }
        if (qrRule == null) {
            log.error("supplyId={},supplyProdId={}，二维码模板不存在", supplyId, supplyProdId);
        }
    }

    private void setVisitor(Map map) {
        this.visitorList = new ArrayList<>();
        dataUrl = CommonUtil.objectParseString(map.get("dataUrl"));
        List<Map> visitorList = null;
        if (StringUtils.isEmpty(dataUrl)) {
            String content = HttpUtils.get(dataUrl, null, "utf-8");
            visitorList = JsonUtils.json2List(content);
        } else {
            visitorList = (List<Map>) map.get("visitors");
        }
        for (Map m : visitorList) {
            String seqId = CommonUtil.objectParseString(m.get("seqId"));
            String customName = CommonUtil.objectParseString(m.get("customName"));
            String mobile = CommonUtil.objectParseString(m.get("mobile"));
            String idType = CommonUtil.objectParseString(m.get("idType"));
            String idNumber = CommonUtil.objectParseString(m.get("idNumber"));
            GroupVisitor groupVisitor = new GroupVisitor();
            groupVisitor.setSeqId(seqId);
            groupVisitor.setCustomName(customName);
            groupVisitor.setMobile(mobile);
            groupVisitor.setIdType(idType);
            groupVisitor.setIdNumber(idNumber);
            this.visitorList.add(groupVisitor);
        }
    }

    public void saveOrder() {
        Integer groupId = orderMapper.insertSelective(groupOrder);
        for (GroupVisitor groupVisitor : visitorList) {
            groupVisitor.setGroup_order_id(groupId);
            groupVisitorMapper.insertSelective(groupVisitor);
        }
    }


}
