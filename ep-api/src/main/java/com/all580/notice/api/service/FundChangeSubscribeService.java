package com.all580.notice.api.service;

import com.all580.ep.api.conf.EpConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

import java.util.Map;

/**
 * Created by wxming on 2016/11/23 0023.
 */
@EventService(EpConstant.Event.FUND_CHANGE)
public interface FundChangeSubscribeService extends MnsSubscribeAction<Map> {
}
