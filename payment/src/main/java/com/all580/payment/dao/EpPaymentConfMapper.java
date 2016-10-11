package com.all580.payment.dao;

import com.all580.payment.entity.EpPaymentConf;

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
     * @return EpPaymentConf
     */
    EpPaymentConf findByEpId(Integer epId);
}