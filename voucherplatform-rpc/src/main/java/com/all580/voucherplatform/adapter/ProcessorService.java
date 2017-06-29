package com.all580.voucherplatform.adapter;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-07.
 */
public interface ProcessorService<TIdentity> {
    Object processor(TIdentity identity, Map map);

    String getAction();
}
