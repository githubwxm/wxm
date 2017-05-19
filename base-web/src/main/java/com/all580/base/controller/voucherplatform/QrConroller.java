package com.all580.base.controller.voucherplatform;

import com.all580.voucherplatform.api.service.QrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Linv2 on 2017/5/18.
 */

@Controller
@RequestMapping("voucherplatform/user")
@Slf4j
public class QrConroller {
    @Autowired
    private QrService qrService;
}
