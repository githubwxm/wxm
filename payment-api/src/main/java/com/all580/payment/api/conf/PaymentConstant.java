package com.all580.payment.api.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付模块常量类
 *
 * @author Created by panyi on 2016/10/8.
 */
public class PaymentConstant {
    public static final String EVENT_NAME_BALANCE_CHANGE = "mnsBalanceChangeAction"; // 事件名称-余额变更
    public static final String EVENT_NAME_FUND_CHANGE="mnsFundChangeAction";
    private static List<Integer> list = new ArrayList<Integer>();
    static{
        list.add(BalanceChangeType.THIRD_PAY_FOR_ORDER);

    }
    public static List<Integer> getList() {
        return list;
    }

    public static void setList(List<Integer> list) {
        PaymentConstant.list = list;
    }

    public static class Status {
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }
    public enum ThirdPayStatus{
        SUCCESS, // 订单支付成功
        NOTPAY, // 订单未支付     订单未支付,已关闭   订单支付取消
        USERPAYING,// 订单正在支付中
        PAYERROR,// 订单支付失败

        REFUND_SUCCES, // 退款成功
        REFUND_FAIL, // 退款失败
        REFUND_PROCESSING, // 退款处理中
        REFUND_NOTSURE, // 未确定，需要商户  原退款单号重新发起
        REFUND_CHANGE // 转入代发 退款到银行发现用户的卡问题需要商户人工干预，通过线下或者财付通转
    }
    /**
     * 余额变动类型
     */
    public static class BalanceChangeType {
        public static final Integer BALANCE_PAY = 7001; // 余额支付
        public static final Integer BALANCE_REFUND = 7002; // 余额退款
        public static final Integer PAY_SPLIT = 7003; // 支付分账
        public static final Integer CONSUME_SPLIT = 7004; // 核销分账
        public static final Integer REVERSE_SPLIT = 7005; // 核销冲正分账
        public static final Integer REFUND_PAY = 7006; // 退票分账
        public static final Integer MANUAL_CHANGE_BALANCE = 7007; // 余额调整
        public static final Integer MANUAL_CHANGE_BALANCE_ADD = 7009; // 余额充值
        public static final Integer MANUAL_CHANGE_BALANCE_EXIT = 7010; // 余额提现
        public static final Integer THIRD_QUIT_FOR_ORDER = 7011; // 下单第三方退订
        public static final Integer THIRD_PAY_FOR_ORDER = 7008; // 下单第三方支付

        public static final Integer SALES_PROFIT = 7021; //销售分润
        public static final Integer CANCEL_THE_REFUND = 7022; //退订返回//
        public static final Integer THIRD_PAYMENT_PROFIT= 7023;//第三方支付分润
        public static final Integer PRODUCT_SALES= 7024;//产品销售
        public static final Integer ORDER_REFUND = 7025;//退订退款
        public static final Integer ORDER_CANCEL = 7026;//订单核销
        public static final Integer ORDER_REVERSE  = 7027;//订单冲正
        public static final Integer REFUND_FEE= 7028;//退订手续费
        public static final Integer REFUND_TICKET= 7029;//门票退订
        public static final Integer PRODUCT_REFUND_FEE= 7030;//产品退票手续费
        public static final Integer PRODUCT_SALES_PROFIT= 7031;//产品销售利润
        public static final Integer FREIGHT_PAY= 7032;//到付支付
        public static final Integer FREIGHT_ROUTING= 7033;//到付分账
        public static final Integer THIRD_PAYMENT_REFUND= 7034;//第三方支付退款
    }

    /**
     * 支付方式
     */
    public static class PaymentType {
        public static final Integer BALANCE = 7111; // 余额
        public static final Integer ALI_PAY = 7112; // 支付宝
        public static final Integer WX_PAY = 7113; // 微信
        public static final Integer CHINA_PAY = 7114; // 银联
    }

    /**
     * 支付方式
     */
    public static class PaymentiImg {
        public static final String ALI_PAY_IMG = "star/upls/2017/03/03/58b92caa3f84c.png"; // 支付宝
        public static final String WX_PAY_IMG = "star/upls/2017/03/03/58b92ceca296d.png"; // 微信
    }
}
