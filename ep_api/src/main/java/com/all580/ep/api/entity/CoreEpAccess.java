package com.all580.ep.api.entity;

import java.io.Serializable;

/**
 * Created by wxm on 2016/9/28 0028.
 */
public class CoreEpAccess implements  Serializable{
    private static final long serialVersionUID = -3442088902089161573L;
    private Integer  id ;//平台商企业id',
    private String  access_id ;//中心平台接口访问标识',
    private String  access_key ;//中心平台接口访问密钥',
    private String  link;//
    public CoreEpAccess(){

    }
    public CoreEpAccess(Integer id,String  access_id,String  access_key){
        this.id=id;
        this.access_id=access_id;
        this.access_key=access_key;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccess_id() {
        return access_id;
    }

    public void setAccess_id(String access_id) {
        this.access_id = access_id;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
