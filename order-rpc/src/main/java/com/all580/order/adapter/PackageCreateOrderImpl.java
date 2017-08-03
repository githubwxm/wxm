package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.PackageOrderItemAccountMapper;
import com.all580.order.dao.PackageOrderItemMapper;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.util.AccountUtil;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.*;

/**
 * Created by xiangzw on 2017/7/20.
 */
@Component
public class PackageCreateOrderImpl  implements CreatePackageOrderService {

    @Autowired
    private PackageOrderItemMapper packageOrderItemMapper;
    @Autowired
    private PackageOrderItemAccountMapper packageOrderItemAccountMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;

    @Resource(name= OrderConstant.CREATE_ADAPTER + "PACKAGE",type = CreateOrderInterface.class)
    private PackageCreateOrderItemImpl packageCreateOrderItemService;

    @Override
    public CreateOrder parseParams(Map params) {
        return packageCreateOrderItemService.parseParams(params);
    }

    @Override
    public Result validate(CreateOrder createOrder, Map params) {
        return packageCreateOrderItemService.validate(createOrder, params);
    }

    @Override
    public Order insertOrder(CreateOrder createOrder, Map params) {
        return packageCreateOrderItemService.insertOrder(createOrder, params);
    }

    @Override
    public ValidateProductSub parseItemParams(CreateOrder createOrder, Map params) {
        return packageCreateOrderItemService.parseItemParams(createOrder, params);
    }

    @Override
    public ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map params) {
        ProductSearchParams packageParam = new ProductSearchParams();
        packageParam.setSubProductCode(sub.getCode());
        packageParam.setStartDate(sub.getBooking());
        packageParam.setDays(sub.getDays());
        packageParam.setQuantity(sub.getQuantity());
        packageParam.setBuyEpId(createOrder.getEpId());
        List<ProductSearchParams> searchParams = new ArrayList<>();
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items){
            ValidateProductSub productSub = packageCreateOrderItemService.parseItemParams(createOrder, item);
            ProductSearchParams productSearchParams = new ProductSearchParams();
            productSearchParams.setSubProductCode(productSub.getCode());
            productSearchParams.setStartDate(productSub.getBooking());
            searchParams.add(productSearchParams);
        }

        Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(packageParam, searchParams);
        if (!salesInfoResult.isSuccess()) {
            throw new ApiException(salesInfoResult.getError());
        }
        ProductSalesInfo salesInfo = salesInfoResult.get();
        // 判断供应商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEp_id()), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("供应商企业已冻结");
        }
        if (salesInfo.getDay_info_list().size() != sub.getDays()) {
            throw new ApiException("预定天数与获取产品天数不匹配");
        }
        // 验证最低购票
        if (salesInfo.getMin_buy_quantity() != null && salesInfo.getMin_buy_quantity() > sub.getQuantity()) {
            throw new ApiException("低于最低购买票数");
        }
        // 判断最高票数
        if (salesInfo.getMax_buy_quantity() != null && salesInfo.getMax_buy_quantity() > 0 && sub.getQuantity() > salesInfo.getMax_buy_quantity()) {
            throw new ApiException(String.format("超过订单最高购买限制: 当前购买:%d, 最大购买:%d", sub.getQuantity(), salesInfo.getMax_buy_quantity())).dataMap()
                    .putData("current", sub.getQuantity()).putData("max", salesInfo.getMax_buy_quantity());
        }
        return salesInfo;
    }

    @Override
    public void validateBookingDate(ValidateProductSub sub, List<ProductSalesDayInfo> dayInfoList) {
        packageCreateOrderItemService.validateBookingDate(sub, dayInfoList);
    }

    @Override
    public PackageOrderItem insertPackageOrderInfo(ProductSalesInfo salesInfo, Order order, Map params) {
        PackageOrderItem packageOrderItem = new PackageOrderItem();
        packageOrderItem.setOrder_number(order.getNumber());
        packageOrderItem.setProduct_sub_name(salesInfo.getProduct_sub_name());
        packageOrderItem.setProduct_sub_id(salesInfo.getProduct_sub_id());
        packageOrderItem.setProduct_sub_code(salesInfo.getProduct_sub_code());
        packageOrderItem.setProduct_name(salesInfo.getProduct_name());
        packageOrderItem.setQuantity(CommonUtil.objectParseInteger(params.get("quantity")));
        packageOrderItem.setEp_id(salesInfo.getEp_id());
        packageOrderItem.setCore_ep_id(bookingOrderManager.getCoreEpId(salesInfo.getEp_id()).get());
        packageOrderItem.setPayment_flag(salesInfo.getPay_type());
        packageOrderItemMapper.insertSelective(packageOrderItem);
        return packageOrderItem;
    }

    @Override
    public List<PackageOrderItemSalesChain> insertSalesChain(PackageOrderItem item, ValidateProductSub sub, List<List<EpSalesInfo>> allDaysSales) {
        List<PackageOrderItemSalesChain> chains = new ArrayList<>();
        for (int i = 0; i < allDaysSales.size(); i++) {
            List<EpSalesInfo> daySales = allDaysSales.get(i);
            Date day = DateUtils.addDays(sub.getBooking(), i);
            Set<Integer> eps = new HashSet<>();
            for (EpSalesInfo daySale : daySales) {
                if (!eps.contains(daySale.getEp_id())) {
                    chains.add(bookingOrderManager.generatePackagerChain(item, daySale.getEp_id(), day, daySales));
                    eps.add(daySale.getEp_id());
                }
                if (!eps.contains(daySale.getSale_ep_id())) {
                    chains.add(bookingOrderManager.generatePackagerChain(item, daySale.getSale_ep_id(), day, daySales));
                    eps.add(daySale.getSale_ep_id());
                }
            }
        }
        return chains;
    }

    @Override
    public void prePaySplitAccount(List<List<EpSalesInfo>> allDaysSales, PackageOrderItem item, Integer epId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(item.getId());
        orderItem.setPro_sub_number(item.getProduct_sub_code());
        orderItem.setStart(item.getStart());
        orderItem.setDays(1);
        orderItem.setQuantity(item.getQuantity());
        orderItem.setSupplier_core_ep_id(item.getCore_ep_id());
        orderItem.setSupplier_ep_id(item.getEp_id());
        orderItem.setPayment_flag(item.getPayment_flag());
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(allDaysSales, orderItem.getStart());
        AccountUtil.setAccountDataCoreEpId(bookingOrderManager.getCoreEpIds(salesMap.keySet()), salesMap);
        List<OrderItemAccount> accounts = AccountUtil.paySplitAccount(salesMap, orderItem, epId);
        for (OrderItemAccount account : accounts) {
            PackageOrderItemAccount itemAccount = new PackageOrderItemAccount();
            BeanUtils.copyProperties(account, itemAccount);
            packageOrderItemAccountMapper.insertSelective(itemAccount);
        }
    }
}
