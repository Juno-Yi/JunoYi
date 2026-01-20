package com.junoyi.framework.file.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.junoyi.framework.file.domain.FileInfo;
import com.junoyi.framework.file.enums.FileBusinessType;
import com.junoyi.framework.file.factory.FileStorageFactory;
import com.junoyi.framework.file.storage.FileStorage;
import com.junoyi.framework.file.strategy.UploadStrategy;
import com.junoyi.framework.file.strategy.UploadStrategyFactory;
import com.junoyi.framework.file.util.FileTypeDetector;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件助手
 * <p>
 * 提供简化的文件操作接口
 *
 * @author Fan
 */
@RequiredArgsConstructor
public class FileHelper {

    private final FileStorageFactory fileStorageFactory;

    /**
     * 上传文件（使用默认存储）
     * 自动根据文件类型检测存储目录
     *
     * @param file 文件
     * @return 文件信息
     */
    public FileInfo upload(MultipartFile file) {
        // 根据文件类型自动检测业务类型
        String contentType = file.getContentType();
        String extension = FileUtil.extName(file.getOriginalFilename());
        String businessType = FileTypeDetector.detectBusinessType(contentType, extension);
        
        return upload(file, businessType);
    }

    /**
     * 上传文件到指定路径
     *
     * @param file 文件
     * @param path 存储路径（业务类型）
     * @return 文件信息
     */
    public FileInfo upload(MultipartFile file, String path) {
        // 如果没有指定路径，自动检测文件类型
        if (StrUtil.isBlank(path)) {
            String contentType = file.getContentType();
            String extension = FileUtil.extName(file.getOriginalFilename());
            path = FileTypeDetector.detectBusinessType(contentType, extension);
        }
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.upload(file, path);
    }

    /**
     * 根据业务类型上传文件（带策略验证）
     *
     * @param file         文件
     * @param businessType 业务类型
     * @return 文件信息
     */
    public FileInfo uploadWithStrategy(MultipartFile file, FileBusinessType businessType) {
        // 获取上传策略
        UploadStrategy strategy = UploadStrategyFactory.getStrategy(businessType);
        
        // 验证文件
        strategy.validate(file);
        
        // 上传文件到策略指定的路径
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.upload(file, strategy.getPathPrefix());
    }

    /**
     * 根据业务类型代码上传文件（带策略验证）
     *
     * @param file             文件
     * @param businessTypeCode 业务类型代码
     * @return 文件信息
     */
    public FileInfo uploadWithStrategy(MultipartFile file, String businessTypeCode) {
        FileBusinessType businessType = FileBusinessType.fromCode(businessTypeCode);
        return uploadWithStrategy(file, businessType);
    }

    /**
     * 上传文件流
     *
     * @param inputStream 输入流  
     * @param fileName    文件名
     * @param path        存储路径
     * @param contentType 文件类型
     * @return 文件信息
     */
    public FileInfo upload(InputStream inputStream, String fileName, String path, String contentType) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.upload(inputStream, fileName, path, contentType);
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件字节数组
     */
    public byte[] download(String filePath) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.download(filePath);
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean delete(String filePath) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.delete(filePath);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    public boolean exists(String filePath) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.exists(filePath);
    }

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 访问URL
     */
    public String getFileUrl(String filePath) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.getFileUrl(filePath);
    }

    /**
     * 获取文件访问URL（带过期时间）
     *
     * @param filePath      文件路径
     * @param expireSeconds 过期时间（秒）
     * @return 访问URL
     */
    public String getFileUrl(String filePath, long expireSeconds) {
        FileStorage storage = fileStorageFactory.getStorage();
        return storage.getFileUrl(filePath, expireSeconds);
    }
}
