package com.all580.order.adapter;

import com.all580.ep.api.service.LogCreditService;
import com.all580.order.api.OrderConstant;
import com.all580.order.entity.Order;
import com.all580.order.manager.LockTransactionManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 景点散客下单自动支付
 * @date 2017/2/15 14:49
 */
@Component(OrderConstant.CREATE_ADAPTER + OrderConstant.OrderAdapter.TICKET_AUTO_PAY)
public class TicketCreateOrderAutoPayImpl extends TicketCreateOrderImpl {
    @Autowired
    private LockTransactionManager lockTransactionManager;

    @Autowired
    private BalancePayService balancePayService;
    @Autowired
    private LogCreditService logCreditService;

    @Override
    public boolean after(Map params, Order order) {
        boolean after = super.after(params, order);
        if (order.getPay_amount() == null || order.getPay_amount() <= 0) return after;
        Result<Map<String, String>> result = balancePayService.getBalanceAccountInfo(order.getBuy_ep_id(), order.getPayee_ep_id());
        if (!result.isSuccess()) {
            throw new ApiException("获取余额失败:" + result.getError());
        }
        Map<String, String> map = result.get();
        int balance = Integer.parseInt(map.get("balance"));
        Result<Integer> creditResult = logCreditService.select(order.getBuy_ep_id(), order.getPayee_ep_id());
        if (!creditResult.isSuccess()) {
            throw new ApiException("获取授信失败:" + creditResult.getError());
        }
        Integer credit = creditResult.get();

        if (balance + (credit == null ? 0 : credit) < order.getPay_amount()) {
            throw new ApiException(Result.BALANCE_LACK_FOR_PAYMENT, "余额不足");
        }

        if (!isCheck(params)) {
            Result<?> payResult = lockTransactionManager.payment(params, order.getNumber(), PaymentConstant.PaymentType.BALANCE);
            if (!payResult.isSuccess()) {
                throw new ApiException("自动支付异常:" + payResult.getError());
            }
        }
        return after;
    }
}
