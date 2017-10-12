package com.all580.voucherplatform.manager;

import com.all580.order.api.OrderConstant;
import com.all580.voucherplatform.dao.OrderLogMapper;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

/**
 * Created by wxming on 2017/10/9 0009.
 */
@Component
@Slf4j
public class OrderLogManager {

    @Autowired
    private OrderLogMapper orderLogMapper;

    public String getOrderId(String voucherId){
      String otaOrderId=  orderLogMapper.getPlatformOrderId(voucherId);
        return otaOrderId==null?orderLogMapper.getGroupPlatformOrderId(voucherId):otaOrderId;
    }
    /**
     * 返回记录订单日志数据
     * @param orderId
     * @param itemId
     * @param operateId
     * @param operateName
     * @param code
     * @param qty
     * @param memo
     * @return
     */
    public Object[] orderLog(String orderId, String itemId, Object operateId, Object operateName, String code, Integer qty, String memo, String sn) {
        if (orderId == null && itemId == null) {
            throw new ApiException("记录日志异常:没有订单号");
        }
        Map result = orderLogMapper.selectByLog(orderId, itemId);
        return new Object[]{
                DateFormatUtils.parseDateToDatetimeString(new Date()),
                result.get("order_number"),
                result.get("item_number"),
                OrderConstant.LogOperateCode.VOUCHERPLATFORM,
                operateId,
                operateName,
                code,
                qty == null && (code.equals(OrderConstant.LogOperateCode.SEND_XIAOMISHU) ) ? result.get("quantity") : qty,
                result.get("used_quantity"),
                result.get("refund_quantity"),
                result.get("refunding"),
                memo,
                sn==null?itemId:sn
        };
    }
}
