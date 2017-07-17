package com.all580.voucherplatform.adapter.supply.ticketV3.manager;

import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignType;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.StringUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-10.
 */
@Service
@Slf4j
public class ConfManager {

    @Autowired
    private SupplyMapper supplyMapper;

    private static final String strConf = "/**\n" +
            " * Created by %s on %s\n" +
            " */\n" +
            "module.exports = {\n" +
            "    ali:{\n" +
            "        queueName : '%s',\n" +
            "        topicName: '%s',\n" +
            "        identity: '%s',\n" +
            "        tag:'%s',\n" +
            "        accessKeyId: '%s',\n" +
            "        secretAccessKey: '%s',\n" +
            "        endpoint: '%s',\n" +
            "    }\n" +
            "}";

    public Map getConfMap(Integer supplyId) {
        Map map = new HashMap();
        Supply supply = supplyMapper.selectByPrimaryKey(supplyId);
        setConf(map, supply);
        setPrivKey(map, supply);
        return map;
    }

    private void setConf(Map map, Supply supply) {
        String queueName = null;
        Map mapConf = JsonUtils.json2Map(supply.getConf());
        if (mapConf != null) {
            queueName = CommonUtil.objectParseString(mapConf.get("queueName"));
        }
        if (StringUtils.isEmpty(queueName)) {
            throw new ApiException("配置项缺失：queueName");
        }
        String conf = String.format(strConf, "voucher platform",
                DateFormatUtils.DATE_FORMAT.format(new Date()),
                queueName,
                "allorder-rsp",
                supply.getId().toString(),
                "ticket",
                "dDC0LZdqDRje0YJC",
                "TR1BkfTY6IuajALJqHYxSTWwpSPGwl",
                "http://1356025265125084.mns.cn-shenzhen.aliyuncs.com/");
        map.put("aliconfig.js", conf);

    }

    private void setPrivKey(Map map, Supply supply) {
        if (supply.getSignType() != SignType.rsa.getValue()) {
            throw new ApiException("错误的签名方式，请使用RSA签名");
        }
        try {
            String strPem = SignInstance.ToPem(supply.getPrivateKey());
            map.put("private.pem", strPem);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
    }
}
