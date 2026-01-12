package com.junoyi.framework.web.exception;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.core.domain.base.BaseException;
import com.junoyi.framework.core.domain.module.R;
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


/**
 * 全局异常处理类
 * <p>
 * 异常处理优先级（从高到低）：
 * 1. 权限相关异常（NotLoginException、NoPermissionException、PermissionException）
 * 2. 业务异常（BaseException 及其子类，包括 AuthException、CaptchaException、各业务模块异常）
 * 3. Spring MVC 异常（参数校验、请求方式等）
 * 4. 运行时异常和系统异常（兜底）
 *
 * @author Fan
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================== 权限相关异常 ====================

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

    // ==================== 业务异常（统一处理 BaseException） ====================

    /**
     * 业务异常统一处理
     * <p>
     * 处理所有继承自 BaseException 的业务异常，包括：
     * - AuthException（认证异常：Token过期、登录失败等）
     * - CaptchaException（验证码异常）
     * - DeptException、MenuException、UserException 等业务模块异常
     */
    @ExceptionHandler(BaseException.class)
    public R<?> handleBaseException(BaseException e, HttpServletRequest request) {
        String domain = e.getFullDomain();
        String prefix = e.getDomainPrefix();
        
        // 根据领域前缀区分日志级别和格式
        if ("AUTH".equals(prefix) || prefix.startsWith("AUTH.")) {
            log.warn("[认证异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), domain, e.getMessage());
        } else if ("CAPTCHA".equals(prefix)) {
            log.warn("[验证码异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), domain, e.getMessage());
        } else {
            log.warn("[业务异常] 请求地址: {}, 领域: {}, 异常信息: {}", request.getRequestURI(), domain, e.getMessage());
        }
        
        return R.fail(e.getCode(), e.getMessage());
    }

    // ==================== Spring MVC 异常 ====================

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

    // ==================== 系统异常（兜底） ====================

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
