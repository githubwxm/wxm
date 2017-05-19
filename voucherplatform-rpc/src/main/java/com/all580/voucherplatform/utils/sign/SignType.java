package com.all580.voucherplatform.utils.sign;

/**
 * Created by Linv2 on 2017-05-19.
 */
public enum SignType {
    none(0),
    md5(1),
    rsa(2);

    SignType(int val) {
        this.value = val;
    }

    private final int value;

    int getValue() {
        return value;
    }

    public static SignType valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return md5;
            case 2:
                return rsa;
            default:
                return none;
        }
    }
}
