package com.all580.order;

import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.junit.Test;

import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 14:39
 */
public class ParamsMapValidTest {
    @Test
    public void main() {
        Map params = new HashMap();
        params.put("a", new HashMap(){{
            put("b", new HashMap(){{
                put("c", "c");
                put("phone", "15019418143");
                put("d", new ArrayList(){{
                    add(new HashMap(){{
                        put("name", "Alone");
                        put("array", new ArrayList(){{
                            add(new HashMap(){{
                                put("a", "haha");
                            }});
                            add(new HashMap(){{
                                put("a", "bb");
                            }});
                        }});
                    }});
                    add(new HashMap(){{
                        put("name", 1);
                    }});
                }});
            }});
        }});
        Map<String[], ValidRule[]> valids = new HashMap<>();
        valids.put(new String[]{"a.b.c", "a.b.d[].name"}, new ValidRule[]{new ValidRule.NotNull()});
        valids.put(new String[]{"a.b.phone"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Pattern("^1(3|4|5|7|8)\\d{9}$")});
        ParamsMapValidate.validate(params, valids);

        System.out.println(getValue(params, "a.b.d.array.a"));
    }

    private static Collection getCollection(Object value, String key) {
        List<Object> tmp = new ArrayList<>();
        for (Object o : ((Collection) value)) {
            if (o == null) {
                tmp.add(null);
                continue;
            }
            if (o instanceof Collection) {
                tmp.addAll(getCollection(o, key));
                continue;
            }
            tmp.add(((Map) o).get(key.replace("[]", "")));
        }
        return tmp;
    }

    public static Object getValue(Map params, String key) {
        String[] keys = key.split("\\.");
        Object value = params;
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];

            if (value instanceof Collection) {
                value = getCollection(value, k);
                continue;
            }
            value = ((Map) value).get(k.replace("[]", ""));
            if (value == null && i == keys.length - 2) {
                value = new HashMap<>();
            }
        }
        return value;
    }

}
