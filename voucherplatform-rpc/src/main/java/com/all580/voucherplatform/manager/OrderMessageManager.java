package com.all580.voucherplatform.manager;

import com.all580.notice.api.service.SmsService;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by Linv2 on 2017-06-15.
 */
@Service
@Slf4j
public class OrderMessageManager {

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private SmsService smsService;


    public void sendOrderMessage(final Order... orders) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                for (Order order : orders) {
                    String message = order.getSms();
                    message = message.replace("{份数}", String.valueOf(order.getNumber()))
                            .replace("{验证码}", MessageFormat.format("{0}，打开二维码: {1} ", order.getVoucherNumber(),
                                    order.getImgUrl()))
                            .replace("{生效日期}", DateFormatUtils.converToStringTime(order.getValidTime()))
                            .replace("{有效年月日}", DateFormatUtils.converToStringTime(order.getInvalidTime()));
                    try {
                        smsService.sendForCty(message, order.getMobile());
                    } catch (Exception ex) {
                        log.error("调用短信RPC失败：mobile={},content={}", new Object[]{order.getMobile(), message});
                    }
                }
            }
        });
    }

    public void sendOrderMessage(final String mobile,
                                 final Order... orders) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                for (Order order : orders) {
                    String message = order.getSms();
                    message = message.replace("{份数}", String.valueOf(order.getNumber()))
                            .replace("{验证码}", MessageFormat.format("{0}，打开二维码: {1} ", order.getVoucherNumber(),
                                    order.getImgUrl()))
                            .replace("{生效日期}", DateFormatUtils.converToStringTime(order.getValidTime()))
                            .replace("{有效年月日}", DateFormatUtils.converToStringTime(order.getInvalidTime()));
                    try {
                        smsService.sendForCty(message, mobile);
                    } catch (Exception ex) {
                        log.error("调用短信RPC失败：mobile={},content={}", new Object[]{mobile, message});
                    }
                }
            }
        });
    }
}
