package com.all580.voucherplatform.test.utils;

import com.all580.voucherplatform.utils.sign.voucher.VoucherGenerate;
import com.all580.voucherplatform.utils.sign.voucher.VoucherUrlGenerate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by Linv2 on 2017-05-26.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class VoucherTest {
    @Autowired
    VoucherGenerate voucherGenerate;
    @Autowired
    VoucherUrlGenerate voucherUrlGenerate;

    @Test
    public void VoucherTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(voucherGenerate.getVoucher(16, "88", "A"));
        }
    }
    @Test
    public void VoucherUrlTest() {
        System.out.println(voucherUrlGenerate.getVoucherUrl("888888888",null,null,null));
        System.out.println(voucherUrlGenerate.getVoucherUrl("888888888","M",346,"RED"));
        System.out.println(voucherUrlGenerate.getVoucherUrl("888888888","M",100,"RED"));
        System.out.println(voucherUrlGenerate.getVoucherUrl("888888888","A",800,"#D45C41"));
    }

}