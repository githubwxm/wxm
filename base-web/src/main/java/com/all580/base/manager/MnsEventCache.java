package com.all580.base.manager;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/23 17:18
 */
@Component
@Slf4j
public class MnsEventCache extends InstantiationAwareBeanPostProcessorAdapter {
    private Map<MnsSubscribeAction, Integer> orderMap = new HashMap<>();
    @Getter
    private TreeMultimap<String, MnsSubscribeAction> cacheEvents = TreeMultimap.create(Ordering.natural(), new Comparator<MnsSubscribeAction>() {
        @Override
        public int compare(MnsSubscribeAction o1, MnsSubscribeAction o2) {
            try {
                Integer order1 = orderMap.get(o1);
                Integer order2 = orderMap.get(o2);
                if (order1 != null && order2 != null) {
                    return order1 < order2 ? -1 : 1;
                }
            } catch (Exception ignored) {}
            return 1;
        }
    });

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        try {
            Class<?> aClass = o instanceof ReferenceBean ? ((ReferenceBean)o).getObjectType() : o.getClass();
            if (MnsSubscribeAction.class.isAssignableFrom(aClass) && aClass.isAnnotationPresent(EventService.class)) {
                EventService eventService = aClass.getAnnotation(EventService.class);
                Object object = o instanceof ReferenceBean ? ((ReferenceBean)o).get() : o;
                MnsSubscribeAction action = (MnsSubscribeAction) object;
                String key = StringUtils.isEmpty(eventService.value()) ? s : eventService.value();
                orderMap.put(action, eventService.order());
                if (!cacheEvents.put(key, action)) {
                    log.error("事件添加缓存失败:{}-{}", eventService, aClass);
                    System.exit(0);
                    return o;
                }
                log.info("事件注册:Key:{}, Event:{}", new Object[]{key, aClass});
            }
        } catch (Exception e) {
            log.error("事件添加缓存失败", e);
            System.exit(0);
        }
        return o;
    }

    public Collection<MnsSubscribeAction> getProcess(String key) {
        return cacheEvents.get(key);
    }
}
