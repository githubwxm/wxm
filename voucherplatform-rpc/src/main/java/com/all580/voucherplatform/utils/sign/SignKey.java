package com.all580.voucherplatform.utils.sign;

/**
 * Created by Linv2 on 2017-05-19.
 */
public class SignKey {
    private String privateKey;
    public String publicKey;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
