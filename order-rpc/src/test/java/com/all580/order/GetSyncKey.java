package com.all580.order;

import com.framework.common.lang.codec.Md5Utils;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/20 9:14
 */
public class GetSyncKey {
    public static void main(String[] args) {
        System.out.println("畅旅测试环境:" + Md5Utils.getMD5For16("T_1476277250138WFBZ35GTM7PL25").toUpperCase()); // 畅旅
        System.out.println("畅旅生产环境:" + Md5Utils.getMD5For16("P_1476277250138WFBZ35GTM7PL25").toUpperCase()); // 畅旅
        System.out.println("畅旅开发环境:" + Md5Utils.getMD5For16("D_1476277250138WFBZ35GTM7PL25").toUpperCase()); // 畅旅
    }
}
