package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * 音频上传策略
 *
 * @author Fan
 */
public class AudioUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList(
                "audio/mpeg",      // MP3
                "audio/wav",       // WAV
                "audio/x-wav",     // WAV (alternative)
                "audio/flac",      // FLAC
                "audio/aac",       // AAC
                "audio/ogg",       // OGG
                "audio/x-ms-wma",  // WMA
                "audio/mp4",       // M4A
                "audio/x-m4a"      // M4A (alternative)
        );
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a");
    }

    @Override
    public long getMaxFileSize() {
        return 50 * 1024 * 1024; // 50MB
    }

    @Override
    public String getPathPrefix() {
        return "audio";
    }
}
