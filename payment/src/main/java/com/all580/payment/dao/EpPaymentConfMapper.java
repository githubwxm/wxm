package com.all580.payment.dao;

import com.all580.payment.entity.EpPaymentConf;

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
     * @param epId 企业ID
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> listByEpId(Integer epId);

    /**
     * 根据企业ID和支付类型统计记录数
     *
     * @param epId 企业ID
     * @param type 支付类型
     * @return 记录数
     */
    int countByEpIdAndType(Integer epId, Integer type);
}