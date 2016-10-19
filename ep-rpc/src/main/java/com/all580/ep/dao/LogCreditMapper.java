package com.all580.ep.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/10/18 0018.
 */
public interface LogCreditMapper {
    /**
     *   查询企业id最后一次修改的授信额度
     * @param ep_id  ep_id 必填
     * @return   credit_after
     */
     Integer select (@Param("ep_id") Integer ep_id );

    /**
     * ,credit_before,credit_after
     * @param map{ep_id: int 企业id 必填，core_ep_id:int 平台商id
     *           credit_before:int  修改之前的额度，credit_after：int 修改之后的额度}
     * @return  Integer
     */
     Integer create(Map map);

    /**
     * @param map {core_ep_id 平台商id必填,name 企业名字,link_phone 联系人电话,
     *            limitStart 起始额度,limitEnd 结束额度,credit boolean 是否授信,
     *             province ,city,area 地区}
     * @return t.id,name,linkman,link_phone,province,city,area,credit_after,credit_date
     */
     List<Map> selectList(Map map);

     Integer selectListCount(Map map);

}
