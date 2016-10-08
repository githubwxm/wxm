package com.all580.ep.api.entity;

import java.io.Serializable;

/**
 * Created by wxm on 2016/9/28 0028.
 */
public class CoreEpChannel implements Serializable{
    private static final long serialVersionUID = 161456160361134132L;
    private  Integer  id ;
    private  Integer  supplier_core_ep_id ;//供应侧平台商ID',
    private Integer  seller_core_ep_id ;//销售侧平台商ID',
    private int  rate; //'费率',

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupplier_core_ep_id() {
        return supplier_core_ep_id;
    }

    public void setSupplier_core_ep_id(Integer supplier_core_ep_id) {
        this.supplier_core_ep_id = supplier_core_ep_id;
    }

    public Integer getSeller_core_ep_id() {
        return seller_core_ep_id;
    }

    public void setSeller_core_ep_id(Integer seller_core_ep_id) {
        this.seller_core_ep_id = seller_core_ep_id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
