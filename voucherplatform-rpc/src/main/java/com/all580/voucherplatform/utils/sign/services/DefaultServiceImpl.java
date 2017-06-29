package com.all580.voucherplatform.utils.sign.services;

import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
public class DefaultServiceImpl implements SignService {
    @Override
    public SignKey generate() {
        SignKey signKey = new SignKey();
        signKey.setPrivateKey(UUID.randomUUID().toString());
        signKey.setPublicKey(UUID.randomUUID().toString());
        return signKey;
    }

    @Override
    public String signature(SignKey signKey, String source) {
        return null;
    }

    @Override
    public boolean verifySign(SignKey signKey, String source, String signed) {
        return true;
    }
}
