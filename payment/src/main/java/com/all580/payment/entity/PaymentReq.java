package com.all580.payment.entity;

import java.io.Serializable;

public class PaymentReq implements Serializable {
    private Integer id;

    private Long ord_id;

    private Long serial_no;

    private String data;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrd_id() {
        return ord_id;
    }

    public void setOrd_id(Long ord_id) {
        this.ord_id = ord_id;
    }

    public Long getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(Long serial_no) {
        this.serial_no = serial_no;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }
}