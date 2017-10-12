package com.all580.order;

import com.framework.common.mns.OssStoreManager;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:26
 */
public class Main {
    public static void main(String[] args) {
//        com.alibaba.dubbo.container.Main.main(args);
        OssStoreManager manager = new OssStoreManager();
        manager.setAccessId("LTAIpUIMOAVmmPLz");
        manager.setAccessKey("3jl12lpG8PVuA4rLcwe8oXyvASNKKH");
        manager.setAccountEndpoint("http://oss-cn-shanghai.aliyuncs.com");
        manager.upload("dev-consume-sync/test4.txt", "test");
    }
}
