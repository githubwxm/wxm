package com.all580.base.controller;

import com.alibaba.dubbo.rpc.RpcException;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;

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
        return new Result(false, Result.FAIL, "服务器异常");
    }

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
        return new Result(false, Result.BUSINESS_EXCEPTION, e.getMsg());
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

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public Result processDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据库唯一约束异常", e);
        return new Result(false, Result.UNIQUE_KEY_ERROR, e.getMessage());
    }
}
