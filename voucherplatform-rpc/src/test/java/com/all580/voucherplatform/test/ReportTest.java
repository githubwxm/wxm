package com.all580.voucherplatform.test;

import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.manager.report.PlatformReportManager;
import com.all580.voucherplatform.utils.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-08-14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class ReportTest {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    public PlatformReportManager platformReportManager;

    @Test
    public void TestOrder() {
        Map map = MapUtils.listToMap(orderMapper.selectOrderReportCount(null, null, 1, null), "hour", "number");
    }

    @Test
    public void TestPlatfrmReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 5, 20);
        platformReportManager.execute(calendar.getTime());
    }
}
