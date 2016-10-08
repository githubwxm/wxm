package com.all580.ep.api;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class EpConstant {
    public static class EpType{
        public static final Integer  CHANGLV=10000;//畅旅
        public static final Integer   PLATFORM=1001;//平台商A
    }
    public static class EpStatus{
        public static final Integer  UNINITIAL =100;//未初始化
        public static final Integer  ACTIVE =101;//激活
        public static final Integer  FREEZE=102;//冻结
        public static final Integer STOP=103;//停用
    }
    public static class PaymentType{
        public static final Integer BALANCE=111;//余额
        public static final Integer ALI_PAY=112;//支付宝
        public static final Integer WE_CHAT=113;//微信
        public static final Integer BANK=114;//银行
        public static final Integer STATUS_NORMAL =1111;// 正常
        public static final Integer STATUS_STOP=1112;// 停用
    }
}
