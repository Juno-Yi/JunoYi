package com.junoyi.framework.web.xss;

import com.junoyi.framework.web.properties.XssProperties;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS 防护的 HTTP 请求包装器
 *
 * @author Fan
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;
    private final XssProperties xssProperties;

    /**
     * 构造 XSS 防护请求包装器
     *
     * @param request HTTP 请求对象
     * @param xssProperties XSS 防护配置属性
     * @throws IOException 读取请求体时可能抛出的 IO 异常
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) throws IOException {
        super(request);
        this.xssProperties = xssProperties;
        // 缓存请求体
        if (xssProperties.isFilterBody()) {
            this.body = request.getInputStream().readAllBytes();
        } else {
            this.body = null;
        }
    }

    /**
     * 获取经过 XSS 过滤的请求参数值
     *
     * @param name 参数名称
     * @return 过滤后的参数值
     */
    @Override
    public String getParameter(String name) {
        if (!xssProperties.isFilterParameter()) return super.getParameter(name);
        String value = super.getParameter(name);
        return filterValue(value);
    }

    /**
     * 获取经过 XSS 过滤的请求参数值数组
     *
     * @param name 参数名称
     * @return 过滤后的参数值数组
     */
    @Override
    public String[] getParameterValues(String name) {
        if (!xssProperties.isFilterParameter()) return super.getParameterValues(name);
        String[] values = super.getParameterValues(name);
        if (values == null) return null;

        String[] filteredValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            filteredValues[i] = filterValue(values[i]);
        }
        return filteredValues;
    }

    /**
     * 获取经过 XSS 过滤的参数映射
     *
     * @return 过滤后的参数映射
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        if (!xssProperties.isFilterParameter()) return super.getParameterMap();
        Map<String, String[]> originalMap = super.getParameterMap();
        Map<String, String[]> filteredMap = new HashMap<>(originalMap.size());

        for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null) {
                String[] filteredValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    filteredValues[i] = filterValue(values[i]);
                }
                filteredMap.put(entry.getKey(), filteredValues);
            } else {
                filteredMap.put(entry.getKey(), null);
            }
        }
        return filteredMap;
    }

    /**
     * 获取经过 XSS 过滤的请求头值
     *
     * @param name 请求头名称
     * @return 过滤后的请求头值
     */
    @Override
    public String getHeader(String name) {
        if (!xssProperties.isFilterHeader()) return super.getHeader(name);
        String value = super.getHeader(name);
        return filterValue(value);
    }

    /**
     * 获取经过 XSS 过滤的输入流
     *
     * @return 过滤后的输入流
     * @throws IOException 读取流时可能抛出的 IO 异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!xssProperties.isFilterBody() || body == null) return super.getInputStream();

        // 过滤请求体中的 XSS
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        String filteredBody = filterValue(bodyStr);
        byte[] filteredBytes = filteredBody.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(filteredBytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // 不支持异步
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    /**
     * 获取经过 XSS 过滤的字符读取器
     *
     * @return 过滤后的字符读取器
     * @throws IOException 读取时可能抛出的 IO 异常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 获取请求体内容长度
     *
     * @return 请求体内容长度
     */
    @Override
    public int getContentLength() {
        if (body == null) return super.getContentLength();
        return body.length;
    }

    /**
     * 获取请求体内容长度（长整型）
     *
     * @return 请求体内容长度
     */
    @Override
    public long getContentLengthLong() {
        if (body == null) return super.getContentLengthLong();
        return body.length;
    }

    /**
     * 根据配置的模式过滤值
     *
     * @param value 待过滤的值
     * @return 过滤后的值
     */
    private String filterValue(String value) {
        if (value == null) return null;

        return switch (xssProperties.getMode()) {
            case CLEAN -> XssUtils.clean(value);
            case ESCAPE -> XssUtils.escape(value);
            case REJECT -> value; // REJECT 模式在 Filter 中处理
        };
    }

    /**
     * 获取原始请求体（用于 REJECT 模式检测）
     *
     * @return 原始请求体字节数组
     */
    public byte[] getOriginalBody() {
        return body;
    }
}
