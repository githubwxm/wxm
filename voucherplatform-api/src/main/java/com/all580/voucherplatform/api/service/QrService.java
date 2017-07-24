package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface QrService {

    /**
     * 创建一个二维码模版
     *
     * @param map {name:xx,len:xx,prefix:xxx,postfix:xxx,errorRate:xxx,size:xxx,foreColor:xxx,supplyId_id:xxx,supplyIdProd_id:xxx}
     *            name    -   String  -   名称
     *            len -   int    -  凭证的生成长度
     *            prefix  -   String  -   前缀
     *            postfix -   String  -   后缀
     *            errorRate   -   String  -   容错率
     *            size    -   int -   二维码图片大小
     *            foreColor    -   String  -   前景色
     *            supplyId_id  -   int -   供应商Id
     *            supplyIdProd_id   -   int -   供应商产品Id
     * @return
     */
    Result create(Map map);

    /**
     * 更新一个二维码模版
     *
     * @param map {id:xxx,name:xx,len:xx,prefix:xxx,postfix:xxx,errorRate:xxx,size:xxx,foreColor:xxx}
     *            id    -   int  -   要修改的id
     *            name    -   String  -   名称
     *            len -   int    -  凭证的生成长度
     *            prefix  -   String  -   前缀
     *            postfix -   String  -   后缀
     *            errorRate   -   String  -   容错率
     *            size    -   int -   二维码图片大小
     *            foreColor    -   String  -   前景色
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
     * @param supplyId
     * @param prodId
     * @return
     */
    Result get(Integer supplyId, Integer prodId);


    /**
     * 获取模版集合
     *
     * @param name
     * @param len
     * @param prefix
     * @param postfix
     * @param supplyId
     * @param prodId
     * @param defaultOption
     * @param recordStart
     * @param recordCount
     * @return
     */
    Result<PageRecord<Map>> selectQrList(String name, Integer len, String prefix, String postfix, Integer supplyId, Integer prodId, Boolean defaultOption, Integer recordStart, Integer recordCount);

}
