package com.all580.base.manager;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.framework.common.event.EventService;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.mns.MnsSubscribeAction;
import com.framework.common.synchronize.LTSStatic;
import com.framework.common.util.CommonUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/23 17:18
 */
@Component
@Slf4j
public class MnsEventCache extends InstantiationAwareBeanPostProcessorAdapter {
    private Map<MnsSubscribeAction, Integer> orderMap = new ConcurrentHashMap<>();
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
    @Getter
    private Multimap<String, String> cacheEventsMap = ArrayListMultimap.create();

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
                cacheEventsMap.put(key, aClass.getName());
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

    public Class getParamsClass(MnsSubscribeAction action) {
        try {
            Class<?> aClass = CommonUtil.getProxyClassForInterface(action, MnsSubscribeAction.class);
            Type type = ((ParameterizedType) aClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            return (Class) type;
        } catch (Exception e) {
            log.error("获取事件处理器的参数类型异常", e);
            throw new ApiException("事件处理器数据类型异常", e);
        }
    }

    public Object getObject(String content, MnsSubscribeAction action, boolean json) {
        if (json) {
            Class pClass = getParamsClass(action);
            log.debug("JSON 类型事件:{}", pClass.getName());
            return JsonUtils.fromJson(content, pClass);
        } else {
            return LTSStatic.SyncData.asObject(TranscodeUtil.base64StrToByteArray(content));
        }
    }

    public Object getObject(String content, MnsSubscribeAction action) {
        if (JSONUtils.mayBeJSON(content)) return getObject(content, action, true);
        try {
            return LTSStatic.SyncData.asObject(TranscodeUtil.base64StrToByteArray(content));
        } catch (Exception e) {
            log.debug("事件内容: {} 不是二进制类型: {}", content, e.getMessage());
            return getObject(content, action, true);
        }
    }
}
