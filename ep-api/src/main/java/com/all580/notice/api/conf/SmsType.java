package com.all580.notice.api.conf;

/**
 * 短信类型
 *
 * @author panyi on 2016/10/18.
 * @since V0.0.1
 */
public class SmsType {
    public static class Ep { // 企业
        public static final int CORE_EP_ADD = 12001; // 平台商入驻
        public static final int CORE_EP_CHANNEL = 12002; // 平台商供销关系开通
        public static final int USER_ADD = 12003; // 员工新增
        public static final int SUPPLIER_ADD = 12004; // 供应商入驻
        public static final int SALES_ADD = 12005; // 销售商入驻
        public static final int BALANCE_SHORTAGE = 12006; // 余额不足
        public static final int PWD_RESET = 12007; // 密码重置
    }
    public static class Prod { // 产品

    }

    public static class Order { // 订单
        public static final int ORDER_ADD_TICKET = 12010; // 下单-门票
        public static final int CONSUME_TICKET = 12011; // 核销-门票
        public static final int REVERSE_TICKET = 12012; // 核销冲正-门票
        public static final int ORDER_REFUND = 12013; // 退订单
        public static final int MONEY_REFUND = 12014; // 退款

    }
}
