package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.PlatformRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlatformRoleMapper {
    /**
     * 根据主键删除数据库的记录,t_platformrole
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_platformrole
     *
     * @param record
     */
    int insert(PlatformRole record);

    /**
     * 动态字段,写入数据库记录,t_platformrole
     *
     * @param record
     */
    int insertSelective(PlatformRole record);

    /**
     * 根据指定主键获取一条数据库记录,t_platformrole
     *
     * @param id
     */
    PlatformRole selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_platformrole
     *
     * @param record
     */
    int updateByPrimaryKeySelective(PlatformRole record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_platformrole
     *
     * @param record
     */
    int updateByPrimaryKey(PlatformRole record);

    PlatformRole getRoleByPlatformIdAndSupplyId(@Param("platform_Id") Integer platformId, @Param("supply_Id") Integer supplyId);

    PlatformRole getRoleByAuthInfo(@Param("auth_Id") String authId, @Param("auth_Key") String authKey);

    int selectRoleCount(@Param("platform_Id") Integer platformId,
                        @Param("supply_Id") Integer supplyId,
                        @Param("authId") String authId,
                        @Param("authKey") String authKey,
                        @Param("code") String code,
                        @Param("name") String name);

    List<Map> selectRoleList(@Param("platform_Id") Integer platformId,
                             @Param("supply_Id") Integer supplyId,
                             @Param("authId") String authId,
                             @Param("authKey") String authKey,
                             @Param("code") String code,
                             @Param("name") String name,
                             @Param("record_start") Integer recordStart,
                             @Param("record_count") Integer recordCount);
}