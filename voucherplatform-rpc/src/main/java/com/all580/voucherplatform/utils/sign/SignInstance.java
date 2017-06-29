package com.all580.voucherplatform.utils.sign;

import com.all580.voucherplatform.utils.sign.services.DefaultServiceImpl;
import com.all580.voucherplatform.utils.sign.services.Md5ServiceImpl;
import com.all580.voucherplatform.utils.sign.services.RsaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
public class SignInstance {
    @Autowired
    private DefaultServiceImpl defaultService;
    @Autowired
    private Md5ServiceImpl md5Service;
    @Autowired
    private RsaServiceImpl rsaService;


    public SignService getSignService(int signType) {
        switch (signType) {
            case 2: return rsaService;
            case 1: return md5Service;
            default: return defaultService;
        }
    }

    public SignService getSignService(SignType signType) {
        return getSignService(signType.getValue());
    }

    public List<Map> getSignTypeList() {
        List<Map> mapList = new ArrayList<>();
        mapList.add(getMap("不加密", SignType.none));
        mapList.add(getMap("MD5", SignType.md5));
        mapList.add(getMap("RSA-SHA1", SignType.rsa));
        return mapList;
    }

    private Map getMap(String name, SignType signType) {
        Map map = new HashMap();
        map.put("name", name);
        map.put("value", signType.getValue());
        return map;
    }
}
