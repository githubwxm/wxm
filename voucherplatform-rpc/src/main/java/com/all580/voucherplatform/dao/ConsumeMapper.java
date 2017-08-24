package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Consume;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ConsumeMapper {
    /**
     * 根据主键删除数据库的记录,t_consume
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_consume
     *
     * @param record
     */
    int insert(Consume record);

    /**
     * 动态字段,写入数据库记录,t_consume
     *
     * @param record
     */
    int insertSelective(Consume record);

    /**
     * 根据指定主键获取一条数据库记录,t_consume
     *
     * @param id
     */
    Consume selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_consume
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Consume record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_consume
     *
     * @param record
     */
    int updateByPrimaryKey(Consume record);

    /**
     * @param orderId
     * @param orderCode
     * @param supplyId
     * @param seqId
     * @return
     */
    Consume selectBySeqId(@Param("order_Id") Integer orderId,
                          @Param("order_Code") String orderCode,
                          @Param("supply_Id") Integer supplyId,
                          @Param("seqId") String seqId);

    List<Consume> selectConsumeByOrder(@Param("orderId") Integer orderId,
                                       @Param("orderCode") String orderCode);

    List<Map> selectOrderConsumeReportNumber(@Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime,
                                             @Param("platformProdId") Integer platformProdId,
                                             @Param("supplyProdId") Integer supplyProdId);

    List<Map> selectOrderConsumeReportCount(@Param("startTime") Date startTime,
                                            @Param("endTime") Date endTime,
                                            @Param("platformProdId") Integer platformProdId,
                                            @Param("supplyProdId") Integer supplyProdId);
}