package com.all580.ep.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28 0028. 功能点
 */
public class Intf implements Serializable{
    private static final long serialVersionUID = -7213735249803456004L;
    private Integer  id;
    private Integer  pid;
    private Integer  link;
    private Integer  type;// '1-中心平台 2-运营平台',

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getLink() {
        return link;
    }

    public void setLink(Integer link) {
        this.link = link;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
