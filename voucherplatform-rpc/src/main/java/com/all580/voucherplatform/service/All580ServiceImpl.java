package com.all580.voucherplatform.service;

import com.all580.order.api.OrderConstant;
import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.service.All580Service;
import com.all580.voucherplatform.dao.PlatformMapper;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.OrderLogManager;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Slf4j
public class All580ServiceImpl implements All580Service {

    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private SupplyMapper supplyMapper;
    @Autowired
    private SignInstance signInstance;

    @Autowired
    private OrderLogManager orderLogManager;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result process(Map map) {
        String action = CommonUtil.objectParseString(map.get("action"));
        Integer identity = CommonUtil.objectParseInteger(map.get("identity"));
        String signed = CommonUtil.objectParseString(map.get("signed"));
        String content = CommonUtil.objectParseString(map.get("content"));
        Map mapContent = getMapFormContent(content);
        log.info("接收小秘书 identity={},action={},content={},signed={}", new Object[]{identity, action, content, signed});
        addLogReceiveContent(action, mapContent);
        Platform platform = platformMapper.selectByPrimaryKey(identity);
        if (platform == null) {
            return new Result(false, "身份数据校检失败");
        }
//        else if (!signInstance.checkSign(platform.getSignType(), platform.getPublicKey(), platform.getPrivateKey(),
//                content, signed)) {
//            throw new ApiException("签名数据校检失败 voucherplatform");
//            //return new Result(false, "签名数据校检失败");
//        }
        PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platform);
        return platformAdapterService.process(action, platform, mapContent);
    }

    private void addLogReceiveContent(String action, Map mapContent) {
        String itemId = "";
        Integer qty = 0;
        String sn = null;
        String code = "";
        String name="小秘书";
        try {
            switch (action) {
                case "sendTicket":
                    itemId = CommonUtil.objectParseString(mapContent.get("orderId"));
                    List<Map<String, Object>> visitors = (List<Map<String, Object>>) mapContent.get("visitors");
                    for (Map temp : visitors) {
                        qty += CommonUtil.objectParseInteger(temp.get("number"));
                    }
                    code = OrderConstant.LogOperateCode.RECEIVE_XIAOMISHU;
                    break;
                case "sendGroupTicket":
                    itemId = CommonUtil.objectParseString(mapContent.get("orderId"));
                    Map<String, Object> products = (Map<String, Object>) mapContent.get("products");
                    qty = CommonUtil.objectParseInteger(products.get("number"));
                    code = OrderConstant.LogOperateCode.RECEIVE_XIAOMISHU;
                    break;
                case "cancelTicket":
                case "cancelGroupTicket":
                    itemId = CommonUtil.objectParseString(mapContent.get("orderId"));
                    qty = CommonUtil.objectParseInteger(mapContent.get("refNumber"));
                    sn = CommonUtil.objectParseString(mapContent.get("refId"));
                    code = OrderConstant.LogOperateCode.RECEIVE_XIAOMISHU;
                    break;
                case "updateGroupTicket":
                    itemId = CommonUtil.objectParseString(mapContent.get("orderId"));
                    qty = null;
                    sn = null;
                    code = OrderConstant.LogOperateCode.RECEIVE_XIAOMISHU;
                    break;
                case "saveOrderRsp":
                    List<Map> mapList = (List<Map>) mapContent.get("orders");
                    itemId = orderLogManager.getOrderId(CommonUtil.objectParseString(mapList.get(0).get("voucherId")));
                    qty = null;
                    sn = null;
                    code = OrderConstant.LogOperateCode.RECEIVE_TICKET;
                    name="票务";
                    break;
                case "sendGroupOrderRsp":
                    name="票务";
                    itemId = orderLogManager.getOrderId(CommonUtil.objectParseString(mapContent.get("voucherId")));
                    qty = null;
                    sn = null;
                    code = OrderConstant.LogOperateCode.RECEIVE_TICKET;
                    break;
                case "cancelOrderRsp":
                case "cancelGroupOrderRsp":
                    name="票务";
                    itemId = orderLogManager.getOrderId(CommonUtil.objectParseString(mapContent.get("voucherId")));
                    qty = CommonUtil.objectParseInteger(mapContent.get("refNumber"));
                    sn = CommonUtil.objectParseString(mapContent.get("refId"));
                    code = OrderConstant.LogOperateCode.RECEIVE_TICKET;
                    break;
                case "consumeOrderRsp":
                    name="票务";
                    itemId = orderLogManager.getOrderId(CommonUtil.objectParseString(mapContent.get("voucherId")));
                    qty = CommonUtil.objectParseInteger(mapContent.get("consumeNumber"));
                    sn = CommonUtil.objectParseString(mapContent.get("voucherId"));
                    code = OrderConstant.LogOperateCode.RECEIVE_TICKET;
                    break;
                case "activateGroupOrderRsp":
                    name="票务";
                    itemId = orderLogManager.getOrderId(CommonUtil.objectParseString(mapContent.get("voucherId")));
                    sn = CommonUtil.objectParseString(mapContent.get("voucherId"));
                    qty = CommonUtil.objectParseInteger(mapContent.get("number"));
                    code = OrderConstant.LogOperateCode.RECEIVE_TICKET;
                    break;
                default:
                    return;
            }
        } catch (Exception e) {
            log.error(" 获取接收"+name+"数据参数错误 action{} content{}", action, mapContent);
        }
        log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                itemId,
                0, action, code,
                qty
                , String.format("接收到小秘书:参数:%s", JsonUtils.toJson(mapContent)),
                sn));
    }

    @Override
    public Result supplyProcess(Map map) {
        String action = CommonUtil.objectParseString(map.get("action"));
        Integer identity = CommonUtil.objectParseInteger(map.get("identity"));
        String signed = CommonUtil.objectParseString(map.get("signed"));
        String content = CommonUtil.objectParseString(map.get("content"));
        Map mapContent = getMapFormContent(content);
        log.info("接收到票务 identity={},action={},content={},signed={}", new Object[]{identity, action, content, signed});
        addLogReceiveContent(action, mapContent);
        Supply supply = supplyMapper.selectByPrimaryKey(identity);
        if (supply == null) {
            return new Result(false, "身份数据校检失败");
        }
//        else if (!signInstance.checkSign(supply.getSignType(), supply.getPublicKey(), supply.getPrivateKey(), content,
//                signed)) {
//            // return new Result(false, "签名数据校检失败");     //  签名暂未用到
//        }
        SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
        return supplyAdapterService.process(action, supply, mapContent);
    }

    private Map getMapFormContent(String content) {
        Map mapContent = null;
        if (content.startsWith("[") && content.endsWith("]")) {
            List list = JsonUtils.json2List(content);
            mapContent = new HashMap();
            mapContent.put("data", list);
        } else {
            mapContent = JsonUtils.json2Map(content);
        }
        return mapContent;
    }
}
