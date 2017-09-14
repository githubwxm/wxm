package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.*;
import com.all580.voucherplatform.utils.UrlUtils;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.all580.voucherplatform.utils.voucher.VoucherGenerate;
import com.all580.voucherplatform.utils.voucher.VoucherUrlGenerate;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class CreateOrderManager {
    @Autowired
    private OrderMapper orderMapper;
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

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;


    private Integer platformId;
    private PlatformProduct platformProduct;
    private Supply supply;
    private SupplyProduct supplyProduct;
    private QrRule qrRule;
    private Template template;
    private String orderId;
    private Integer sendType;
    private Integer voucherType;
    private Integer consumeType;
    private String validTime;
    private String invalidTime;
    private String validWeek;
    private String invalidDate;
    private String payTime;
    private String sms;
    private List<Order> orderList;

    public CreateOrderManager() {
    }

    /**
     * @param platformId
     * @param map
     * @throws Exception
     */
    public void setProd(Integer platformId, Map map) throws Exception {
        this.platformId = platformId;
        orderList = new ArrayList<>();
        orderId = CommonUtil.objectParseString(map.get("orderId"));

        if (orderMapper.selectOrderCount(platformId, null, null, orderId, null, null, null, null, null, null) > 0) {

            log.warn("订单号为{}的订单已存在", orderId);
            throw new Exception("订单已处理！");
        }
        String prodId = CommonUtil.objectParseString(map.get("prodId"));
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
        supply = supplyMapper.selectByPrimaryKey(platformProduct.getSupply_id());

        loadTemplate(platformProduct.getSupplyprod_id(), supplyProduct == null ? null : supplyProduct.getId());
        loadQrRule(platformProduct.getSupplyprod_id(), supplyProduct == null ? null : supplyProduct.getId());


        voucherType = CommonUtil.objectParseInteger(map.get("voucherType"), 0);
        consumeType = CommonUtil.objectParseInteger(map.get("consumeType"), 0);
        validTime = CommonUtil.objectParseString(map.get("validTime"));
        invalidTime = CommonUtil.objectParseString(map.get("invalidTime"));
        validWeek = CommonUtil.objectParseString(map.get("validWeek"));
        invalidDate = CommonUtil.objectParseString(map.get("invalidDate"));
        sendType = CommonUtil.objectParseInteger(map.get("sendType"), VoucherConstant.SmsSendType.YES);
        payTime = CommonUtil.objectParseString(map.get("payTime"));
        sms = CommonUtil.objectParseString(map.get("sms"));
        sms = replaceSmsTemplate();

        if (StringUtils.isEmpty(validWeek)) {
            validWeek = "1111111";
        }
        this.check();
    }


    public void setVisitor(List<Map> maps) {
        for (Map map : maps) {
            setVisitor(map);
        }
    }

    public void setVisitor(Map... maps) {
        for (Map map : maps) {
            String seqId = CommonUtil.objectParseString(map.get("seqId"));
            String customName = CommonUtil.objectParseString(map.get("customName"));
            String mobile = CommonUtil.objectParseString(map.get("mobile"));
            String idNumber = CommonUtil.objectParseString(map.get("idNumber"));
            Integer number = CommonUtil.objectParseInteger(map.get("number"), 1);

            Order order = getOrder(number);
            order.setSeqId(seqId);
            order.setCustomName(customName);
            order.setMobile(mobile);
            order.setIdNumber(idNumber);
            order.setNumber(number);
            order.setStatus(VoucherConstant.OrderSyncStatus.WAIT_SYNC);
            orderList.add(order);
        }
    }

    public void submit() {
        Integer[] orderIdList = saveOrder();
        notifySupply(supply, orderIdList);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private Integer[] saveOrder() {
        Integer[] orderIdList = new Integer[orderList.size()];
        for (int i = 0; i < orderList.size(); i++) {
            orderMapper.insertSelective(orderList.get(i));
            orderIdList[i] = orderList.get(i).getId();
        }
        return orderIdList;
    }

    private void notifySupply(final Supply supply, final Integer[] orderIdList) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
                if (supplyAdapterService != null) {
                    supplyAdapterService.sendOrder(orderIdList);
                }
            }
        });
    }


    private void check() throws Exception {
        long _validTime = DateFormatUtils.converToDateTime(validTime).getTime();
        long _invalidTime = DateFormatUtils.converToDateTime(invalidTime).getTime();
        long nowDate = System.currentTimeMillis();
        if (_invalidTime < nowDate || _invalidTime < _validTime) {
            throw new Exception("时间段错误");
        }
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

    private String replaceSmsTemplate() {
        if (StringUtils.isEmpty(sms)) {
            if (template != null) {
                sms = template.getSms();
            }
        }
        sms = sms.replace("{标题}", platformProduct.getName());
        //.replace("{份数}")
        //.replace("{验证码}", MessageFormat.format("{0}，打开二维码: {1} ", order.getVoucherNumber(), order.getImgUrl()))
        //.replace("{生效日期}", validTime)
        //.replace("{有效年月日}", invalidTime);
        return sms;
    }

//    private String replaceSmsByVisitor(Integer number, String voucherCode, String imgUrl) {
//        String s = new String(sms);
//        return s.replace("{份数}", String.valueOf(number)).replace("{验证码}", MessageFormat.format("{0}，打开二维码: {1} ", voucherCode, imgUrl));
//    }

    private Order getOrder(Integer number) {
        Order order = new Order();
        order.setOrderCode(String.valueOf(UUIDGenerator.generateUUID()));//生成订单号
        order.setPlatformOrderId(orderId);
        order.setPlatform_id(platformId);
        order.setPlatformprod_id(platformProduct.getPlatform_id());
        order.setSupply_id(platformProduct.getSupply_id());
        order.setTicketsys_id(supply.getTicketsys_id());
        if (supplyProduct != null) {
            order.setSupplyProdId(supplyProduct.getId());
        }
        order.setVoucherType(voucherType);
        order.setConsumeType(consumeType);
        order.setValidTime(DateFormatUtils.converToDateTime(validTime));
        order.setInvalidTime(DateFormatUtils.converToDateTime(invalidTime));
        order.setValidWeek(validWeek);
        order.setInvalidDate(invalidDate);
        order.setSendType(sendType);
        order.setCreateTime(new Date());

        String voucherNumber = voucherGenerate.getVoucher(qrRule.getSize(), qrRule.getPrefix(), qrRule.getPostfix());
        order.setVoucherNumber(voucherNumber);
        String voucherImgUrl = voucherUrlGenerate.getVoucherUrl(voucherNumber, qrRule.getErrorRate(), qrRule.getSize(), qrRule.getForeColor());
         order.setLongUrl(voucherImgUrl);
        voucherImgUrl= UrlUtils.shortUri(voucherImgUrl);
        order.setImgUrl(voucherImgUrl);
        order.setSms(sms);
        if (template != null) {
            order.setPrintText(template.getPrintText());
        }
        return order;
    }

    public List<Order> getOrderList() {
        return orderList;
    }
}
