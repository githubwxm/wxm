package com.all580.payment.api.conf;

/**
 * 支付模块常量类
 *
 * @author Created by panyi on 2016/10/8.
 */
public class PaymentConstant {
    public static class Status{
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }
    /**
     * 余额变动类型
     */
    public static class BalanceChangeType {
        public static final Integer BALANCE_PAY = 7001; // 余额支付
        public static final Integer PAY_SPLIT = 7002; // 支付分账
        public static final Integer CONSUME_SPLIT = 7003; // 核销分账
        public static final Integer REVERSE_SPLIT = 7004; // 核销冲正分账
        public static final Integer REFUND_PAY = 7005; // 退票分账
        public static final Integer MANUAL_CHANGE_BALANCE = 7006; // 余额调整
        public static final Integer THIRD_PAY_FOR_ORDER = 7007; // 第三方下单支付
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
}
