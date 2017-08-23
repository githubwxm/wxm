package com.all580.base.controller.com.all580.base.order;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wxming on 2017/7/31 0031.
 */
public class Linked {
    public static LinkedList<OrderTicket> linkedList = new LinkedList<OrderTicket>();
     static List<OrderTicket> list = new ArrayList();
        static{
       //list.add(new OrderTicket("橘子洲魔幻奇乐园成人票（平日票）",   "1498730461835081",8750-3649,"2017-08-10 00:00:00"));//id 792
       // list.add(new OrderTicket("橘子洲魔幻奇乐园亲子套票（平日票）","1498730167965081",2000,"2017-08-10 00:00:00"));  //   id 791
       list.add(new OrderTicket("橘子洲魔幻奇乐园儿童票（平日票）",  "1498730603328631",7500    ,"2017-08-10 00:00:00"));//793
        for(OrderTicket order:list ){
            for( int i =0;i< order.getNumber();i++){
                OrderTicket  temp = new OrderTicket(order.getName(),order.getProduct_sub_code(),1,order.getDate());
                linkedList.add(temp);
            }
         }

        /*
        *
        * 平日票：成人10000、亲子和儿童各5000
         * 假日票：成人20000、亲子和儿童各10000
         * 平日票预定时间：8月10号  假日票预定日期：8月11号   一个订单一张票  所有订单一个游客信息     游客信息：姓名：余海涛   电话：13973186663   身份证：420501198007121314
            橘子洲魔幻奇乐园成人票（平日票）:1498730461835081
            橘子洲魔幻奇乐园亲子套票（平日票）：1498730167965081   791
            橘子洲魔幻奇乐园儿童票（平日票）:1498730603328631   793
            橘子洲魔幻奇乐园成人票（假日票）:1500540773710441
            橘子洲魔幻奇乐园亲子套票（假日票）:1500540481482441
            橘子洲魔幻奇乐园儿童票（假日票）:1500541065775461
        * */
    }




    public static OrderTicket getOrderTicket()
    {
        synchronized (linkedList)
        {
            return linkedList.pollFirst();
        }
    }
}
