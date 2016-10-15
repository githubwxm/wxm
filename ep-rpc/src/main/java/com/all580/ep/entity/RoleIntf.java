package com.all580.ep.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class RoleIntf implements Serializable{
    private static final long serialVersionUID = 6266102147756993334L;
    private  Integer  id ;
    private  Integer  inft_id;
    private  Integer  role_id;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInft_id() {
        return inft_id;
    }

    public void setInft_id(Integer inft_id) {
        this.inft_id = inft_id;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }


}
