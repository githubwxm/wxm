package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.QrRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QrRuleMapper {
    /**
     *  根据主键删除数据库的记录,t_qrrule
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_qrrule
     *
     * @param record
     */
    int insert(QrRule record);

    /**
     *  动态字段,写入数据库记录,t_qrrule
     *
     * @param record
     */
    int insertSelective(QrRule record);

    /**
     *  根据指定主键获取一条数据库记录,t_qrrule
     *
     * @param id
     */
    QrRule selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_qrrule
     *
     * @param record
     */
    int updateByPrimaryKeySelective(QrRule record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_qrrule
     *
     * @param record
     */
    int updateByPrimaryKey(QrRule record);


    QrRule getQrRule(@Param("supply_id") Integer supplyId, @Param("prod_Id") Integer prodId);

    QrRule getDefaultQrRule();

    int getCount(@Param("name") String name,
                 @Param("len") Integer len,
                 @Param("prefix") String prefix,
                 @Param("postfix") String postfix,
                 @Param("supply_id")  Integer supplyId,
                 @Param("prod_Id") Integer prodId,
                 @Param("defaultOption") Boolean defaultOption);

    List<Map> getList(@Param("name") String name,
                      @Param("len") Integer len,
                      @Param("prefix") String prefix,
                      @Param("postfix") String postfix,
                      @Param("supply_id")  Integer supplyId,
                      @Param("prod_Id") Integer prodId,
                      @Param("defaultOption") Boolean defaultOption,
                      @Param("record_start") Integer recordStart,
                      @Param("record_count") Integer recordCount);

}