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
        public static final Integer THIRD_PAY_FOR_ORDER = 7008; // 下单第三方支付
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
