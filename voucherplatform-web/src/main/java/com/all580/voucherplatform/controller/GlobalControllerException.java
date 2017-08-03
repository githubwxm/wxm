package com.all580.voucherplatform.controller;

import com.alibaba.dubbo.rpc.RpcException;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;

/**
 * Created by Linv2 on 2017-08-02.
 */

@ControllerAdvice
@Slf4j
public class GlobalControllerException {
    @ExceptionHandler(RpcException.class)
    @ResponseBody
    public Result processRpcException(RpcException e) {
        log.error("网关调用RPC服务异常", e);
        return new Result(false, "服务器RPC异常:" + e.getCode());
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public Result processApiException(ApiException e) {
        log.error("API业务异常", e);
        Result result = new Result(false, e.getCode() == 0 ? Result.BUSINESS_EXCEPTION : e.getCode(), e.getMessage());
        if (e.getData() != null) {
            result.put(e.getData());
        }
        return result;
    }

    @ExceptionHandler(ParamsMapValidationException.class)
    @ResponseBody
    public Result processValidationException(ParamsMapValidationException e) {
        log.error("参数验证失败", e);
        return new Result(false, Result.PARAMS_ERROR, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result processMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("参数验证异常", e);
        return new Result(false, Result.PARAMS_ERROR, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result processIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数验证异常", e);
        return new Result(false, Result.PARAMS_ERROR, e.getMessage());
    }
}
