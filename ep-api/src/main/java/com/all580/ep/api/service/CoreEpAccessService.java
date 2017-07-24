package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface CoreEpAccessService {

    /**
     *  {  id :企业id int 必填 ,access_id:中心平台借口标识 String 必填，
     *  access_key :String 必填 中心平台接口访问密钥,link:String 必填 地址}
     * @param map
     * @return
     */
    Result<Integer> create(Map<String, Object> map);

    /**
     *
     * @param params  {  id :企业id ,access_id:中心平台借口标识}
     *                    id - int  非必填  access_id- String  非必填
     * @return  {  id :企业id ,access_id:中心平台借口标识,access_key:中心平台接口访问密钥,link:link}
     *              id - int    access_id- String  access_key-String ,link-String
     */
    Result<List<Map<String,Object>>> select(Map<String, Object> params);

    /**
     * 校验access_id 是否存在
     * @param access_id
     * @return   平台商id   Integer
     */
    Result<Integer> checkAccessId(Object access_id);


    Result<Map<String,Object>>  selectAccess(Map<String, Object> map);

    /**
     *
     * @param ids
     * @return   access_key
     */
    Result<List<String>> selectAccessList(List<Integer> ids);

    Result<List<Map<String,Object>>> selectAccessMap(List<Integer> ids);
}
