package com.all580.ep.api.entity;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class PlatformEp {
    private   Ep ep_info;
    private CoreEpPaymentConf payment;
    private  Object  capital;

    public Ep getEp_info() {
        return ep_info;
    }

    public void setEp_info(Ep ep_info) {
        this.ep_info = ep_info;
    }

    public CoreEpPaymentConf getPayment() {
        return payment;
    }

    public void setPayment(CoreEpPaymentConf payment) {
        this.payment = payment;
    }

    public Object getCapital() {
        return capital;
    }

    public void setCapital(Object capital) {
        this.capital = capital;
    }
}
