package com.junoyi.framework.security.filter;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.crypto.DecryptedRequestWrapper;
import com.junoyi.framework.security.crypto.EncryptedResponseWrapper;
import com.junoyi.framework.security.crypto.RsaCryptoHelper;
import com.junoyi.framework.security.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * API 加密过滤器
 * 用于对请求体和响应体进行加密/解密处理
 * 
 * 加密协议：
 * - 请求头 X-Encrypted: true 表示请求体已加密（需要解密）
 * - 请求头 X-No-Encrypt: true 表示不需要加密响应（跳过加密）
 * - 响应头 X-Encrypted: true 表示响应体已加密
 * 
 * 默认行为（配置开启后）：
 * - 请求：只有带 X-Encrypted: true 的请求才会解密
 * - 响应：所有响应都会加密，除非带 X-No-Encrypt: true
 *
 * @author Fan
 */
@RequiredArgsConstructor
public class ApiEncryptFilter extends OncePerRequestFilter {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(ApiEncryptFilter.class);

    private final SecurityProperties securityProperties;
    private final RsaCryptoHelper rsaCryptoHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 检查是否启用 API 加密
        if (!securityProperties.getApiEncrypt().isEnable()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查加密服务是否可用
        if (!rsaCryptoHelper.isAvailable()) {
            log.warn("ApiEncryptUnavailable", "RSA key not loaded, API encryption functionality is not available");
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        HttpServletRequest wrappedRequest = request;
        HttpServletResponse wrappedResponse = response;
        boolean needEncryptResponse = false;

        try {
            // 处理请求体解密
            if (securityProperties.getApiEncrypt().isRequest() && needDecryptRequest(request)) {
                wrappedRequest = decryptRequest(request);
                log.debug("RequestDecrypted", "Request body decrypted: " + requestURI);
            }

            // 判断是否需要加密响应
            needEncryptResponse = securityProperties.getApiEncrypt().isResponse() && needEncryptResponse(request);
            if (needEncryptResponse)
                wrappedResponse = new EncryptedResponseWrapper(response);

            // 执行过滤器链
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            // 加密响应体
            if (needEncryptResponse && wrappedResponse instanceof EncryptedResponseWrapper wrapper) {
                encryptResponse(wrapper, response);
                log.debug("ResponseEncrypted", "The response body is encrypted: " + requestURI);
            }

        } catch (Exception e) {
            log.error("ApiEncryptError", "API encryption processing failed: " + requestURI, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"API 加密处理失败\"}");
        }
    }

    /**
     * 判断请求是否需要解密
     */
    private boolean needDecryptRequest(HttpServletRequest request) {
        // 检查请求头中是否有加密标识
        String encrypted = request.getHeader("X-Encrypted");
        if (!"true".equalsIgnoreCase(encrypted))
            return false;

        // 只对 POST/PUT/PATCH 请求进行解密
        String method = request.getMethod();
        return "POST".equalsIgnoreCase(method) 
            || "PUT".equalsIgnoreCase(method) 
            || "PATCH".equalsIgnoreCase(method);
    }

    /**
     * 判断响应是否需要加密
     * 配置开启后默认加密所有响应，除非请求头明确指定 X-No-Encrypt: true
     */
    private boolean needEncryptResponse(HttpServletRequest request) {
        // 检查请求头是否明确不需要加密
        String noEncrypt = request.getHeader("X-No-Encrypt");
        if ("true".equalsIgnoreCase(noEncrypt))
            return false;
        // 配置开启响应加密后，默认加密所有响应
        return true;
    }

    /**
     * 解密请求体
     */
    private HttpServletRequest decryptRequest(HttpServletRequest request) throws IOException {
        // 读取加密的请求体
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String encryptedBody = sb.toString().trim();
        if (encryptedBody.isEmpty())
            return request;

        // 解密请求体
        String decryptedBody = rsaCryptoHelper.decrypt(encryptedBody);
        return new DecryptedRequestWrapper(request, decryptedBody);
    }

    /**
     * 加密响应体
     */
    private void encryptResponse(EncryptedResponseWrapper wrapper, HttpServletResponse originalResponse) 
            throws IOException {
        String originalContent = wrapper.getCapturedContent();
        if (originalContent == null || originalContent.isEmpty())
            return;

        // 加密响应内容
        String encryptedContent = rsaCryptoHelper.encrypt(originalContent);
        
        // 写入加密后的响应
        wrapper.writeEncryptedContent(encryptedContent);
    }
}
