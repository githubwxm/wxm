package com.all580.order.api;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单模块常量
 * @date 2016/9/30 14:47
 */
public class OrderConstant {

    public static final String CREATE_ADAPTER = "CREATE_ADAPTER";
    public static final String REFUND_ADAPTER = "REFUND_ADAPTER";
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
        public static final int MODIFYING = 327; // 修改中
        public static final int MODIFY = 328; // 已修改
        public static final int MODIFY_FAIL = 329; // 修改失败
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
        public static final int REFUND_MONEY_AUDITING = 346; // 退款审核中
    }

    /**
     * 代收
     */
    public static class FromType {
        public static final int TRUST = 350; // 代收
        public static final int NON_TRUST = 351; // 不代收
    }

    /**
     * 查询日期类型
     */
    public static class SearchDateType {
        public static final int CREATE = 360; // 下单
        public static final int BOOKING = 361; // 预定
        public static final int USE = 362; // 使用
    }

    /**
     * 证件类型
     */
    public static class CardType {
        public static final int ID = 370; // 身份证
        public static final int OTHER = 371; // 其它
    }

    /**
     * 第三方支付任务类型
     */
    public static class PaymentJobType {
        public static final int PAYING = 380; // 支付中
        public static final int REFUND = 381; // 退款中
    }

    /**
     * 线路团队状态
     */
    public static class LineGroupStatus {
        public static final int NONE = 390; //未出团
        public static final int SEND = 391; //已发团
        public static final int COMPLETE = 392; //已完成
    }

    /**
     * 性别
     */
    public static class SexType {
        public static final int NONE = 3000; // 未知
        public static final int MAN = 3001; // 男
        public static final int FEMALE = 3002; // 女
    }

    /**
     * 任务执行器
     */
    public static class Actions {
        public static final String PAYMENT_CALLBACK = "PAYMENT_CALLBACK"; // 支付回调
        public static final String PAYMENT_SPLIT_ACCOUNT = "PAYMENT_SPLIT_ACCOUNT"; // 支付分账
        public static final String REFUND_MONEY_CALLBACK = "REFUND_MONEY_CALLBACK"; // 退款回调
        public static final String CANCEL_CALLBACK = "CANCEL_CALLBACK"; // 取消回调
        public static final String REFUND_MONEY = "REFUND_MONEY";   // 退款
        public static final String CONSUME_SPLIT_ACCOUNT = "CONSUME_SPLIT_ACCOUNT"; // 核销分账
        public static final String RE_CONSUME_SPLIT_ACCOUNT = "RE_CONSUME_SPLIT_ACCOUNT"; // 反核销分账
        public static final String SEND_TICKET = "SEND_TICKET"; // 出票
        public static final String SEND_GROUP_TICKET = "SEND_GROUP_TICKET"; // 出票
        public static final String REFUND_STOCK = "REFUND_STOCK"; // 还库存
        public static final String REFUND_MONEY_SPLIT_ACCOUNT = "REFUND_MONEY_SPLIT_ACCOUNT"; // 退款分账
    }

    public static class EventType {
        public static final String ORDER_CREATE = "ORDER_CREATE"; // 订单创建成功
        public static final String ORDER_WAIT_AUDIT = "ORDER_WAIT_AUDIT"; // 订单待审核
        public static final String ORDER_AUDIT = "ORDER_AUDIT"; // 订单审核
        public static final String PAID = "PAID"; // 支付成功
        public static final String SPLIT_CREATE_ACCOUNT = "SPLIT_CREATE_ACCOUNT"; // 创建订单分账
        public static final String SEND_TICKET = "SEND_TICKET"; // 出票结果
        public static final String CONSUME_TICKET = "CONSUME_TICKET"; // 核销票据
        public static final String ORDER_REFUND_APPLY = "ORDER_REFUND_APPLY"; // 订单退订申请成功
        public static final String ORDER_REFUND_AUDIT = "ORDER_REFUND_AUDIT"; // 订单退订审核
        public static final String REFUND_TICKET = "REFUND_TICKET"; // 退票结果
        public static final String REFUND_SUCCESS = "REFUND_SUCCESS"; // 退订成功
        public static final String REFUND_ALI_PAY_AUDIT = "REFUND_ALI_PAY_AUDIT"; // 退款支付宝待确认
        public static final String REFUND_MONEY_APPLY = "REFUND_MONEY_APPLY"; // 退款申请成功
        public static final String REFUND_MONEY_AUDIT = "REFUND_MONEY_AUDIT"; // 退款申请审核
        public static final String REFUND_MONEY = "REFUND_MONEY"; // 退款结果
        public static final String ORDER_CANCEL = "ORDER_CANCEL"; // 订单取消
    }

    /**
     * 日志操作码
     */
    public static class LogOperateCode {
        public static final String NAME = "order-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}-_-{}";
        public static final String SYSTEM = "all580";
        public static final String CREATE_SUCCESS = "CA1000100";
        public static final String CANCEL_SUCCESS = "CA3000100";
        public static final String PAID = "CA40001";
        public static final String PAID_FAIL = "CA4000101";
        public static final String PAID_SUCCESS = "CA4000100";
        public static final String SEND_TICKETING = "CA5000110";
        public static final String RECEIVE_TICKETING = "CA5000111";
        public static final String SENDED = "CA50003";
        public static final String REFUND_APPLY_SUCCESS = "CA6000100";
        public static final String REFUND_AUDIT_PASS_SUCCESS = "CA7000100";
        public static final String REFUND_AUDIT_REJECT_SUCCESS = "CA7000200";
        public static final String SEND_REFUND_TICKETING = "CA7000110";
        public static final String RECEIVE_REFUND_TICKETING = "CA7000111";
        public static final String REFUND_TICKET = "CA7000112";
        public static final String REFUND_MONEY_APPLY = "CA80001";
        public static final String REFUND_MONEY_AUDIT_SUCCESS = "CA8000200";
        public static final String REFUND_MONEY_FAIL = "CA8000301";
        public static final String REFUND_MONEY_SUCCESS = "CA8000300";
        public static final String TICKET_CONSUME = "CA90001";
        public static final String TICKET_CONSUME_SUCCESS = "CA9000100";
        public static final String TICKET_CONSUME_FAIL = "CA9000101";
        public static final String TICKET_RECONSUME = "CA100001";
        public static final String TICKET_RECONSUME_SUCCESS = "CA10000101";
        public static final String TICKET_RECONSUME_FAIL = "CA10000100";
        public static final String AUDIT_PASS_SUCCESS = "CA20000100";
        public static final String AUDIT_REJECT_SUCCESS = "CA20000200";
    }

    public static String[] ORDER_TABLES = new String[]{
            "t_order", "t_order_item", "t_order_item_detail", "t_refund_order",
            "t_refund_serial", "t_refund_visitor", "t_shipping", "t_visitor", "t_order_clearance_detail",
            "t_order_clearance_serial", "t_ma_send_response", "t_clearance_washed_serial", "t_refund_account", "t_order_item_account"};
}
