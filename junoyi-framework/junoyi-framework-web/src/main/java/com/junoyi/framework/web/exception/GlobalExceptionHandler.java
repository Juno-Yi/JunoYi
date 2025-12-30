package com.junoyi.framework.web.exception;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.core.domain.base.BaseException;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.exception.captcha.CaptchaException;
import com.junoyi.framework.core.exception.menu.MenuException;
import com.junoyi.framework.permission.exception.NoPermissionException;
import com.junoyi.framework.permission.exception.NotLoginException;
import com.junoyi.framework.permission.exception.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.awt.*;


/**
 * 全局异常处理类
 *
 * @author Fan
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    public R<?> handleBaseException(BaseException e, HttpServletRequest request) {
        log.warn("[业务异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), e.getFullDomain(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     *  人机验证（验证码）异常
     */
    @ExceptionHandler(CaptchaException.class)
    public R<?> handleCaptchaException(CaptchaException e, HttpServletRequest request){
        log.warn("验证码验证失败，请求URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail(e.getCode(),e.getMessage());
    }

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public R<?> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        log.warn("[未登录] 请求地址: {}", request.getRequestURI());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 无权限异常
     */
    @ExceptionHandler(NoPermissionException.class)
    public R<?> handleNoPermissionException(NoPermissionException e, HttpServletRequest request) {
        String[] permissions = e.getRequiredPermissions();
        if (permissions != null && permissions.length > 0) {
            log.warn("[无权限] 请求地址: {}, 缺少权限: {}", request.getRequestURI(), String.join(", ", permissions));
        } else {
            log.warn("[无权限] 请求地址: {}", request.getRequestURI());
        }
        return R.fail(e.getCode(), "没有访问权限");
    }

    /**
     * 权限异常（兜底）
     */
    @ExceptionHandler(PermissionException.class)
    public R<?> handlePermissionException(PermissionException e, HttpServletRequest request) {
        log.warn("[权限异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), e.getFullDomain(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 菜单异常
     */
    @ExceptionHandler(MenuException.class)
    public R<?> handleMenuException(MenuException e, HttpServletRequest request){
        log.warn("[菜单异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), e.getFullDomain(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }


    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("[请求方式错误] 请求地址: {}, 不支持 {} 请求", request.getRequestURI(), e.getMethod());
        return R.fail(HttpStatus.BAD_METHOD, "不支持 " + e.getMethod() + " 请求");
    }

    /**
     * 请求路径不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public R<?> handleNoHandlerFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("[路径不存在] 请求地址: {}", request.getRequestURI());
        return R.fail(HttpStatus.NOT_FOUND, "请求路径不存在");
    }

    /**
     * 参数校验异常 - @Valid 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("[参数校验失败] {}", message);
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("[参数绑定失败] {}", message);
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("[缺少参数] 参数名: {}", e.getParameterName());
        return R.fail(HttpStatus.BAD_REQUEST, "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("[参数类型错误] 参数名: {}, 期望类型: {}", e.getName(), e.getRequiredType());
        return R.fail(HttpStatus.BAD_REQUEST, "参数类型错误: " + e.getName());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("[运行时异常] 请求地址: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail("系统异常，请稍后重试");
    }

    /**
     * 系统异常 - 兜底处理
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        log.error("[系统异常] 请求地址: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail("系统繁忙，请稍后重试");
    }
}
