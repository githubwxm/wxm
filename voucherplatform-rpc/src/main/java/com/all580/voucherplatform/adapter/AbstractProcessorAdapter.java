package com.all580.voucherplatform.adapter;

import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Slf4j
public abstract class AbstractProcessorAdapter<Identity> {

    @Autowired
    private ApplicationContext applicationContext;
    protected Map<String, ProcessorService> processMapper = new HashMap<>();

    /**
     * @param action
     * @param identity
     * @param map
     * @return
     */
    public Result process(String action, Identity identity, Map map) {
        Result result = new Result(false);
        ProcessorService processorService = processMapper.get(action);
        if (processorService == null) {
            result.setError("未实现的ACTION=" + action);
        } else {
            result.setSuccess(true);
            try {
                Object ret = processorService.processor(identity, map);
                if (ret != null) {
                    result.put(ret);
                }
            } catch (Exception ex) {
                throw new ApiException(ex);
            }
        }
        return result;
    }


    @PostConstruct
    protected void init() {
        Map<String, ProcessorService> map = applicationContext.getBeansOfType(ProcessorService.class);
        String packetName = this.getClass().getPackage().getName();
        for (ProcessorService processorService : map.values()) {
            if (processorService.getClass().getName().startsWith(packetName)) {
                processMapper.put(processorService.getAction(), processorService);
            }
        }
    }
}
