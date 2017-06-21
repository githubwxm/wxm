package com.all580.voucherplatform.utils.voucher;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;


/**
 * Created by Linv2 on 2017-05-26.
 */
@Service
public class VoucherUrlGenerate {
    private String voucherServiceUrl = "http://img.all580.cn/voucher/index.html";
    private String[] errorRateList = new String[]{"L", "M", "Q", "H"};

    public VoucherUrlGenerate() {
    }

    public String getVoucherUrl(String voucherCode, String errorRate, Integer size, String foreColor) {
        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(voucherServiceUrl);
        } catch (Exception ex) {

        }
        uriBuilder.addParameter("text", voucherCode);
        if (!StringUtils.isEmpty(errorRate)) {
            errorRate = errorRate.toUpperCase();
            if (ArrayUtils.contains(errorRateList, errorRate)) {
                uriBuilder.addParameter("errRate", errorRate);
            }
        }
        if (size != null && size > 200 && size < 600) {
            uriBuilder.addParameter("size", String.valueOf(size));
        }
        if (!StringUtils.isEmpty(foreColor)) {
            uriBuilder.addParameter("colorDark", foreColor);
        }

        return uriBuilder.toString();
    }
}
