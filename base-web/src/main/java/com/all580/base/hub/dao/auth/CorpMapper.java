package com.all580.base.hub.dao.auth;

import java.util.List;
import com.all580.base.hub.model.auth.CorpModel;

public interface CorpMapper {
    int deleteCorpModelById(Integer id);

    int insertCorpModel(CorpModel record);

    CorpModel selectCorpModelById(Integer id);

    int updateCorpModelById(CorpModel record);

    List<CorpModel> selectCorpModel(CorpModel record);
}
