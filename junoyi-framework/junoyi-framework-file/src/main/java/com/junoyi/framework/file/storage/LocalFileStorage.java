package com.junoyi.framework.file.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.junoyi.framework.file.domain.FileInfo;
import com.junoyi.framework.file.properties.FileStorageProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

/**
 * 本地文件存储实现
 *
 * @author Fan
 */
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    private static final JunoYiLog log = JunoYiLogFactory.getLogger(LocalFileStorage.class);
    
    private final FileStorageProperties properties;

    @Override
    public FileInfo upload(MultipartFile file, String path) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);
            String storageName = IdUtil.simpleUUID() + "." + extension;
            
            // 构建完整路径
            String fullPath = buildFullPath(path, storageName);
            Path targetPath = Paths.get(properties.getLocal().getBasePath(), fullPath);
            
            // 确保目录存在
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 计算MD5
            String md5 = MD5.create().digestHex(file.getBytes());
            
            return FileInfo.builder()
                    .originalName(originalFilename)
                    .storageName(storageName)
                    .filePath(fullPath)
                    .fileUrl(getFileUrl(fullPath))
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .extension(extension)
                    .storageType("local")
                    .md5(md5)
                    .uploadTime(LocalDateTime.now())
                    .build();
                    
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileInfo upload(InputStream inputStream, String fileName, String path, String contentType) {
        try {
            String extension = FileUtil.extName(fileName);
            String storageName = IdUtil.simpleUUID() + "." + extension;
            
            String fullPath = buildFullPath(path, storageName);
            Path targetPath = Paths.get(properties.getLocal().getBasePath(), fullPath);
            
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            long fileSize = Files.size(targetPath);
            
            return FileInfo.builder()
                    .originalName(fileName)
                    .storageName(storageName)
                    .filePath(fullPath)
                    .fileUrl(getFileUrl(fullPath))
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .extension(extension)
                    .storageType("local")
                    .uploadTime(LocalDateTime.now())
                    .build();
                    
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] download(String filePath) {
        try {
            Path path = Paths.get(properties.getLocal().getBasePath(), filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("文件下载失败: {}", filePath, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String filePath) {
        try {
            Path path = Paths.get(properties.getLocal().getBasePath(), filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean exists(String filePath) {
        Path path = Paths.get(properties.getLocal().getBasePath(), filePath);
        return Files.exists(path);
    }

    @Override
    public String getFileUrl(String filePath) {
        String urlPrefix = properties.getLocal().getUrlPrefix();
        String domain = properties.getDomain();
        
        if (StrUtil.isNotBlank(domain)) {
            return domain + urlPrefix + "/" + filePath;
        }
        return urlPrefix + "/" + filePath;
    }

    @Override
    public String getFileUrl(String filePath, long expireSeconds) {
        // 本地存储不支持过期URL，直接返回永久URL
        return getFileUrl(filePath);
    }

    /**
     * 构建完整路径（按日期分目录）
     */
    private String buildFullPath(String path, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = String.format("%d/%02d/%02d", 
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        if (StrUtil.isBlank(path)) {
            return datePath + "/" + fileName;
        }
        return path + "/" + datePath + "/" + fileName;
    }
}
