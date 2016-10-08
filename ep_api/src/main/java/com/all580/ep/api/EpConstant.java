package com.all580.ep.api;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class EpConstant {
    public static class EpStatus{
        public static final Integer  UNINITIAL =0;//未初始化
        public static final Integer  ACTIVE =1;//激活
        public static final Integer  FREEZE=2;//冻结
        public static final Integer STOP=3;//停用
    }
    public static class PaymentType{
        public static final Integer BALANCE=1;//余额
        public static final Integer ALI_PAY=2;//支付宝
        public static final Integer WE_CHAT=3;//微信
        public static final Integer BANK=4;//银行
        public static final Integer STATUS_NORMAL =1;// 正常
        public static final Integer STATUS_STOP=2;// 停用
    }
}
