package com.all580.voucherplatform.test;

import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.PlatformRoleMapper;
import com.all580.voucherplatform.entity.PlatformRole;
import com.all580.voucherplatform.manager.report.PlatformReportManager;
import com.all580.voucherplatform.utils.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Calendar;
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
    @Autowired
    public PlatformRoleMapper platformRoleMapper;

    @Test
    public void Test(){
        PlatformRole platformRole =platformRoleMapper.getRoleByAuthInfo("572b8834-44b2-439d-bb54-9dbda98128f4", null);
        System.out.println(" 1 1111 1  11"+platformRole.getName()+"   "+platformRole.getAuthId());
    }
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
