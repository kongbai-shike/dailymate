package com.xsy.dailymate.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常：{}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败：{}", e.getMessage());
        return Result.error(401, "认证失败：" + e.getMessage());
    }

    /**
     * 处理凭证错误
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("凭证错误：{}", e.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    /**
     * 处理重复键异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("重复键异常：{}", e.getMessage());
        return Result.error(400, "数据已存在");
    }

    /**
     * 处理 SQL 完整性约束违反
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public Result<Void> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("SQL 完整性约束违反：{}", e.getMessage());
        return Result.error(400, "数据完整性错误");
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }

    /**
     * 处理所有未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Void> handleException(Exception e) {
        log.error("未知异常：", e);
        return Result.error(500, "服务器内部错误");
    }
}
