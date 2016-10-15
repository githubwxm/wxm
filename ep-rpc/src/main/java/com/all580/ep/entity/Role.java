package com.all580.ep.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class Role implements Serializable{
    private static final long serialVersionUID = -9200357642408334987L;
    private  Integer id ;
    private  String name;
    private   Integer type ;//1-企业 2-用户',

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
