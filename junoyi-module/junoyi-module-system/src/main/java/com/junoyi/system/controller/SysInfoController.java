package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.R;
import com.junoyi.framework.log.core.JunoLog;
import com.junoyi.framework.log.core.JunoLogFactory;
import com.junoyi.framework.stater.config.JunoYiProperties;
import com.junoyi.system.domain.vo.SystemInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URLConnection;

/**
 * 系统信息控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/info")
@RequiredArgsConstructor
public class SysInfoController {

    private final JunoLog log = JunoLogFactory.getLogger(SysInfoController.class);

    private final JunoYiProperties junoYiProperties;

    /**
     * 获取系统信息
     */
    @GetMapping
    public R<SystemInfoVo> getSystemInfo(){
        SystemInfoVo systemInfoVo = SystemInfoVo.builder()
                .name(junoYiProperties.getName())
                .version(junoYiProperties.getVersion())
                .copyrightYear(junoYiProperties.getCopyrightYear())
                .copyright(junoYiProperties.getCopyright())
                .registration(junoYiProperties.getRegistration())
                .logo("/system/info/logo")
                .build();
        return R.ok(systemInfoVo);
    }

    /**
     * 获取LOGO图片
     * 直接将图片响应
     */
    @GetMapping("/logo")
    public ResponseEntity<byte[]> getLogo() {
        try {
            ClassPathResource logoResource = new ClassPathResource("public/" + junoYiProperties.getLogo());
            if (!logoResource.exists()) {
                log.error("Logo file does not exist: public/" + junoYiProperties.getLogo());
                return ResponseEntity.notFound().build();
            }

            byte[] bytes = StreamUtils.copyToByteArray(logoResource.getInputStream());

            String contentType = URLConnection.guessContentTypeFromName(junoYiProperties.getLogo());
            if (contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (IOException e) {
            log.error("Failed to read Logo file", e);
            return ResponseEntity.status(500).build();
        }
    }
}