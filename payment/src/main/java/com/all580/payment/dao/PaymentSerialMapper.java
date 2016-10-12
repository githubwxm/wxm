package com.all580.payment.dao;

import com.all580.payment.entity.PaymentSerial;

public interface PaymentSerialMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentSerial record);

    int insertSelective(PaymentSerial record);

    PaymentSerial selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentSerial record);

    int updateByPrimaryKey(PaymentSerial record);
}