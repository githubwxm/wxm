package com.all580.payment.entity;

import java.io.Serializable;
import java.util.Date;

public class CapitalSerial implements Serializable {
    private Integer id;

    private String ref_id;

    private Integer ref_type;

    private Integer capital_id;

    private Integer old_balance;

    private Integer old_can_cash;

    private Integer new_balance;

    private Integer new_can_cash;

    private Date create_time;
    private String summary="";

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public Integer getRef_type() {
        return ref_type;
    }

    public void setRef_type(Integer ref_type) {
        this.ref_type = ref_type;
    }

    public Integer getCapital_id() {
        return capital_id;
    }

    public void setCapital_id(Integer capital_id) {
        this.capital_id = capital_id;
    }

    public Integer getOld_balance() {
        return old_balance;
    }

    public void setOld_balance(Integer old_balance) {
        this.old_balance = old_balance;
    }

    public Integer getOld_can_cash() {
        return old_can_cash;
    }

    public void setOld_can_cash(Integer old_can_cash) {
        this.old_can_cash = old_can_cash;
    }

    public Integer getNew_balance() {
        return new_balance;
    }

    public void setNew_balance(Integer new_balance) {
        this.new_balance = new_balance;
    }

    public Integer getNew_can_cash() {
        return new_can_cash;
    }

    public void setNew_can_cash(Integer new_can_cash) {
        this.new_can_cash = new_can_cash;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}