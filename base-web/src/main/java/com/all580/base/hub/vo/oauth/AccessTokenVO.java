package com.all580.base.hub.vo.oauth;

public class AccessTokenVO {

    /**
     * oauth token
     */
    private String access_token;
    /**
     * 过期时间
     */
    private Integer expires_in;
    /**
     * 同意授权的用户openid
     */
    private String open_id;
    
    /**
     * 刷新access_token
     */
    private String refresh_token;
    
    /**
     * 授权范围
     */
    private String scope;
    
    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public Integer getExpires_in() {
        return expires_in;
    }
    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
    public String getOpen_id() {
        return open_id;
    }
    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    
}
