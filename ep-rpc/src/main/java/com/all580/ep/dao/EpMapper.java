package com.all580.ep.dao;




import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface EpMapper {
     List<Map<String,Object>> selectPlatform();
    List<Map<String,Object>> selectPlatformSupplier(int seller_core_ep_id);
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
    Map<String, Object> selectId(int id);

    /**
     * 查询平台商下的企业 按名字与类型包含平台商
     * @return
     */
    List<Map<String,Object>> selectTypeName(Map<String,Object> map);
    List<Map<String,Object>> getSellerPlatfromAccuntInfo(Map<String,Object> map);
    int getSellerPlatfromAccuntInfoCount(Map<String,Object> map);
    List<Map<String,Object>> getAccountInfoList(Map<String,Object> map);

    int getAccountInfoListCount(Map<String,Object> map);

    Integer selectPlatformId(int id);
    List<Map<String,Object>> checkNamePhone(Map<String,Object> map);
     int updateEpGroup(@Param("groupId") Integer groupId,@Param("GroupName") String GroupName,@Param("epids") List<Integer> epids);
    /**
     * 只查询了ep单表的数据
     * @param map
     * @return
     */
    List<Map<String,String>> selectSingleTable(Map<String,Object> map);

    List<Map<String,String>>selectSingleTableStatus(Map<String,Object> map);
    /**
     * 只查询了ep单表的数据
     * @param
     * @return
     */
    List<Map<String,String>> selectEpList(@Param("epids") List<Integer> epids);
    List<Map<String,String>> selectEpGroupList(@Param("epids") List<Integer> epids);

    String selectPhone(int id);
    int updateEpRole(Map<String,Object> map);

    /**
     * 检查  该企业类型是否有这个  企业角色
     * @param map
     * @return
     */
    int checkEpRole(Map<String,Object> map);
    List<Map<String,String>> getCoreEpName(@Param("list") List<Integer> list,@Param("mainEpId") Integer mainEpId);
    List<Integer> selectSupplier(Integer  coreEpId);
    List<Integer> getSeller(@Param("list") List<Integer> list);

    List<Map<String,String>> selectCoreEpId(Collection<Integer> list);
}
