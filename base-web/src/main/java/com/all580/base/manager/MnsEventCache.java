package com.all580.base.manager;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.framework.common.mns.MnsSubscribeAction;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/23 17:18
 */
@Component
@Slf4j
public class MnsEventCache extends InstantiationAwareBeanPostProcessorAdapter {
    @Getter
    private TreeMultimap<String, MnsSubscribeAction> cacheEvents = TreeMultimap.create(Ordering.natural(), new Comparator<MnsSubscribeAction>() {
        @Override
        public int compare(MnsSubscribeAction o1, MnsSubscribeAction o2) {
            try {
                Method orderMethod1 = BeanUtils.findMethod(o1.getClass(), "order");
                Method orderMethod2 = BeanUtils.findMethod(o2.getClass(), "order");
                if (orderMethod1 != null && orderMethod2 != null) {
                    int order1 = (int) orderMethod1.invoke(o1);
                    int order2 = (int) orderMethod2.invoke(o2);
                    return order1 < order2 ? -1 : order1 > order2 ? 1 : 0;
                }
            } catch (Exception ignored) {}
            return 0;
        }
    });

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class<?> aClass = o instanceof ReferenceBean ? ((ReferenceBean)o).getObjectType() : o.getClass();
        if (MnsSubscribeAction.class.isAssignableFrom(aClass)) {
            Object object = o instanceof ReferenceBean ? ((ReferenceBean)o).get() : o;
            MnsSubscribeAction action = (MnsSubscribeAction) object;
            String key = action.key();
            key = key == null ? s : key;
            if (!cacheEvents.put(key, action)) {
                throw new BeanCreationException(s, "事件添加缓存失败:" + key);
            }
            log.info("事件注册:Key:{}, Event:{}", key, aClass);
        }
        return o;
    }

    public Collection<MnsSubscribeAction> getProcess(String key) {
        return cacheEvents.get(key);
    }
}
