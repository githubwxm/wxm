package com.all580.base.manager.auth;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.all580.base.hub.dao.auth.CorpMapper;
import com.all580.base.hub.model.auth.CorpModel;
import com.framework.common.BaseManager;
import com.framework.common.exception.ApiException;

@Component
public class CorpManager extends BaseManager {

	@Resource
	private CorpMapper corpMapper;
	
	public CorpModel queryByClientId(String clientId) {
	    if (StringUtils.isEmpty(clientId)) {
	        throw new ApiException("clientid is null");
	    }
	    CorpModel p = new CorpModel();
	    p.setClientId(clientId);
	    List<CorpModel> models = this.corpMapper.selectCorpModel(p);
	    if (models.isEmpty())
	        return null;
	    else
	        return models.get(0);
	}
}
