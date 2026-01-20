package com.junoyi.framework.file.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.junoyi.framework.file.domain.FileInfo;
import com.junoyi.framework.file.properties.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 阿里云OSS文件存储实现
 *
 * @author Fan
 */
public class AliyunOssFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(AliyunOssFileStorage.class);
    
    private final FileStorageProperties properties;
    private final OSS ossClient;

    public AliyunOssFileStorage(FileStorageProperties properties) {
        this.properties = properties;
        FileStorageProperties.AliyunOssConfig config = properties.getAliyunOss();
        
        this.ossClient = new OSSClientBuilder().build(
                config.getEndpoint(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
        );
    }

    @Override
    public FileInfo upload(MultipartFile file, String path) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);
            String storageName = IdUtil.simpleUUID() + "." + extension;
            
            String fullPath = buildFullPath(path, storageName);
            String bucketName = properties.getAliyunOss().getBucketName();
            
            // 上传到OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, fullPath, file.getInputStream());
            ossClient.putObject(putObjectRequest);
            
            return FileInfo.builder()
                    .originalName(originalFilename)
                    .storageName(storageName)
                    .filePath(fullPath)
                    .fileUrl(getFileUrl(fullPath))
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .extension(extension)
                    .storageType("aliyun-oss")
                    .uploadTime(LocalDateTime.now())
                    .build();
                    
        } catch (IOException e) {
            log.error("OSS文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileInfo upload(InputStream inputStream, String fileName, String path, String contentType) {
        String extension = FileUtil.extName(fileName);
        String storageName = IdUtil.simpleUUID() + "." + extension;
        
        String fullPath = buildFullPath(path, storageName);
        String bucketName = properties.getAliyunOss().getBucketName();
        
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, fullPath, inputStream);
        ossClient.putObject(putObjectRequest);
        
        return FileInfo.builder()
                .originalName(fileName)
                .storageName(storageName)
                .filePath(fullPath)
                .fileUrl(getFileUrl(fullPath))
                .contentType(contentType)
                .extension(extension)
                .storageType("aliyun-oss")
                .uploadTime(LocalDateTime.now())
                .build();
    }

    @Override
    public byte[] download(String filePath) {
        try {
            String bucketName = properties.getAliyunOss().getBucketName();
            OSSObject ossObject = ossClient.getObject(bucketName, filePath);
            
            try (InputStream inputStream = ossObject.getObjectContent();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("OSS文件下载失败: {}", filePath, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String filePath) {
        try {
            String bucketName = properties.getAliyunOss().getBucketName();
            ossClient.deleteObject(bucketName, filePath);
            return true;
        } catch (Exception e) {
            log.error("OSS文件删除失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean exists(String filePath) {
        try {
            String bucketName = properties.getAliyunOss().getBucketName();
            return ossClient.doesObjectExist(bucketName, filePath);
        } catch (Exception e) {
            log.error("OSS文件检查失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        FileStorageProperties.AliyunOssConfig config = properties.getAliyunOss();
        
        // 如果配置了自定义域名，使用自定义域名
        if (StrUtil.isNotBlank(config.getCustomDomain())) {
            return config.getCustomDomain() + "/" + filePath;
        }
        
        // 否则使用OSS默认域名
        return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + filePath;
    }

    @Override
    public String getFileUrl(String filePath, long expireSeconds) {
        String bucketName = properties.getAliyunOss().getBucketName();
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, filePath, expiration);
        return url.toString();
    }

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
