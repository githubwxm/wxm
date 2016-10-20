package com.all580.base.controller;

import com.alibaba.dubbo.rpc.RpcException;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 全局控制器异常
 * @date 2016/10/20 9:15
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerException {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result processException(Exception e) {
        log.error("网关未知异常", e);
        return new Result(false, "服务器异常");
    }

    @ExceptionHandler(RpcException.class)
    @ResponseBody
    public Result processRpcException(RpcException e) {
        log.error("网关调用RPC服务异常", e);
        return new Result(false, "服务器异常:" + e.getCode());
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public Result processApiException(ApiException e) {
        log.error("API业务异常", e);
        return new Result(false, e.getCode(), e.getMsg());
    }

    @ExceptionHandler(ParamsMapValidationException.class)
    @ResponseBody
    public Result processValidationException(ParamsMapValidationException e) {
        log.error("参数验证失败", e);
        return new Result(false, Result.PARAMS_ERROR, e.getMessage());
    }
}
