package com.all580.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemAccountMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemAccount;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/8 19:07
 */
@Component
@Slf4j
public class BaseOrderManager {
    @Autowired
    private EpService epService;
    @Autowired
    private BalancePayService balancePayService;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CoreEpChannelService coreEpChannelService;
    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    /**
     * 获取卖家企业ID(null则返回自己)
     * @param salesInfoList 销售链
     * @param epId 企业
     * @return
     */
    public Integer getSaleEpId(List<EpSalesInfo> salesInfoList, int epId) {
        for (EpSalesInfo info : salesInfoList) {
            if (info.getEp_id() == epId) {
                return info.getSale_ep_id();
            }
        }
        return epId;
    }

    /**
     * 获取进货结构
     * @param epSalesInfos 销售链
     * @param buyEpId 进货企业ID
     * @return
     */
    public EpSalesInfo getBuyingPrice(List<EpSalesInfo> epSalesInfos, Integer buyEpId) {
        for (EpSalesInfo info : epSalesInfos) {
            if (info.getEp_id() == buyEpId.intValue()) {
                return info;
            }
        }
        return null;
    }

    /**
     * 判断企业状态
     * @param result 企业状态返回
     * @param status 需要的状态
     * @return
     */
    public boolean isEpStatus(Result<Integer> result, int status) {
        if (result != null && result.get() == status) {
            return true;
        }
        return false;
    }
    /**
     * 判断企业类型
     * @param result 企业状类型返回
     * @param type 需要的类型
     * @return
     */
    public boolean isEpType(Result<Integer> result, int type) {
        if (result != null && result.get() == type) {
            return true;
        }
        return false;
    }

    /**
     * 获取平台商ID
     * @param result
     * @return
     */
    public Integer getCoreEpId(Result<Integer> result) {
        if (result == null || result.isNULL()) {
            return null;
        }
        return result.get();
    }

    public Result<Integer> getCoreEpId(int epId) {
        return epService.selectPlatformId(epId);
    }

    public Map<Integer, Integer> getCoreEpIds(Collection<Integer> ids) {
        Result<Map<Integer, Integer>> result = epService.selectCoreEpId(ids);
        if (result == null || result.isNULL()) {
            return null;
        }
        return result.get();
    }

    public Integer getCoreEp(Map<Integer, Integer> coreEpMap, Integer epId) {
        Integer buyCoreEpId = coreEpMap.get(epId);
        if (buyCoreEpId == null) {
            buyCoreEpId = getCoreEpId(getCoreEpId(epId));
            if (buyCoreEpId == null) {
                throw new ApiException("企业平台商不存在");
            }
            coreEpMap.put(epId, buyCoreEpId);
        }
        return buyCoreEpId;
    }

    /**
     * 把子订单转换为产品参数
     * @param orderItem 子订单
     * @return
     */
    public ProductSearchParams parseParams(OrderItem orderItem) {
        ProductSearchParams params = new ProductSearchParams();
        params.setSubProductCode(orderItem.getPro_sub_number());
        params.setStartDate(orderItem.getStart());
        params.setDays(orderItem.getDays());
        params.setQuantity(orderItem.getQuantity());
        params.setSubOrderId(orderItem.getId());
        return params;
    }

    public Job createJob(String action, Map<String, String> params, boolean once) {
        Job job = new Job();
        job.setTaskId("ORDER-JOB-" + UUIDGenerator.getUUID());
        job.setExtParams(params);
        job.setParam("$ACTION$", action);
        job.setTaskTrackerNodeGroup(taskTracker);
        if (!once && maxRetryTimes != null) {
            job.setMaxRetryTimes(maxRetryTimes);
        }
        job.setNeedFeedback(false);
        return job;
    }

    /**
     * 余额变动
     * @param type 变动类型
     * @param sn 流水
     * @param infos 账户
     * @return
     */
    public Result<BalanceChangeRsp> changeBalances(int type, String sn, BalanceChangeInfo... infos) {
        return changeBalances(type, sn, Arrays.asList(infos));
    }

    /**
     * 余额变动
     * @param type 变动类型
     * @param sn 流水
     * @param infos 账户
     * @return
     */
    public Result<BalanceChangeRsp> changeBalances(int type, String sn, List<BalanceChangeInfo> infos) {
        return balancePayService.changeBalances(infos, type, sn);
    }

    /**
     * 获取某天详情
     * @param daysData 天数据
     * @param day 某一天
     * @return
     */
    public JSONObject getAccountDataByDay(JSONArray daysData, String day) {
        if (daysData == null || day == null) {
            return null;
        }
        for (Object o : daysData) {
            JSONObject json = (JSONObject) o;
            String d = json.getString("day");
            if (day.equals(d)) {
                return json;
            }
        }
        return null;
    }

    /**
     * 根据每天的利润获取消费金额
     * @param consumeQuantity 消费张数
     * @param dayData 消费日期利润
     * @return
     */
    private int getMoney(int consumeQuantity, JSONObject dayData) {
        int money = 0;
        // 平台内部分账->利润
        int profit = dayData.getIntValue("profit");
        money = consumeQuantity * profit;
        return money;
    }

    /**
     * 获取某天消费利润金额
     * @param orderItemId 子订单ID
     * @param epId 企业ID
     * @param coreEpId 平台商ID
     * @param day 天
     * @param consumeQuantity 消费张数
     * @return
     */
    public int getOutPriceForEp(int orderItemId, int epId, int coreEpId, Date day, int consumeQuantity) {
        int money = 0;
        OrderItemAccount account = orderItemAccountMapper.selectByOrderItemAndEp(orderItemId, epId, coreEpId);
        if (account != null && day != null) {
            JSONArray daysData = JSONArray.parseArray(account.getData());
            // 获取核销日期的单价利润
            JSONObject dayData = getAccountDataByDay(daysData, DateFormatUtils.parseDateToDatetimeString(day));
            int outPrice = dayData.getIntValue("outPrice");
            money = consumeQuantity * outPrice;
        }
        return money;
    }

    /**
     * 核销反核销分账
     * @param orderItem 子订单
     * @param day 日期
     * @param consumeQuantity 张数
     * @param sn 流水
     * @param consume 是否核销
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void consumeOrReConsumeSplitAccount(OrderItem orderItem, Date day, int consumeQuantity, String sn, boolean consume) {
        // 获取子订单的分账记录
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
        List<BalanceChangeInfo> balanceChangeInfoList;

        if (orderItem.getPayment_flag() == ProductConstants.PayType.PAYS) {
            Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
            balanceChangeInfoList =  AccountUtil.makerCashConsumeOrReConsumeBalanceChangeInfo(accounts, day, consumeQuantity, order.getBuy_ep_id(), order.getPayee_ep_id(), consume);
        } else {
            balanceChangeInfoList = AccountUtil.makerConsumeOrReConsumeBalanceChangeInfo(accounts, day, consumeQuantity, consume);
        }

        // 调用分账
        Result<BalanceChangeRsp> result = changeBalances(PaymentConstant.BalanceChangeType.CONSUME_SPLIT, sn, balanceChangeInfoList);
        if (!result.isSuccess()) {
            log.warn("核销OR反核销:{},分账失败:{}", consume, result.get());
            throw new ApiException(result.getError());
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderClearanceSerial saveClearanceSerial(OrderItem orderItem, int saleCoreEpId, Date day, int quantity, String sn, Date procTime) {
        OrderClearanceSerial serial = new OrderClearanceSerial();
        serial.setOrder_item_id(orderItem.getId());
        serial.setClearance_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(day);
        serial.setQuantity(quantity);
        serial.setSerial_no(sn);
        // 获取本次核销 供应平台商应得金额
        int money = getOutPriceForEp(orderItem.getId(), orderItem.getSupplier_core_ep_id(),
                orderItem.getSupplier_core_ep_id(), serial.getDay(), serial.getQuantity());
        serial.setSupplier_money(money);
        // 获取平台商通道费率
        serial.setChannel_fee(getChannelRate(orderItem.getSupplier_core_ep_id(), saleCoreEpId));
        orderClearanceSerialMapper.insertSelective(serial);
        return serial;
    }

    /**
     * 获取平台商通道费率
     * @param supplier 供应侧
     * @param sale 销售侧
     * @return
     */
    public int getChannelRate(int supplier, int sale) {
        if (supplier == sale) {
            return 0;
        }
        // 获取平台商通道费率
        Result<Integer> channelResult = coreEpChannelService.selectPlatfromRate(supplier, sale);
        if (!channelResult.isSuccess()) {
            throw new ApiException("获取平台商通道费率失败:" + channelResult.getError());
        }
        return channelResult.get();
    }

    public int[] getAuditConfig(int productSubId, int coreEpId) {
        int auditTicket = ProductConstants.RefundAudit.YES;
        int auditMoney = ProductConstants.RefundMoneyAudit.YES;
        Result<Map> platFormConfig = productSalesPlanRPCService.getPlatformConfig(productSubId, coreEpId);
        if (platFormConfig != null && platFormConfig.get() != null && platFormConfig.isSuccess()) {
            Map map = platFormConfig.get();
            Integer tmpTicket = CommonUtil.objectParseInteger(map.get("audit_ticket"));
            Integer tmpMoney = CommonUtil.objectParseInteger(map.get("audit_money"));
            auditTicket = tmpTicket == null ? auditTicket : tmpTicket;
            auditMoney = tmpMoney == null ? auditMoney : tmpMoney;
        }
        return new int[]{auditTicket, auditMoney};
    }

    public int[] getAuditConfig(Order order, OrderItem orderItem) {
        // 获取退订审核
        int[] auditSupplierConfig = getAuditConfig(orderItem.getPro_sub_id(), orderItem.getSupplier_core_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (orderItem.getSupplier_core_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney = getAuditConfig(orderItem.getPro_sub_id(), order.getPayee_ep_id())[1];
        }
        return new int[]{auditTicket, auditMoney};
    }

    /**
     * 返回记录订单日志数据
     * @param id
     * @param date
     * @param operateId
     * @param operateName
     * @param code
     * @param qty
     * @param memo
     * @return
     */
    public Object[] orderLog(Integer id, Date date, Object operateId, Object operateName, String code, Integer qty, String memo) {
        if (id == null) {
            throw new ApiException("记录日志异常:没有订单号");
        }
        Map result = orderMapper.selectByLog(id);
        return new Object[]{
                DateFormatUtils.parseDateToDatetimeString(date),
                result.get("order_number"),
                result.get("item_number"),
                OrderConstant.LogOperateCode.SYSTEM,
                operateId,
                operateName,
                code,
                qty == null && (code.equals(OrderConstant.LogOperateCode.CREATE_SUCCESS) || code.equals(OrderConstant.LogOperateCode.CANCEL_SUCCESS)) ? result.get("quantity") : qty,
                result.get("used_quantity"),
                result.get("refund_quantity"),
                result.get("refunding"),
                memo
        };
    }
}
