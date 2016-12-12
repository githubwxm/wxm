package com.all580.ep.dao;

import com.all580.ep.entity.EpParam;

public interface EpParamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EpParam record);

    int insertSelective(EpParam record);

    EpParam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EpParam record);

    int updateByPrimaryKey(EpParam record);
    Integer selectEpNoteStatus();
    int updateEpNoteStatus(Integer status);
}