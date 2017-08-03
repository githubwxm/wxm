package com.all580.base.controller.com.all580.base.order;

import lombok.Data;

/**
 * Created by wxming on 2017/7/28 0028.
 */
@Data
public class OrderTicket {
    private String name;
    private String product_sub_code;
    private int number;
    private String date;
    public OrderTicket(){

    }
    public OrderTicket(String name,String product_sub_code,int number,String date){
        this.name=name;
        this.product_sub_code=product_sub_code;
        this.number=number;
        this.date=date;
    }
}
