package com.all580.notice.api.service;

import com.all580.ep.api.conf.EpConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * Created by wxming on 2016/11/23 0023.
 */
@EventService(EpConstant.Event.BALANCE_CHANGE)
public interface BalanceChangeSubscribeService extends MnsSubscribeAction {
}
