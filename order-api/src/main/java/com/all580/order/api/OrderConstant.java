package com.all580.order.api;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单模块常量
 * @date 2016/9/30 14:47
 */
public class OrderConstant {
    /**
     * 票据团散
     */
    public static class TicketFlag {
        public static final int INDIVIDUAL = 300;// 散购票
        public static final int GROUP = 301;// 团购票
    }

    /**
     * 订单状态（支付）
     */
    public static class OrderStatus {
        public static final int AUDIT_WAIT = 310; // 待审核
        public static final int PAY_WAIT = 311; // 待支付
        public static final int PAYING = 312; // 支付中
        public static final int PAY_FAIL = 313; // 支付失败
        public static final int PAID_HANDLING = 314; // 已支付,处理中
        public static final int PAID = 315; // 已支付,交易成功
        public static final int CANCEL = 316; // 已取消
    }

    /**
     * 子订单状态（票）
     */
    public static class OrderItemStatus {
        public static final int AUDIT_WAIT = 320; // 待审核
        public static final int AUDIT_SUCCESS = 321; // 审核通过
        public static final int NON_SEND = 322; // 未出票
        public static final int TICKET_FAIL = 323; // 出票失败
        public static final int TICKETING = 324; // 出票中
        public static final int SEND = 325; // 已出票
        public static final int CANCEL = 326; // 已取消
    }

    /**
     * 分账状态
     */
    public static class AccountSplitStatus {
        public static final int NOT = 330; // 未结清
        public static final int HAS = 331; // 已结清
    }

    /**
     * 退订状态
     */
    public static class RefundOrderStatus {
        public static final int AUDIT_WAIT = 340; // 待审核
        public static final int FAIL = 341; // 退订失败
        public static final int REFUNDING = 342; // 退票中
        public static final int REFUND_MONEY = 343; // 退款中
        public static final int REFUND_MONEY_FAIL = 344; // 退款失败
        public static final int REFUND_SUCCESS = 345; // 退订成功
    }

    /**
     * 代收
     */
    public static class FromType {
        public static final int TRUST = 350; // 代收
        public static final int NON_TRUST = 351; // 不代收
    }
}