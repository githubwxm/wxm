package com.all580.voucherplatform.utils.sign.services;

import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
public class Md5ServiceImpl implements SignService {
    @Override
    public SignKey generate() {
        return null;
    }

    @Override
    public String signature(SignKey signKey, String source) {
        return null;
    }

    @Override
    public boolean verifySign(SignKey signKey, String source, String signed) {
        return false;
    }
}
