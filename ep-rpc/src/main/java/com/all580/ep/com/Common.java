package com.all580.ep.com;

import com.framework.common.Result;
import com.framework.common.util.CommonUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if(null==obj){
            return false;
        }else{
            String  key=obj.toString();
            Pattern pattern = Pattern.compile("\\d+");
            return pattern.matcher(key).matches();
        }
    }

    /**
     * 对象转Map
     * @param bean
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map<String,Object> convertBean(Object bean) throws IntrospectionException,
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
    public static void checkPage(Map<String,Object> map){
        if(objectIsNumber(map.get("record_start"))&&objectIsNumber(map.get("record_count"))){
            map.put("limit","limit");
        }
    }

    /**
     * 判断查询结果统一处理
     *
     * @param result
     * @param list
     */
    public static void isEmpty(Result<List<Map<String,Object>>> result, List<Map<String,Object>> list) {
        //if (list.isEmpty()) {
            //result.setError("未查询到数据");
            //result.setSuccess();
        //} else {
            result.put(list);
            result.setSuccess();
        //}
    }

    /**
     * removeAll 前端传的类型实际上不一致removeAll不掉   过滤掉已经存在的无需添加
     * func_ids 中过滤掉initFunc中已经存在的 而无需添加
     *
     * @param func_ids
     * @param initFunc
     */
    public static void removeAllList(List<Integer> func_ids, List<Integer> initFunc) {
        if(func_ids==null || initFunc==null){
            return;
        }
        for (int i = func_ids.size() - 1; i > -1; i--) {
            Integer id = CommonUtil.objectParseInteger(func_ids.get(i));//
            if(id==null){
                func_ids.remove(i);
                continue;
            }

            for (Integer temp : initFunc) {
                if (temp.equals(id)) {
                    func_ids.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 获取把已经存在而用不到的数据删除掉
     * initFunc   中有 而  func_ids 中没有
     *
     * @param func_ids
     * @param initFunc
     */
    public static List<Integer> deleteAllList(List<Integer> func_ids, List<Integer> initFunc) {
        List<Integer> list = new ArrayList<>();
        if (func_ids == null || initFunc == null) {
            return list;
        }
        for (int i = initFunc.size() - 1; i > -1; i--) {
            Integer id = initFunc.get(i);
            boolean ref = true;
            for (int j = func_ids.size() - 1; j > -1; j--) {
                Integer temp = CommonUtil.objectParseInteger(func_ids.get(j));

                if (id.equals(temp)) {
                    ref = false;
                    break;
                }else if(temp==null){
                    func_ids.remove(j);
                }
            }
            if (ref) {
                list.add(id);
            }
        }
        return list;
    }

}
