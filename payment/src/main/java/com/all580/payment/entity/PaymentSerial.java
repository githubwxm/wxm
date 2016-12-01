package com.all580.payment.entity;

import java.io.Serializable;
import java.util.Date;

public class PaymentSerial implements Serializable {
    private Integer id;

    private String local_serial_no;

    private String remote_serial_no;

    private Integer pay_amount;

    private String pay_time;

    private Integer ref_id;

    private Integer ref_type;

    private Integer payment_type;

    private Date create_time;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocal_serial_no() {
        return local_serial_no;
    }

    public void setLocal_serial_no(String local_serial_no) {
        this.local_serial_no = local_serial_no;
    }

    public String getRemote_serial_no() {
        return remote_serial_no;
    }

    public void setRemote_serial_no(String remote_serial_no) {
        this.remote_serial_no = remote_serial_no;
    }

    public Integer getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(Integer pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public Integer getRef_id() {
        return ref_id;
    }

    public void setRef_id(Integer ref_id) {
        this.ref_id = ref_id;
    }

    public Integer getRef_type() {
        return ref_type;
    }

    public void setRef_type(Integer ref_type) {
        this.ref_type = ref_type;
    }

    public Integer getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(Integer payment_type) {
        this.payment_type = payment_type;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}