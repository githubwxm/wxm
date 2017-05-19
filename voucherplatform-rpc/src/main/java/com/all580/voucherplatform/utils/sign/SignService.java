package com.all580.voucherplatform.utils.sign;

/**
 * Created by Linv2 on 2017-05-19.
 */
public interface SignService {
    SignKey generate();

    String signature(SignKey signKey, String source);

    boolean verifySign(SignKey signKey, String source, String signed);
}
