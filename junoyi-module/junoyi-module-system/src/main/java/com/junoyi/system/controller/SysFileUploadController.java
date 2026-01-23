package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.file.domain.FileInfo;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.service.ISysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件上传控制类
 *
 * @author Fan
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class SysFileUploadController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysFileUploadController.class);
    private final ISysFileService sysFileService;

    /**
     * 上传文件（按照业务类型）
     * <p>
     * 根据业务类型自动应用对应的上传策略，包括：
     * - 文件类型验证（只允许特定类型）
     * - 文件大小限制（不同业务类型有不同限制）
     * - 自动路径分类（存储到对应业务目录）
     *
     * @param file 上传的文件
     * @param bizType 业务类型：avatar(头像)、document(文档)、image(图片)、video(视频)、audio(音频)、other(其他)
     * @return 文件信息
     */
    @Operation(summary = "上传文件", description = "根据业务类型上传文件，自动进行类型和大小验证")
    @PostMapping("/{bizType}")
    public R<FileInfo> uploadAvatarImageFile(
            @Parameter(description = "上传的文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务类型：avatar/document/image/video/audio/other", required = true) @PathVariable("bizType") String bizType) {
        
        try {
            FileInfo fileInfo = sysFileService.uploadFile(file, bizType);
            return R.ok(fileInfo);
        } catch (IllegalArgumentException e) {
            log.warn("FileUpload", "文件验证失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        } catch (Exception e) {
            log.error("FileUpload", "文件上传失败: {}", e.getMessage());
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

}