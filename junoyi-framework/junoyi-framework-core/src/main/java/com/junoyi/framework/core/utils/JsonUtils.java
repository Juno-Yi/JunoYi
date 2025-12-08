package com.junoyi.framework.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Json工具类
 *
 * @author Fan
 * @deprecated 请使用 {@link com.junoyi.framework.json.utils.JsonUtils} 代替,该类提供更完善的功能
 */
@Deprecated
public class JsonUtils {

    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
    }

    /**
     * 转换成Json格式字符串
     * @param obj 对象
     * @return json字符串
     */
    public static String toJson(Object obj){
        try{
            return JSON.writeValueAsString(obj);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return null;
    }

}