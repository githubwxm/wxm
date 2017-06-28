package com.all580.notice.api.conf;

/**
 * 短信类型
 *
 * @author panyi on 2016/10/18.
 * @since V0.0.1
 */
public class SmsType {
    public static class Ep { // 企业
        public static final String CHANGLV_SERVICE_PHONE="18807490002";
        public static final int CORE_EP_ADD = 12001; // 平台商入驻
        public static final int CORE_EP_CHANNEL = 12002; // 平台商供销关系开通
        public static final int USER_ADD = 12003; // 员工新增
        public static final int SUPPLIER_ADD = 12004; // 供应商入驻
        public static final int SALES_ADD = 12005; // 销售商入驻
        public static final int BALANCE_SHORTAGE = 12006; // 余额不足
        public static final int PWD_RESET = 12007; // 密码重置
        public static final int PLATFORM_FREEZE=12008;//平台商冻结
        public static final int CHANNEL_SUPPLIER_FREEZE=12009;//渠道或供应商被冻结 supplier
        public static final int USER_ACCOUNT =12010;//员工账号冻结
        public static final int PLATFORM_STOP =12011;//平台商停用 账号被停用
        public static final int SUPPLIER_SELLER_FREEZE =12012;//销售商或供应商被冻结
        public static final int USER_STOP = 12013; // 员工账号 禁用（不可恢复
        public static final int PLATFORM_ACTIVE = 12014; //平台商激活
        public static final int PLATFORM_ACTIVE_SINGLE =12114;//平台商激活
        public static final int SUPPLIER_SELLER_ACTIVE = 12015; // 销售商、供应商激活
        public static final int USER_ACTIVE= 12016; // 员工账号激活
        public static final int USER_AUDIT= 12017; // 企业审核申请
        public static final int ADD_WARN = 12019; // 充值提醒
        public static final int EXTRCAT_WARN = 12020; // 提现提醒提取
        public static final int BALANCE_WARN = 12021; // 余额提醒
        public static final int CHANNEL_ADD = 12022; // 平台间的通道开通
        public static final int CHANNEL_FAIL = 12023; // 对接失败 渠道对接失败
        public static final int CHANGLV_CHANNEL_FAIL = 12024; // 中央平台失败 中央平台对接失败
        public static final int FAULT = 12025; // 系统故障 凭证、票务系统、小秘书故障
    }
    public static class Prod { // 产品
        public static final int PRODUCT_INFORM = 12201; // 产品调整通知 产品上架、价格更改、内容修改、计划变动。
        public static final int PRODUCT_ORDER = 12202; // 产品订购 产品预定支付完成
        public static final int PRODUCT_ORDER_OTA = 12203; // 产品订购  产品订购OTA
        public static final int VOUCHER_SEND_MA = 12207; // 凭证发码

        //第三方产品上下架回调通知
        public static final int THIRD_PRODUCT_UP = 12204; // 上架
        public static final int THIRD_PRODUCT_DOWN = 12205; // 下架
        public static final int THIRD_PRODUCT_UPDATE_UP = 12206; // 更新上架
    }

    public static class Order { // 订单
//        public static final int ORDER_ADD_TICKET = 12301; // 下单-门票
//        public static final int CONSUME_TICKET = 12302; // 核销-门票
//        public static final int REVERSE_TICKET = 12303; // 核销冲正-门票
//        public static final int ORDER_REFUND = 12304; // 退订单
//        public static final int MONEY_REFUND = 12305; // 退款
        public static final int ORDER_CONSUME = 12027; // 订单核销 通过凭证平台核销。
        public static final int CONSUME = 12028; // 核销冲正 销售商或渠道发起核销冲正申请
        public static final int CONSUME_SUCCESS= 12029; // 核销冲正 核销冲正成功
        public static final int MONEY_REFUND_FAIL = 12030; // 订单退款 退款失败
        public static final int ORDER_REFUND = 12031; // 游客退订 1、游客或客服人员发起订单退订 客户人员退订审核完成或系统自动审核完成
        public static final int MONEY_REFUND = 12032; // 订单退款
        public static final int SUPPLIER_ORDER_WARN = 12033; // 供应商订单确认通知refuse
        public static final int SUPPLIER_ORDER_REFUSE = 12034; // 供应商拒绝游客订购 供应商拒绝订购（库存不足等原因）
        public static final int SUPPLIER_PAY = 12035; // 供应商发起支付告知
        public static final int HOTEL_TICKET = 12050; // 酒店出票
        public static final int ITINERARY_ORDER = 12051; // 线路预定成功

    }
}
