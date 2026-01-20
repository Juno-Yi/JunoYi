package com.junoyi.framework.file.strategy;

import com.junoyi.framework.file.enums.FileBusinessType;
import com.junoyi.framework.file.strategy.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 上传策略工厂
 *
 * @author Fan
 */
public class UploadStrategyFactory {

    private static final Map<FileBusinessType, UploadStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        STRATEGY_MAP.put(FileBusinessType.AVATAR, new AvatarUploadStrategy());
        STRATEGY_MAP.put(FileBusinessType.DOCUMENT, new DocumentUploadStrategy());
        STRATEGY_MAP.put(FileBusinessType.IMAGE, new ImageUploadStrategy());
        STRATEGY_MAP.put(FileBusinessType.VIDEO, new VideoUploadStrategy());
        STRATEGY_MAP.put(FileBusinessType.AUDIO, new AudioUploadStrategy());
        STRATEGY_MAP.put(FileBusinessType.OTHER, new DefaultUploadStrategy());
    }

    /**
     * 获取上传策略
     *
     * @param businessType 业务类型
     * @return 上传策略
     */
    public static UploadStrategy getStrategy(FileBusinessType businessType) {
        return STRATEGY_MAP.getOrDefault(businessType, new DefaultUploadStrategy());
    }

    /**
     * 根据业务类型代码获取上传策略
     *
     * @param businessTypeCode 业务类型代码
     * @return 上传策略
     */
    public static UploadStrategy getStrategy(String businessTypeCode) {
        FileBusinessType businessType = FileBusinessType.fromCode(businessTypeCode);
        return getStrategy(businessType);
    }
}
