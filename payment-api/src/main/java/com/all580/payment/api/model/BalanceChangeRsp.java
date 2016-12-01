package com.all580.payment.api.model;

import java.io.Serializable;

/**
 * 余额变更的返回结果
 *
 * @author Created by panyi on 2016/10/10.
 */
public class BalanceChangeRsp implements Serializable{
    private Integer ep_id; // 企业ID
    private Integer core_ep_id; // 平台商ID
    private Integer less_balance; // 缺少的余额

    public BalanceChangeRsp() {
    }

    public BalanceChangeRsp(Integer epId, Integer coreEpId, Integer lessBalance) {
        this.ep_id = epId;
        this.core_ep_id = coreEpId;
        this.less_balance = lessBalance;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public Integer getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(Integer core_ep_id) {
        this.core_ep_id = core_ep_id;
    }

    public Integer getLess_balance() {
        return less_balance;
    }

    public void setLess_balance(Integer less_balance) {
        this.less_balance = less_balance;
    }
}
