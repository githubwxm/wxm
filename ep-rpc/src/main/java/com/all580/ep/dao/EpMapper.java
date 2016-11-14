package com.all580.ep.dao;




import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface EpMapper {
     List<Map<String,Object>> selectPlatform();
    int create(Object obj);
    List<Map<String,Object>> select (Map<String,Object> params);
    Integer selectCount(Map<String,Object> params);
    List<Map<String,Object>>  getEp(Map<String,Object> params);
    List<Map<String,Object>> all(Map<String,Object> params);
    //List<List<?>>  validate(Map<String,Object> params);
    int updateCoreEpId(Map<String,Object> map);
    int update(Map<String,Object> params);
    int updateStatus(Map<String,Object> params);
    int updatePlatfromStatus(Map<String,Object> map);
    int platformEnable(Map<String,Object> map);
    int epEnable(Map<String,Object> map);// 平台商激活时激活 企业
    List<Map<String,Object>> platformListDown(Map<String,Object> map);
    int platformListDownCount(Map<String,Object> map);

    List<Map<String,Object>> platformListUp(Map<String,Object> map);
    int platformListUpCount(Map<String,Object> map);


    List<Map<String,Object>> getAccountInfoList(Map<String,Object> map);
    List<Map<String,Object>> getAccountInfoListCount(Map<String,Object> map);

    List<Map<String,Object>> checkNamePhone(Map<String,Object> map);

    /**
     * 只查询了ep单表的数据
     * @param map
     * @return
     */
    List<Map<String,String>> selectSingleTable(Map<String,Object> map);
    String selectPhone(int id);
    int updateEpRole(Map<String,Object> map);
}
