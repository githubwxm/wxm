package com.all580.ep.api;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class EpConstant {
    public static class EpStatus{
        public static Integer uninitial=0;//未初始化
        public static Integer active=1;//激活
        public static Integer freeze=2;//冻结
        public static Integer stop=3;//停用
    }
    public static class PaymentType{
        public static Integer balance=1;//余额
        public static Integer aliPay=2;//支付宝
        public static Integer weixin=3;//微信
        public static Integer bank=4;//银行
        public static Integer normal=1;// 正常
        public static Integer stop=1;// 停用
    }
}
