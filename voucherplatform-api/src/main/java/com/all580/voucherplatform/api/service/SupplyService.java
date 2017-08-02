package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface SupplyService {

    /**
     * 添加供应商信息
     *
     * @param map {name:xx,phone,xx,address:xx,region:xx,description:xx,signType:xx}
     *            name    -string -名字
     *            phone -string -手机号码
     *            address -string -地址
     *            region    -string -区域
     *            description   -string -描述
     *            signTye   -enum   -签名类型
     * @return
     */
    Result create(Map map);

    Result getSupply(Integer id);

    /**
     * 修改供应商信息
     *
     * @param map {id:xx,name:xx,phone,xx,address:xx,region:xx,description:xx}
     *            id  -int    -要修改的供应商id
     *            name    -string -名字
     *            phone -string -手机号码
     *            address -string -地址
     *            region    -string -区域
     *            description   -string -描述
     * @return
     */
    Result update(Map map);

    /**
     * 修改配置
     *
     * @param map {id:xx,conf:xx}
     *            id  -int    -要修改的供应商id
     *            conf  -string -配置数据
     * @return
     */
    Result updateConf(Map map);

    /**
     * 修改票务对接信息
     *
     * @param map {id:xx,ticketsysId:xx}
     *            id  -int    -要修改的供应商id
     *            ticketsysId  -int -票务系统id
     * @return
     */
    Result updateTicketSys(Map map);

    /**
     * 修改供应商的签名方式，调用该函数将回重置私钥和公钥
     *
     * @param map {id:xx,signType:xx}
     *            id  -int    -要修改的供应商id
     *            signType  -int -签名方式
     * @return
     */
    Result updateSignType(Map map);

    Result selectSupply(Integer supplyId);

    Result<PageRecord<Map>> selectSupplyList(String name, Integer recordStart, Integer recordCount);


    Result<PageRecord<Map>> selectSupplyProdList(Integer supplyId, String prodCode, Integer recordStart, Integer recordCount);

    /**
     * 根据供应商的身份id和产品id，获取产品信息
     *
     * @param supplyId
     * @param prodId
     * @return
     */
    Map getProd(int supplyId, String prodId);


    /**
     * 根据供应商的身份id，更新产品信息
     *
     * @param supplyId
     * @param map      {code:xx,name:xx:descripton:xx:data:object}
     *                 code-   string- code
     *                 name- string- 名字
     *                 description-  string- 描述
     *                 data- object- 对象数据
     * @return
     */

    /**
     * 根据供应商的身份id和产品id，删除产品
     *
     * @param supplyId
     * @param prodId
     * @return
     */

    Result delProd(int supplyId, String prodId);


    /**
     * 检测是否可以通过界面维护产品
     *
     * @param supply
     * @return
     */
    Boolean checkProdPower(int supply);

    /**
     * 根据供应商的身份id，批量更新产品信息
     *
     * @param supplyId
     * @param map      {code:xx,name:xx:descripton:xx:data:object}
     *                 code-   string- code
     *                 name- string- 名字
     *                 description-  string- 描述
     *                 data- object- 对象数据
     * @return
     */
    Result setProd(int supplyId, Map map);

    /**
     * 根据供应商的身份id，批量更新产品信息
     *
     * @param supplyId
     * @param map      {code:xx,name:xx:descripton:xx:data:object}
     *                 code-   string- code
     *                 name- string- 名字
     *                 description-  string- 描述
     *                 data- object- 对象数据
     * @return
     */
    Result setProd(int supplyId, List<Map> map);

    /**
     * 申请同步产品操作
     *
     * @param supplyId
     * @return
     */
    Result syncProd(int supplyId);

    /**
     * 申请下载配置
     *
     * @param supplyId
     * @return
     */
    Map getConfFile(int supplyId);

    List<Map> getSignType();


}
