package com.all580.ep.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class Ep implements Serializable{
    private static final long serialVersionUID = -2032438614881917537L;

    public Ep(String name, String en_name, String linkman, String link_phone, String address, String code,
              String license, String logo_pic, Integer province, Integer city, Integer area,String status_name) {
        this.name = name;
        this.en_name = en_name;
        this.linkman = linkman;
        this.link_phone = link_phone;
        this.address = address;
        this.code = code;
        this.license = license;
        this.logo_pic = logo_pic;
        this.province = province;
        this.city = city;
        this.area = area;
        this.status_name=status_name;
    }
    public Ep(){

    }
    private String status_name;
    private Integer id ;
    private String name; // 企业名称
    private String en_name  ;//企业英文名',
    private Integer ep_type  ;//1001-畅旅平台商 1002-平台商A 1003-平台商B 1004-平台商C1021-供应商 1022-叶子供应商1031-销售商 1032-自营商 1033-OTA 1034-叶子销售商
    private String linkman  ;//联系人',
    private String link_phone  ;//联系电话',
    private String address  ;//地址',
    private String code  ;//企业组织机构代码',
    private String license  ;//营业执照',
    private String logo_pic  ;//企业logo',
    private Integer status  ;//100初始化101-正常\n102-已冻结\n103-已停用',
    private String access_id  ;//运营平台接口访问标识',
    private String access_key  ;//运营平台接口访问密钥',
    private Integer creator_ep_id  ;//上级企业',
    private Integer core_ep_id  ;//所属平台商企业id',
    private Date add_time  ;//
    private Integer status_bak  ;//冻结/停用平台商操作时企业当前的状态',
    private Integer province  ;//省',
    private Integer city  ;//市',
    private Integer area  ;//区',
    private Integer   group_id  ;//组ID',
    private String group_name  ;//组名称'

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

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

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public Integer getEp_type() {
        return ep_type;
    }

    public void setEp_type(Integer ep_type) {
        this.ep_type = ep_type;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getLink_phone() {
        return link_phone;
    }

    public void setLink_phone(String link_phone) {
        this.link_phone = link_phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLogo_pic() {
        return logo_pic;
    }

    public void setLogo_pic(String logo_pic) {
        this.logo_pic = logo_pic;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getCreator_ep_id() {
        return creator_ep_id;
    }

    public void setCreator_ep_id(Integer creator_ep_id) {
        this.creator_ep_id = creator_ep_id;
    }

    public Integer getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(Integer core_ep_id) {
        this.core_ep_id = core_ep_id;
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public Integer getStatus_bak() {
        return status_bak;
    }

    public void setStatus_bak(Integer status_bak) {
        this.status_bak = status_bak;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
