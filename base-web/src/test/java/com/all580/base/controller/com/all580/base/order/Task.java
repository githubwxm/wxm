package com.all580.base.controller.com.all580.base.order;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by wxming on 2017/7/31 0031.
 */
public class Task implements Runnable {

    LinkedList<OrderTicket> linkedList;

    public Task()
    {

    }

    public Task(LinkedList<OrderTicket> linkedList)
    {
        this.linkedList = linkedList;
    }

    @Override
    public void run()
    {
        while (!linkedList.isEmpty())
        {
            CreateOrder.start(Linked.getOrderTicket(),CreateOrder.url,linkedList.size());
        }
        System.out.println("linkedList 为空  end:"+new Date());
        CollectSql.addSql("end:"+new Date());
    }
}
