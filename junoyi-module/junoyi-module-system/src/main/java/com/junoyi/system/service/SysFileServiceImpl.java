package com.junoyi.system.service;

import com.junoyi.framework.file.domain.FileInfo;
import com.junoyi.framework.file.helper.FileHelper;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl implements ISysFileService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysFileServiceImpl.class);
    private final FileHelper fileHelper;

    /**
     * 根据业务类型上传文件
     *
     * @param file 要上传的文件，类型为MultipartFile
     * @param businessType 业务类型标识，用于区分不同的业务场景
     * @return FileInfo 包含上传文件信息的对象，如文件URL等
     * @throws Exception 当文件上传失败时抛出异常
     */
    @Override
    public FileInfo uploadFile(MultipartFile file, String businessType) {
        log.info("FileUpload", "开始上传文件: {}, 业务类型: {}", file.getOriginalFilename(), businessType);

        try {
            // 使用策略模式进行文件上传，该方法会自动进行文件类型和大小的验证
            FileInfo fileInfo = fileHelper.uploadWithStrategy(file, businessType);

            log.info("FileUpload", "文件上传成功: {}", fileInfo.getFileUrl());
            return fileInfo;
        } catch (Exception e) {
            log.error("FileUpload", "文件上传失败: {}", e.getMessage(), e);
            throw e;
        }
    }


}