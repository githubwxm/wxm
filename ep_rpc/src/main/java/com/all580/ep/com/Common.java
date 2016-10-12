package com.all580.ep.com;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class Common {
    private static final char[] CHAR_32 = new char[] { 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8',
            '9' };
    public static String getAccessId(){
        return System.currentTimeMillis()+getRandom(8);
    }
    public static String getAccessKey(){
        return System.currentTimeMillis()+getRandom(14);
    }

    /**
     * 获取随机字符串
     *
     * @param size
     *            长度
     * @return String 随机字符串
     */
    public static String getRandom(int size) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size; i++) {
            s.append(CHAR_32[((int) (Math.random() * 1000000)) % CHAR_32.length]);
        }
        return s.toString();
    }
    /**
     * 正则差找字符串
     *
     * @param regmach
     * @param code
     * @return String
     */
    public static String matcher(String code, String regmach) {
        Matcher matcher = Pattern.compile(regmach).matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    /**
     * 字符串是否为数字
     *
     * @param key
     * @return
     */
    public static boolean isTrue(String key, String grep) {
        if(null==key){
            return false;
        }
        Pattern pattern = Pattern.compile(grep);
        return pattern.matcher(key).matches();
    }
    /**
     * 字符串是否为数字
     *
     * @param obj
     * @return
     */
    public static boolean objectIsNumber(Object obj) {
        String key="";
        if(null==obj){
            return false;
        }else{
            key=obj.toString();
        }
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(key).matches();
    }

    /**
     * 对象转Map
     * @param bean
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map convertBean(Object bean) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

    /**
     * Map 转对象
     * @param type
     * @param map
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static Object convertMap(Class type, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors = beanInfo
                .getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }


    public static Integer objectParseInteger(Object obj){
        if(null==obj){
            return null;
        }else{
            return Integer.parseInt(obj.toString());
        }
    }

    /**
     * 检查分页参数  起始 余总数  record_start  record_count
     * @param map
     */
    public static void checkPage(Map map){
        if(objectIsNumber(map.get("record_start"))&&objectIsNumber(map.get("record_count"))){
            map.put("limit","");
        }
    }
}
