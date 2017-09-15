package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.*;
import com.all580.voucherplatform.utils.UrlUtils;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.all580.voucherplatform.utils.voucher.VoucherGenerate;
import com.all580.voucherplatform.utils.voucher.VoucherUrlGenerate;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private SupplyMapper supplyMapper;
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
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;


    private PlatformProduct platformProduct;
    private SupplyProduct supplyProduct;
    private String dataUrl = null;
    private GroupOrder groupOrder;
    private List<GroupVisitor> visitorList;
    private QrRule qrRule;
    private Template template;
    private Supply supply;

    public void setProd(Integer platformId,
                        Map map) throws Exception {

        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        groupOrder = new GroupOrder();
        groupOrder.setPlatformOrderId(orderId);
        if (orderMapper.selectByPlatform(platformId, orderId) != null) {
            log.warn("订单号为{}的订单已存在", orderId);
            throw new Exception("订单已处理！");
        }
        groupOrder.setFormAreaCode(CommonUtil.objectParseString(map.get("formAreaCode")));
        groupOrder.setFormAddr(StringUtils.isEmpty(groupOrder.getManager())?groupOrder.getFormAreaCode():groupOrder.getFormAddr());
        groupOrder.setTravelName(CommonUtil.objectParseString(map.get("travelName")));
        groupOrder.setManager(StringUtils.isEmpty(groupOrder.getManager())?"经理人":groupOrder.getManager());
        groupOrder.setGroupNumber(CommonUtil.objectParseString(map.get("groupNumber")));
        groupOrder.setGuideName(CommonUtil.objectParseString(map.get("guideName")));
        groupOrder.setGuideMobile(CommonUtil.objectParseString(map.get("guideMobile")));
        groupOrder.setGuideIdNumber(CommonUtil.objectParseString(map.get("guideIdNumber")));
        groupOrder.setPayment(CommonUtil.objectParseInteger(map.get("payment"), 0));
        if (map.containsKey("payTime")) {
            groupOrder.setPayTime(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("payTime"))));
        }
        groupOrder.setSendType(CommonUtil.objectParseInteger(map.get("sendType"), 0));

        groupOrder.setValidTime(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("validTime"))));
        groupOrder.setInvalidTime(
                DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("invalidTime"))));

        Map mapProd = (Map) map.get("products");
        String prodId = CommonUtil.objectParseString(mapProd.get("productId"));
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
        groupOrder.setPlatformProdId(platformProduct.getId());
        groupOrder.setSupply_id(platformProduct.getSupply_id());

        supply = supplyMapper.selectByPrimaryKey(platformProduct.getSupply_id());
        groupOrder.setTicketsys_id(supply.getTicketsys_id());

        if (supplyProduct != null) {
            groupOrder.setSupplyProdId(supplyProduct.getId());
        }
        groupOrder.setPrice(new BigDecimal(CommonUtil.objectParseString(mapProd.get("price"))));
        groupOrder.setNumber(CommonUtil.objectParseInteger(mapProd.get("number")));
        groupOrder.setCreateTime(new Date());
        String voucherNumber = voucherGenerate.getVoucher(qrRule.getSize(), qrRule.getPrefix(), qrRule.getPostfix());
        groupOrder.setVoucherNumber(voucherNumber);
        String voucherImgUrl = voucherUrlGenerate.getVoucherUrl(voucherNumber, qrRule.getErrorRate(), qrRule.getSize(),
                qrRule.getForeColor());
        groupOrder.setLongUrl(voucherImgUrl);
        voucherImgUrl= UrlUtils.shortUri(voucherImgUrl);
        groupOrder.setImgUrl(voucherImgUrl);
        groupOrder.setActivateStatus(false);
        groupOrder.setStatus(VoucherConstant.OrderSyncStatus.WAIT_SYNC);
        setVisitor(map);
    }

    private void loadTemplate(Integer supplyId,
                              Integer supplyProdId) {
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

    private void loadQrRule(Integer supplyId,
                            Integer supplyProdId) {
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
        if (!StringUtils.isEmpty(dataUrl)) {
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

    public void submit() {
        Integer groupOrderId = saveOrder();
        notifySupply(groupOrder.getTicketsys_id(), groupOrder.getId());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private Integer saveOrder() {
        orderMapper.insertSelective(groupOrder);

        for (GroupVisitor groupVisitor : visitorList) {
            groupVisitor.setGroup_order_id(groupOrder.getId());
            groupVisitorMapper.insertSelective(groupVisitor);
        }
        return groupOrder.getId();
    }

    private void notifySupply(final Integer ticketSysId,
                              final Integer groupOrderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(ticketSysId);
                if (supplyAdapterService != null) {
                    supplyAdapterService.sendGroupOrder(groupOrderId);
                }
            }
        });
    }


}
