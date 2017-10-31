package com.all580.order.service;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.service.OrderService;
import com.all580.order.dao.*;
import com.all580.order.entity.MaSendResponse;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.order.service.event.BaseNotifyEvent;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.synchronize.SyncAccess;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.lang.exception.ApiException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/10/22 17:32
 */
@Service
@Slf4j
public class OrderServiceImpl extends BasicSyncDataEvent implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private EpService epService;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private BaseNotifyEvent baseNotifyEvent;

    @Value("${order.pay.timeout}")
    private Integer payTimeOut;
    @Value("${order.audit.timeout}")
    private Integer auditTimeOut;

    @Override
    public Result<Integer> getPayeeEpIdByOutTransId(String outTranId) {
        Order order = orderMapper.selectByThirdSn(outTranId);
        if (order == null) {
            return new Result<>(false, "订单不存在");
        }
        Result<Integer> result = new Result<>(true);
        result.put(order.getPayee_ep_id());
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectPlatformOrderBySupplierCore(Integer coreEpId, Date startTime, Date endTime, Integer orderStatus, Integer orderItemStatus, String phone, Long orderItemNumber, Integer recordStart, Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = orderItemMapper.selectPlatformBySupplierCoreEpIdCount(coreEpId, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNumber);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = orderItemMapper.selectPlatformBySupplierCoreEpId(coreEpId, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNumber, recordStart, recordCount);
            record.setList(list);
        } else {
            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectBySupplierPlatform(Integer coreEpId, Integer saleCoreEpId, Integer dateType, Date startTime, Date endTime, Integer orderStatus, Integer orderItemStatus, String phone, Long orderItemNum, Boolean self, Long productSubNumber, Integer recordStart, Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = orderItemMapper.selectBySupplierPlatformCount(coreEpId, saleCoreEpId, dateType, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNum, self, productSubNumber);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = orderItemMapper.selectBySupplierPlatform(coreEpId, saleCoreEpId, dateType, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNum, self, productSubNumber, recordStart, recordCount);
            record.setList(list);
        } else {
            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result<List<Map>> selectChannelBill(Integer coreEpId, Date start, Date end, Boolean settled) {
        List<Map> list = orderClearanceSerialMapper.selectChannelBill(coreEpId, start, end, settled);
        setEpNameForList(list);
        Result<List<Map>> result = new Result<>(true);
        result.put(list);
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectChannelBillDetail(Integer coreEpId, Integer saleCoreEpId, Integer month, Integer recordStart, Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = orderClearanceSerialMapper.selectChannelBillDetailCount(coreEpId, saleCoreEpId, month);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = orderClearanceSerialMapper.selectChannelBillDetail(coreEpId, saleCoreEpId, month, recordStart, recordCount);
            record.setList(list);
        } else {
            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result<List<Map>> selectChannelBillForSupplier(Integer coreEpId, Date start, Date end, Boolean settled) {
        List<Map> list = orderClearanceSerialMapper.selectChannelBillForSupplier(coreEpId, start, end, settled);
        setEpNameForList(list);
        Result<List<Map>> result = new Result<>(true);
        result.put(list);
        return result;
    }

    private void setEpNameForList(List<Map> list) {
        if (list != null) {
            Set<Integer> epIds = new HashSet<>();
            for (Map map : list) {
                Integer epId = CommonUtil.objectParseInteger(map.get("supplier_core_ep_id"));
                if (epId != null) {
                    epIds.add(epId);
                }
                epId = CommonUtil.objectParseInteger(map.get("sale_core_ep_id"));
                if (epId != null) {
                    epIds.add(epId);
                }
            }
            if (epIds.size() > 0) {
                Result<List<Map<String,Object>>> epResult = epService.getEp(epIds.toArray(new Integer[epIds.size()]), new String[]{"id", "name"});
                if (epResult != null && epResult.isSuccess()) {
                    setEpName(list, epResult.get());
                } else {
                    log.warn("获取企业:{}名称异常:{}", JsonUtils.toJson(epIds), epResult == null ? "null" : epResult.getError());
                }
            }
        }
    }

    private void setEpName(List<Map> list, List<Map<String, Object>> epList) {
        if (epList != null) {
            for (Map<String, Object> epMap : epList) {
                Integer id = CommonUtil.objectParseInteger(epMap.get("id"));
                for (Map map : list) {
                    Integer epId = CommonUtil.objectParseInteger(map.get("supplier_core_ep_id"));
                    if (epId != null && epId.equals(id)) {
                        map.put("supplier_core_ep_name", CommonUtil.objectParseString(epMap.get("name")));
                    }
                    epId = CommonUtil.objectParseInteger(map.get("sale_core_ep_id"));
                    if (epId != null && epId.equals(id)) {
                        map.put("sale_core_ep_name", CommonUtil.objectParseString(epMap.get("name")));
                    }
                }
            }
        }
    }

    @Override
    public Result settledChannelBill(Map params) {

        return new Result(true);
    }

    @Override
    public Result<Integer[]> getCancelTimeout() {
        Result<Integer[]> result = new Result<>(true);
        result.put(new Integer[]{auditTimeOut == null ? 0 : auditTimeOut, payTimeOut == null ? 0 : payTimeOut});
        return result;
    }

    /**
     * 同步订单数据
     * @param number     订单号
     * @param accessKeys 同步运营平台(可选)
     * @param tables 同步的表(可选)
     * @return
     */
    @Override
    public Result<?> syncOrder(long number, String[] accessKeys, String[] tables) {
        Order order = orderMapper.selectBySN(number);
        Assert.notNull(order, "订单不存在");
        SyncAccess syncAccess = getAccessKeys(order);
        bookingOrderManager.addAllOrderTableSync(syncAccess, order, accessKeys, tables);
        sync(syncAccess.getDataMaps());
        return new Result<>(true);
    }

    @Override
    public Result<?> selectOrderItemInfoByOta(long number) {
        Result<Map> result = new Result<>(true);
        Map map = orderItemMapper.selectInfoForOtaBySn(number);
        result.put(map);
        return result;
    }

    @Override
    public Result<Map> selectOrderItemByNumber(long number) {
        Result<Map> result = new Result<>(true);
        OrderItem orderItem = orderItemMapper.selectBySN(number);
        result.put(JsonUtils.obj2map(orderItem));
        return result;
    }

    @Override
    public Result<?> heartbeat() {
        return new Result<>(true);
    }

    /**
     * view sms content
     *
     * @param number  item number
     * @param visitor visitor id(optional)
     * @return
     */
    @Override
    public Result<?> viewVoucherSms(long number, Integer visitor) {
        OrderItem orderItem = orderItemMapper.selectBySN(number);
        Assert.notNull(orderItem, "订单不存在");
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (StringUtils.isEmpty(orderItem.getVoucher_msg())) throw new ApiException("该订单没有短信模板");
        boolean isGroup = orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0;
        if (orderItem.getPro_type() == ProductConstants.ProductType.SCENERY && (visitor == null || (visitor == 0 && !isGroup))) throw new ApiException("请输入要查看的游客");
        MaSendResponse maSendResponse = null;
        Visitor v = null;
        if (visitor != null && (visitor > 0 || isGroup)) {
            maSendResponse = maSendResponseMapper.selectByVisitorId(orderItem.getId(), visitor, orderItem.getEp_ma_id());
        }
        if (visitor != null && visitor > 0) {
            List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
            v = smsManager.getVisitor(visitor, visitors);
            Assert.notNull(v, "游客不存在");
        }
        Map<String, String> params = smsManager.parseParams(orderItem.getVoucher_msg(), order, orderItem, maSendResponse, v == null ? orderItem.getQuantity() : v.getQuantity());
        Matcher matcher = SmsManager.pattern.matcher(orderItem.getVoucher_msg());
        String res = orderItem.getVoucher_msg();
        while (matcher.find()) {
            String key = matcher.group();
            String val = key.substring(key.indexOf("${") + 2, key.length() - 1);
            if (params.containsKey(val)) {
                res = res.replace(key, params.get(val));
            }
        }
        Result<String> result = new Result<>(true);
        result.put(res);
        return result;
    }

    /**
     * 查詢推送内容
     *
     * @param number
     * @return
     */
    @Override
    public Result<?> queryPushContent(long number, String opCode, Map params) {
        Map data = baseNotifyEvent.packaging(number, opCode, params);
        Result<Map> result = new Result<>(true);
        result.put(data);
        return result;
    }
}
