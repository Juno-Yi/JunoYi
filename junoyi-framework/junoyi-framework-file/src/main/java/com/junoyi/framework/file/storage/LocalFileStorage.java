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

    /**
     * 上传文件到本地存储
     * @param file 要上传的MultipartFile对象
     * @param path 文件存储路径
     * @return FileInfo 包含文件信息的对象
     */
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

    /**
     * 通过输入流上传文件到本地存储
     * @param inputStream 文件输入流
     * @param fileName 原始文件名
     * @param path 文件存储路径
     * @param contentType 文件内容类型
     * @return FileInfo 包含文件信息的对象
     */
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

    /**
     * 从本地存储下载文件
     * @param filePath 文件路径
     * @return byte[] 文件字节数组
     */
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

    /**
     * 从本地存储删除文件
     * @param filePath 文件路径
     * @return boolean 删除是否成功
     */
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

    /**
     * 检查本地存储中文件是否存在
     * @param filePath 文件路径
     * @return boolean 文件是否存在
     */
    @Override
    public boolean exists(String filePath) {
        Path path = Paths.get(properties.getLocal().getBasePath(), filePath);
        return Files.exists(path);
    }

    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @return String 文件访问URL
     */
    @Override
    public String getFileUrl(String filePath) {
        String urlPrefix = properties.getLocal().getUrlPrefix();
        String domain = properties.getDomain();

        if (StrUtil.isNotBlank(domain)) {
            return domain + urlPrefix + "/" + filePath;
        }
        return urlPrefix + "/" + filePath;
    }

    /**
     * 获取带过期时间的文件访问URL（本地存储不支持过期URL）
     * @param filePath 文件路径
     * @param expireSeconds 过期时间（秒）
     * @return String 文件访问URL
     */
    @Override
    public String getFileUrl(String filePath, long expireSeconds) {
        // 本地存储不支持过期URL，直接返回永久URL
        return getFileUrl(filePath);
    }

    /**
     * 构建完整路径（按业务类型和日期分目录）
     * 格式：bizType/yyyy/MM/dd/fileName
     * @param path 业务类型路径（如：avatar、image、document等）
     * @param fileName 文件名
     * @return String 完整的文件路径
     */
    private String buildFullPath(String path, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = String.format("%d/%02d/%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        if (StrUtil.isBlank(path)) {
            // 如果没有指定业务类型，使用 other 目录
            return "other/" + datePath + "/" + fileName;
        }
        // 业务类型/日期/文件名
        return path + "/" + datePath + "/" + fileName;
    }

}
