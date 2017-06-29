package com.all580.voucherplatform.utils.sign.services;

import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
public class RsaServiceImpl implements SignService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    /**
     * 签名和算法编码
     */
    public static final String SIGN_ENCODING = "utf-8";


    @Override
    public SignKey generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            KeyPair kayPair = generator.genKeyPair();
            generator.initialize(1024, new SecureRandom());
            // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) kayPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) kayPair.getPrivate();
            // 得到公钥

            SignKey rsaKeys = new SignKey();
            rsaKeys.setPrivateKey(new String(Base64.encodeBase64(privateKey.getEncoded()), SIGN_ENCODING));
            rsaKeys.setPublicKey(new String(Base64.encodeBase64(publicKey.getEncoded()), SIGN_ENCODING));
            return rsaKeys;
        } catch (Exception ex) {
            logger.error("RSA密钥生成异常", ex);
            return null;
        }
    }

    @Override
    public String signature(SignKey signKey, String source) {
        try {
            PrivateKey priKey = loadPrivateKeyByStr(signKey.getPrivateKey());
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(source.getBytes(SIGN_ENCODING));

            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception ex) {
            logger.error("RSA签名异常", ex);
            return null;
        }
    }

    /**
     * @param signKey
     * @param source
     * @param signed  签名后数据的base64编码串
     * @return
     */
    @Override
    public boolean verifySign(SignKey signKey, String source, String signed) {
        try {
            PublicKey pubKey = loadPublicKeyByStr(signKey.getPublicKey());

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(source.getBytes(SIGN_ENCODING));

            return signature.verify(Base64.decodeBase64(signed));
        } catch (Exception ex) {
            logger.error("RSA验证签名异常", ex);
            return false;
        }
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

    private PublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }
}
