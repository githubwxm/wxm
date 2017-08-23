package com.all580.base.controller.com.all580.base.order;

import java.util.Date;

import static com.all580.base.controller.com.all580.base.order.CreateOrder.start;
import static com.all580.base.controller.com.all580.base.order.CreateOrder.url;

/**
 * Created by wxming on 2017/7/31 0031.
 */
public class Test {
    public static void main(String [] a){
        System.out.println("start:"+new Date());
        for(int i =0;i<1;i++){
            OrderTicket orderTicket =new OrderTicket("橘子洲魔幻奇乐园成人票（平日票）",   "1498730461835081",1,"2017-08-10 00:00:00");
            start(new OrderTicket("橘子洲魔幻奇乐园成人票（平日票）",   "1498730461835081",1,"2017-08-10 00:00:00"),url,i);
            start(new OrderTicket("橘子洲魔幻奇乐园亲子套票（平日票）","1498730167965081",1,"2017-08-10 00:00:00"),url,i);
            start(new OrderTicket("橘子洲魔幻奇乐园儿童票（平日票）",  "1498730603328631",1,"2017-08-10 00:00:00"),url,i);
            start(new OrderTicket("橘子洲魔幻奇乐园成人票（假日票）",  "1500540773710441",1,"2017-08-11 00:00:00"),url,i);
            start(new OrderTicket("橘子洲魔幻奇乐园亲子套票（假日票）","1500540481482441",1,"2017-08-11 00:00:00"),url,i);
            start(new OrderTicket("橘子洲魔幻奇乐园亲子套票（假日票）","1500541065775461",1,"2017-08-11 00:00:00"),url,i);
        }
        System.out.println("end:"+new Date());
    }
}
