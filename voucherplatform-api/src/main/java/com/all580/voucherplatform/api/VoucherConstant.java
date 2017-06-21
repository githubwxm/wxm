package com.all580.voucherplatform.api;

/**
 * Created by Linv2 on 2017-06-06.
 */
public class VoucherConstant {
    /**
     * 退票状态
     */
    public static class RefundStatus {
        public static final int WAIT_CONFIRM = 0; // 退票待确认
        public static final int SUCCESS = 1;//退票成功
        public static final int FAIL = 2;//退款失败
    }

    /**
     * 短信发送状态
     */
    public static class SmsSendType {
        public static final int YES = 1;
        public static final int NO = 0;
    }


    /**
     * 产品类型
     */
    public static class ProdType {
        public static final int GENERAL = 1;//通用散客产品
        public static final int GROUP = 2;//团队
    }

    /**
     * 订单状态
     */
    public static class OrderStatus{
        public static final int WAIT_CONFIRM = 0; // 订单待确认

    }
}
