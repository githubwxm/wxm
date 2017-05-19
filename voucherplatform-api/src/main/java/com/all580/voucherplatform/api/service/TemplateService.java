package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/16.
 * 短彩信模板管理操作相关函数类
 */
public interface TemplateService {


    /**
     * 创建一个短信/打印模版
     *
     * @param map {name:xx,sms:xx,printText:222,supplier_id:xxx,supplierproduct_id:xxx}
     *            name    -   String  -   名称
     *            sms -   String    -  短信内容
     *            printText  -   String  -   小票打印内容
     *            supplier_id  -   int -   供应商Id
     *            supplierproduct_id   -   int -   供应商产品Id
     * @return
     */
    Result create(Map map);

    /**
     * 更新一个二维码模版
     *
     * @param map {id:xxx,name:xx,,sms:xx,printText:222}
     *            id    -   int  -   要修改的id
     *            name    -   String  -   名称
     *            sms -   String    -  短信内容
     *            printText  -   String  -   小票打印内容
     * @return
     */
    Result update(Map map);

    /**
     * 删除一个二维码模版(默认模版不能删除)
     *
     * @param id
     * @return
     */
    Result delete(Integer id);


    /**
     * 根据id获取一个模版
     *
     * @param id
     * @return
     */
    Result get(Integer id);

    /**
     * 根据供应商的id或产品获取一个模版
     *
     * @param supplierId
     * @param prodId
     * @return
     */
    Result get(Integer supplierId, Integer prodId);


    /**
     * 获取模版的总数量
     *
     * @param name
     * @param supplierId
     * @param prodId
     * @param defaultOption
     * @return
     */
    int getCount(String name, Integer supplierId, Integer prodId, Boolean defaultOption);


    /**
     * 获取模版集合
     *
     * @param name
     * @param len
     * @param prefix
     * @param postfix
     * @param supplierId
     * @param prodId
     * @param defaultOption
     * @param recordStart
     * @param recordSCount
     * @return
     */
    List<Map> getList(String name,
                      Integer len,
                      String prefix,
                      String postfix,
                      Integer supplierId,
                      Integer prodId,
                      Boolean defaultOption,
                      Integer recordStart,
                      Integer recordSCount);

}
