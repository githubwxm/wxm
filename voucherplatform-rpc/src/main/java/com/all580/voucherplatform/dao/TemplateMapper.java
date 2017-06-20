package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Template;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TemplateMapper {
    /**
     *  根据主键删除数据库的记录,t_template
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_template
     *
     * @param record
     */
    int insert(Template record);

    /**
     *  动态字段,写入数据库记录,t_template
     *
     * @param record
     */
    int insertSelective(Template record);

    /**
     *  根据指定主键获取一条数据库记录,t_template
     *
     * @param id
     */
    Template selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_template
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Template record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_template
     *
     * @param record
     */
    int updateByPrimaryKey(Template record);

    Template getTemplate(@Param("supppy_Id") Integer supppyId, @Param("prod_Id") Integer prodId);

    Template getDefaultTemplate();


    int getCount(@Param("name") String name,
                 @Param("supppy_Id") Integer supppyId,
                 @Param("prod_Id") Integer prodId,
                 @Param("defaultOption") Boolean defaultOption);

    List<Map> getList(@Param("name") String name,
                      @Param("supppy_Id") Integer supppyId,
                      @Param("prod_Id") Integer prodId,
                      @Param("defaultOption") Boolean defaultOption,
                      @Param("record_start") Integer recordStart,
                      @Param("record_count") Integer recordCount);

}