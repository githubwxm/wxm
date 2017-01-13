package com.all580.order;

import com.all580.order.dto.AccountDataDto;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemAccount;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.framework.common.lang.JsonUtils;

import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/10 16:20
 */
public class SplitAccountTest {
    public static void main(String[] args) {
//        new SplitAccountTest().余额支付();
//        new SplitAccountTest().第三方零售();
//        new SplitAccountTest().余额支付_跨平台();
//        new SplitAccountTest().第三方零售_跨平台();
//        new SplitAccountTest().到付();
//        new SplitAccountTest().到付_零售();
//        new SplitAccountTest().到付_跨平台();
//        new SplitAccountTest().到付_零售_跨平台();
        AccountUtil.decompileRefundDay(JsonUtils.json2List("[{\"day\":\"2016-12-29 00:00:00\",\"quantity\":\"1\",\"visitors\":[{\"id\":\"240\",\"quantity\":\"1\"}],\"fee\":500}]"));
    }

    public void 余额支付(){
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 2);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PREPAY);
        print(salesMap, 4, orderItem);
    }

    public void 第三方零售() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"4\",\"ep_id\":\"4\",\"price\":\"1200\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 2);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PREPAY);
        print(salesMap, 4, orderItem);
    }

    public void 余额支付_跨平台(){
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 1);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PREPAY);
        print(salesMap, 4, orderItem);
    }

    public void 第三方零售_跨平台() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"4\",\"ep_id\":\"4\",\"price\":\"1200\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 1);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PREPAY);
        print(salesMap, 4, orderItem);
    }

    public void 到付() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"400\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PAYS);
        orderItem.setSupplier_ep_id(2);
        print(salesMap, 4, orderItem);
    }

    public void 到付_零售() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"4\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PAYS);
        orderItem.setSupplier_ep_id(2);
        print(salesMap, 4, orderItem);
    }

    public void 到付_跨平台() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 1);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PAYS);
        orderItem.setSupplier_ep_id(1);
        print(salesMap, 4, orderItem);
    }

    public void 到付_零售_跨平台() {
        List<List<EpSalesInfo>> orgData = new ArrayList<>();
        List<EpSalesInfo> salesInfos = new ArrayList<>();
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"1\",\"ep_id\":\"2\",\"price\":\"200\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"2\",\"ep_id\":\"3\",\"price\":\"400\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"3\",\"ep_id\":\"4\",\"price\":\"800\"}", EpSalesInfo.class));
        salesInfos.add(JsonUtils.fromJson("{\"sale_ep_id\":\"4\",\"ep_id\":\"4\",\"price\":\"1200\"}", EpSalesInfo.class));
        orgData.add(salesInfos);
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(orgData, new Date());
        Map<Integer, Integer> result = new HashMap<>();
        result.put(1, 1);
        result.put(2, 2);
        result.put(3, 2);
        result.put(4, 2);
        AccountUtil.setAccountDataCoreEpId(result, salesMap);
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(1);
        orderItem.setPayment_flag(ProductConstants.PayType.PAYS);
        orderItem.setSupplier_ep_id(1);
        print(salesMap, 4, orderItem);
    }

    public void print(Map<Integer, Collection<AccountDataDto>> salesMap, int finalEpId, OrderItem orderItem) {
        System.out.println("销售链信息:");
        System.out.println(JsonUtils.toJson(salesMap));

        List<OrderItemAccount> accounts = AccountUtil.paySplitAccount(salesMap, orderItem, finalEpId);
        System.out.println("支付分账信息:");
        System.out.println(JsonUtils.toJson(accounts));

        List<BalanceChangeInfo> balanceChangeInfoList = AccountUtil.makerPayBalanceChangeInfo(accounts, orderItem.getPayment_flag(), 1, 1, 4);
        System.out.println("支付分账余额变动信息:");
        System.out.println(JsonUtils.toJson(balanceChangeInfoList));
    }
}
