package com.all580.ep.api;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class EpConstant {
    public static class EpType{//企业类型
        public static final Integer  CHANGLV=10000;//畅旅
        public static final Integer   PLATFORM=10001;//平台商A
        public static final Integer   SUPPLIER=10002;//供应商
        public static final Integer   SELLER=10003;//销售商
    }
    public static class EpStatus{//企业状态
        public static final Integer  UNINITIAL =100;//初始化
        public static final Integer  ACTIVE =101;//激活
        public static final Integer  FREEZE=102;//冻结
        public static final Integer STOP=103;//停用
    }
    public static class EpClass{//企业分类
        public static final Integer  SCENIC =10010;//景区
        public static final Integer  HOTEL =10011;//酒店
        public static final Integer  TRAVEL=10012;//旅行社
        public static final Integer  RESTS=10013;//其他

    }
    public static class PaymentType{ //收款方式
        public static final Integer BALANCE=111;//余额
        public static final Integer ALI_PAY=112;//支付宝
        public static final Integer WE_CHAT=113;//微信
        public static final Integer BANK=114;//银行
        public static final Integer STATUS_NORMAL =1111;// 收款正常
        public static final Integer STATUS_STOP=1112;// 收款停用
    }
}
