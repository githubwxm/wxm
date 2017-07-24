package com.all580.voucherplatform.utils.sign;

import com.all580.voucherplatform.utils.sign.services.DefaultServiceImpl;
import com.all580.voucherplatform.utils.sign.services.Md5ServiceImpl;
import com.all580.voucherplatform.utils.sign.services.RsaServiceImpl;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
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


    public SignService getSignService(Integer signType) {
        if (signType == null) {
            signType = 0;
        }
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

    private Map getMap(String name,
                       SignType signType) {
        Map map = new HashMap();
        map.put("name", name);
        map.put("value", signType.getValue());
        return map;
    }

    public static String ToPem(String privateKeyStr) throws Exception {
        PrivateKey priKey = loadPrivateKeyByStr(privateKeyStr);
        PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(
                priKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        StringWriter writer = new StringWriter();

        PemWriter pemWriter = new PemWriter(writer);
        PemObject pemObject = new PemObject("PRIVATE KEY", priKey.getEncoded());
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        return writer.toString();
    }

    private static PrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            KeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }


    public boolean checkSign(Integer signType,
                             String publicKey,
                             String privateKey,
                             String source,
                             String signed) {
        SignService signService = this.getSignService(signType);
        if (signService == null) {
            return true;
        }
        SignKey signKey = new SignKey();
        signKey.setPublicKey(publicKey);
        signKey.setPrivateKey(privateKey);
        return signService.verifySign(signKey, source, signed);
    }
}
