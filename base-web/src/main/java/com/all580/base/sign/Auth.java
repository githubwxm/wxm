package com.all580.base.sign;

import com.framework.common.io.cache.redis.RedisUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存角色地址
 * Created by wxming on 2016/12/7 0007.
 */
public class Auth {
    private static Map<Integer,List<String>> authMap= new HashMap();
    private final static String key ="auth_base";
    private  static List<String> notAuthAddress= new ArrayList<>();
    public static void setnotAuthAddress(List<String> urls){
        notAuthAddress=urls;
    }
//    static{
//        notAuthAddress.add("/api/balance/info");
//        notAuthAddress.add("/api/ep/platform/validate");
//        notAuthAddress.add("/api/sms/send");
//        notAuthAddress.add("/api/order/cancel/times");
//        notAuthAddress.add("/api/ep/platform/payment/list_by_core_ep_id");
//    }

    public static boolean isNotAuth(String url){
        return notAuthAddress.contains(url);
    }
    public static List<String>  getAuthMap(RedisUtils redisUtils, Integer epRole ) {
        return redisUtils.hmget(key, key+epRole);
    }


    public  static void updateAuthMap(List<Integer> list,RedisUtils redisUtils){
        Map<String, String> map =redisUtils.hgetAll(key);
        for(Integer epRoleId :list){
            map.put(key+epRoleId,"");//   map.remove(key+epRoleId);
        }
        redisUtils.hmset(key,map);
    }
    public static void setAuthMap(RedisUtils redisUtils, List<String> list, Integer epRole ){
        if( null!=list){
            Map<String, String> map =redisUtils.hgetAll(key);
            map.put(key+epRole,list.toString());
            redisUtils.hmset(key,map);
        }
    }
}
