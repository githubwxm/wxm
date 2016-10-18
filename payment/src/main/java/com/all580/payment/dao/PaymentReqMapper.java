package com.all580.payment.dao;

import com.all580.payment.entity.PaymentReq;

public interface PaymentReqMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentReq record);

    int insertSelective(PaymentReq record);

    PaymentReq selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentReq record);

    int updateByPrimaryKey(PaymentReq record);
}