package com.all580.role.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserRoleFuncMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    int insert(Map record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    int insertSelective(Map record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    Map selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    int updateByPrimaryKeySelective(Map record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_role_func
     *
     * @mbggenerated Mon Jul 03 16:18:09 CST 2017
     */
    int updateByPrimaryKey(Map record);

    int insertUserRoleFuncBatch(@Param("user_role_id") Integer user_role_id, @Param("oper_id") Integer oper_id,
                                @Param("func_ids") List<Integer> func_ids);

    List<Integer> selectUserRoleIdFuncId(@Param("user_role_id") int user_role_id, @Param("ids") List<Integer> ids);
    List<Map> selectUserRoleIdFunc(@Param("user_role_id") int user_role_id);
    List<Integer> selectUserRoleIdId(@Param("user_role_id") int user_role_id, @Param("ids") List<Integer> ids);
    int deleteUserFunc(@Param("user_role_id") int ep_role_id, @Param("ids") List<Integer> ids);

    List<Map<String,Object>> selectUserRoleId(@Param("user_role_id") int ep_role_id, @Param("ids") List<Integer> ids);
    List<Integer> selectFuncId(int func_id);
    int deleteFuncId(int func_id);
}