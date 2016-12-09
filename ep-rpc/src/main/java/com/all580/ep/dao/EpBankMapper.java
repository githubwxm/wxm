package com.all580.ep.dao;

import com.all580.ep.entity.EpBank;

import java.util.Map;

public interface EpBankMapper {
    int insert(Map<String,Object> record);
    Map<String,Object> selectBank(int id);
    Map<String,Object> selectBankEpSummary(int id);
}