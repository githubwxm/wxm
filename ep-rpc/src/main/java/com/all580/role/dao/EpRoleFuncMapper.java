package com.all580.role.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EpRoleFuncMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Map<String,Object> record);

    int insertSelective(Map<String,Object> record);

    Map<String,Object> selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Map<String,Object> record);

    int updateByPrimaryKey(Map<String,Object> record);


    /**
     * ON DUPLICATE KEY UPDATE isdelete = 0  分步操作 先更新 为delete 然后按id 查找更新与插入  分步骤
     * @param ep_role_id
     * @param oper_id
     * @param func_ids
     * @return
     */
    int insertEpRoleFuncBatch(@Param("ep_role_id") int ep_role_id,
    @Param("oper_id") Integer oper_id,@Param("func_ids") List<Integer> func_ids);

    /**
     * 按 角色id全部制成删除状态
     * @param ep_role_id
     * @return
     */
    int  updateEpRoleFuncIsDelete(@Param("ep_role_id")int ep_role_id,@Param("oper_id") int oper_id);


    /**
     * 更新已经存在的状态为正常
     * @param ep_role_id
     * @param ids
     * @return
     */
    int updateEpRoleFuncIsNotDelete(@Param("ep_role_id") int ep_role_id,@Param("ids")List<Integer> ids);

    /**
     * 查询出已经存在需要重新插入的功能 id
     * @param ep_role_id
     * @param ids
     * @return
     */
    List<Integer> selectEpRoleIdFuncId(@Param("ep_role_id") int ep_role_id,@Param("ids")List<Integer> ids);

    List<Map<String,Object>> selectepRoleId(@Param("ep_role_id") int ep_role_id);
}