package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface PlatformService {
    /**
     * 创建一个新的平台商
     * 目前只有小秘书一个平台商
     * 尽量简化调用过程
     *
     * @param map {name:xx,description:xx,signType:xx}
     *            name  -string -名字
     *            description   -string -描述
     *            signType  -euun   -签名类型
     * @return
     */
    Result create(Map map);

    /**
     * 创建一个新的平台商的信息
     *
     * @param map {id:xx,name:xx,description:xx,signType:xx}
     *            id    -int    -要修改的id
     *            name  -string -名字
     *            description   -string -描述
     *            signType  -euun   -签名类型(注意，如果此参数不为空，则会重置公钥和私钥)
     * @return
     */
    Result update(Map map);

    /**
     * 添加 供应商对平台商的授权
     *
     * @param platformId
     * @param supplyId
     * @return
     */
    Result createRole(Integer platformId, Integer supplyId);

    /**
     * 平台上认证商户信息
     *
     * @param map {authId:xx,authKey:xx,code:xx,name:xx}
     *            authId    -string -认证id
     *            authKey   -string -认证key
     *            code  -string -商户编号
     *            name  -string -商户名字
     * @return
     */
    Result auth(Map map);

    /**
     * 获取一个平台商
     *
     * @param id
     * @return
     */
    Map getPlatform(Integer id);

    /**
     * @param roleId
     * @param map    {code:xxx,name:xxx,productTypeId:xxx,supplyprodId:xxx}
     *               code    -string     -产品id
     *               name    -string     -产品名称
     *               productTypeId   -int    -产品类型，可空
     *               supplyprodId    -int    -供应产品id
     * @return
     */
    Result setProd(int roleId, Map map);

    /**
     * 获取产品信息
     *
     * @param prodId
     * @return
     */
    Map getProd(int prodId);

    /**
     * 根据平台id和产品好获取一个产品实体
     *
     * @param platformId
     * @param prodCode
     * @return
     */
    Result getProdByPlatform(int platformId, String prodCode);


    Result<PageRecord<Map>> selectPlatformProdList(String name, Integer platformId, Integer supplyId, Integer supplyprodId, String platformProdCode, Integer productTypeId, Integer recordStart, Integer recordCount);


    Result<PageRecord<Map>> selectPlatformList(String name, Integer recordStart, Integer recordCount);

    Result<PageRecord<Map>> selectRoleList(Integer platformId, Integer supplyId, String authId, String authKey, String code, String name, Integer recordStart, Integer recordCount);

    //Result<PageRecord<Map>>


    Result<PageRecord<Map>> selectProdTyeList(String name, Integer recordStart, Integer recordCount);
    Result selectProdType(Integer prodTypeId);
    Result setProdType(Map map);
    Result delProdType(Integer prodTypeId);
}
