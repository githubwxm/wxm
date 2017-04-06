package com.all580.base.adapter.push;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 推送消息适配器
 * @date 2017/4/6 9:09
 */
public interface PushMsgAdapter {
    /**
     * 解析消息
     * @param msg 消息
     * @return
     */
    String parseMsg(Map map, String msg);

    /**
     * 推送消息
     * @param epId 企业ID
     * @param url 推送地址
     * @param msg 消息
     */
    void push(String epId, String url, String msg, Map config);
}
