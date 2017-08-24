package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Reverse;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReverseMapper {
    /**
     * 根据主键删除数据库的记录,t_reverse
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_reverse
     *
     * @param record
     */
    int insert(Reverse record);

    /**
     * 动态字段,写入数据库记录,t_reverse
     *
     * @param record
     */
    int insertSelective(Reverse record);

    /**
     * 根据指定主键获取一条数据库记录,t_reverse
     *
     * @param id
     */
    Reverse selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_reverse
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Reverse record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_reverse
     *
     * @param record
     */
    int updateByPrimaryKey(Reverse record);

    /**
     * @param orderId
     * @param orderCode
     * @param supplyId
     * @param consume_Id
     * @return
     */
    Reverse selectBySeqId(@Param("order_Id") Integer orderId,
                          @Param("order_Code") String orderCode,
                          @Param("supply_Id") Integer supplyId,
                          @Param("consume_Id") Integer consume_Id);


    List<Map> selectOrderReverseReportNumber(@Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime,
                                             @Param("platformProdId") Integer platformProdId,
                                             @Param("supplyProdId") Integer supplyProdId);

    List<Map> selectOrderReverseReportCount(@Param("startTime") Date startTime,
                                            @Param("endTime") Date endTime,
                                            @Param("platformProdId") Integer platformProdId,
                                            @Param("supplyProdId") Integer supplyProdId);
}