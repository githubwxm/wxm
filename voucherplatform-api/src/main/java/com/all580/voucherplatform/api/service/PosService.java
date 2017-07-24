package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created by Linv2 on 2017-07-20.
 */
public interface PosService {
    Result apply(Map map);

    Result query(Map map);

    Result request(Map map);
}
