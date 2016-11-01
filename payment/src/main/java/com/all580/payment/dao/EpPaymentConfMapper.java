package com.all580.payment.dao;

import com.all580.payment.entity.EpPaymentConf;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EpPaymentConfMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EpPaymentConf record);

    int insertSelective(EpPaymentConf record);

    EpPaymentConf selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EpPaymentConf record);

    int updateByPrimaryKey(EpPaymentConf record);

    /**
     * 通过企业ID获取配置信息
     *
     * @param epId 企业ID
     * @return List<Map<String, String>>
     */
    List<Map<String, String>> listByEpId(Integer epId);

    /**
     * 根据企业ID和支付类型统计记录数
     *
     * @param coreEpId    企业ID
     * @param paymentType 支付类型
     * @return 记录数
     */
    int countByEpIdAndType(@Param("coreEpId") Integer coreEpId, @Param("paymentType") Integer paymentType);

    EpPaymentConf getByEpIdAndType(@Param("coreEpId") Integer coreEpId, @Param("paymentType") Integer paymentType);
}