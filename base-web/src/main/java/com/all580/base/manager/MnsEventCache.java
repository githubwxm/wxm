package com.all580.base.manager;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.framework.common.mns.MnsSubscribeAction;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import java.util.Collection;

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
    private Multimap<String, MnsSubscribeAction> cacheEvents = ArrayListMultimap.create();

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
