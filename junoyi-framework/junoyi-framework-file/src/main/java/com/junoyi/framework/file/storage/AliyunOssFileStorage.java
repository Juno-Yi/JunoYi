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
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
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

    private static final JunoYiLog log = JunoYiLogFactory.getLogger(AliyunOssFileStorage.class);
    
    private final FileStorageProperties properties;
    private final OSS ossClient;

    /**
     * 构造函数，初始化阿里云OSS文件存储服务
     * @param properties 文件存储配置属性
     */
    public AliyunOssFileStorage(FileStorageProperties properties) {
        this.properties = properties;
        FileStorageProperties.AliyunOssConfig config = properties.getAliyunOss();

        // 验证配置
        validateConfig(config);

        log.info("File Manager", "Initializing Aliyun OSS client, endpoint: "+ config.getEndpoint() + " bucket: "+ config.getBucketName());

        // 创建OSS客户端
        this.ossClient = new OSSClientBuilder().build(
                config.getEndpoint(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
        );

        // 测试连接
        testConnection(config);
    }

    /**
     * 验证OSS配置的完整性
     * @param config 阿里云OSS配置对象
     */
    private void validateConfig(FileStorageProperties.AliyunOssConfig config) {
        if (StrUtil.isBlank(config.getEndpoint())) {
            throw new IllegalArgumentException(
                    "阿里云OSS配置错误: endpoint 不能为空，请在配置文件中设置 junoyi.file.aliyun-oss.endpoint"
            );
        }
        if (StrUtil.isBlank(config.getAccessKeyId())) {
            throw new IllegalArgumentException(
                    "阿里云OSS配置错误: access-key-id 不能为空，请在配置文件中设置 junoyi.file.aliyun-oss.access-key-id"
            );
        }
        if (StrUtil.isBlank(config.getAccessKeySecret())) {
            throw new IllegalArgumentException(
                    "阿里云OSS配置错误: access-key-secret 不能为空，请在配置文件中设置 junoyi.file.aliyun-oss.access-key-secret"
            );
        }
        if (StrUtil.isBlank(config.getBucketName())) {
            throw new IllegalArgumentException(
                    "阿里云OSS配置错误: bucket-name 不能为空，请在配置文件中设置 junoyi.file.aliyun-oss.bucket-name"
            );
        }
    }

    /**
     * 测试OSS连接
     * 使用 getBucketInfo 方法，该方法在认证失败时会抛出异常
     * @param config 阿里云OSS配置对象
     */
    private void testConnection(FileStorageProperties.AliyunOssConfig config) {
        log.info("File Manager","Testing OSS connection...");
        try {
            ossClient.getBucketInfo(config.getBucketName());
            log.info("File Manager","OSS connection test successful, bucket '" + config.getBucketName() + "' is accessible");
        } catch (Exception e) {
            shutdownClient();
            String errorMsg = buildErrorMessage(e, config);
            log.error("File Manager", errorMsg);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    /**
     * 关闭OSS客户端
     */
    private void shutdownClient() {
        try {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.warn("File Manager","Failed to shutdown OSS client", e);
        }
    }

    /**
     * 根据异常类型构建友好的错误提示信息
     * @param e 异常对象
     * @param config 阿里云OSS配置对象
     * @return 友好的错误提示信息
     */
    private String buildErrorMessage(Exception e, FileStorageProperties.AliyunOssConfig config) {
        String exceptionMsg = e.getMessage() != null ? e.getMessage() : "";

        if (exceptionMsg.contains("InvalidAccessKeyId") || exceptionMsg.contains("does not exist in our records")) {
            return String.format(
                    "阿里云OSS认证失败: Access Key ID 无效或不存在\n" +
                            "配置的 access-key-id: %s\n" +
                            "提示：请确保使用正确的阿里云访问密钥，而不是示例配置中的占位符",
                    config.getAccessKeyId()
            );
        }

        if (exceptionMsg.contains("SignatureDoesNotMatch")) {
            return "阿里云OSS认证失败: Access Key Secret 错误\n" +
                    "请检查配置文件中的 access-key-secret 是否正确";
        }

        if (exceptionMsg.contains("InvalidBucketName")) {
            return String.format(
                    "阿里云OSS配置错误: Bucket名称 '%s' 不合法\n" +
                            "Bucket命名规则：只能包含小写字母、数字和短横线(-)，必须以小写字母或数字开头和结尾，长度3-63字符\n" +
                            "请检查配置文件中的 bucket-name 是否符合命名规范",
                    config.getBucketName()
            );
        }

        if (exceptionMsg.contains("NoSuchBucket")) {
            return String.format(
                    "阿里云OSS Bucket '%s' 不存在\n" +
                            "请检查配置或在OSS控制台创建该Bucket",
                    config.getBucketName()
            );
        }

        if (exceptionMsg.contains("Forbidden")) {
            return String.format(
                    "阿里云OSS访问被拒绝: 当前密钥没有访问 Bucket '%s' 的权限\n" +
                            "请在阿里云控制台检查RAM权限配置",
                    config.getBucketName()
            );
        }

        return String.format(
                "阿里云OSS连接测试失败: %s\n" +
                        "请检查以下配置项：\n" +
                        "1. endpoint 是否正确: %s\n" +
                        "2. access-key-id 和 access-key-secret 是否有效\n" +
                        "3. bucket-name 是否存在: %s\n" +
                        "4. 网络是否可以访问OSS服务",
                exceptionMsg, config.getEndpoint(), config.getBucketName()
        );
    }

    /**
     * 上传文件到OSS
     * @param file 要上传的多部分文件对象
     * @param path 存储路径
     * @return 文件信息对象
     */
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
            log.error("File Manager", "OSS文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件流到OSS
     * @param inputStream 文件输入流
     * @param fileName 原始文件名
     * @param path 存储路径
     * @param contentType 文件内容类型
     * @return 文件信息对象
     */
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

    /**
     * 从OSS下载文件
     * @param filePath 文件路径
     * @return 文件字节数组
     */
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
            log.error("File Manager","OSS文件下载失败: {}", filePath, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除OSS上的文件
     * @param filePath 文件路径
     * @return 删除结果，成功返回true，失败返回false
     */
    @Override
    public boolean delete(String filePath) {
        try {
            String bucketName = properties.getAliyunOss().getBucketName();
            ossClient.deleteObject(bucketName, filePath);
            return true;
        } catch (Exception e) {
            log.error("File Manager","OSS文件删除失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查OSS上文件是否存在
     * @param filePath 文件路径
     * @return 存在返回true，不存在返回false
     */
    @Override
    public boolean exists(String filePath) {
        try {
            String bucketName = properties.getAliyunOss().getBucketName();
            return ossClient.doesObjectExist(bucketName, filePath);
        } catch (Exception e) {
            log.error("File Manager","OSS文件检查失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件的访问URL
     * @param filePath 文件路径
     * @return 文件访问URL
     */
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

    /**
     * 获取带过期时间的临时文件访问URL
     * @param filePath 文件路径
     * @param expireSeconds 过期秒数
     * @return 带过期时间的文件访问URL
     */
    @Override
    public String getFileUrl(String filePath, long expireSeconds) {
        String bucketName = properties.getAliyunOss().getBucketName();
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, filePath, expiration);
        return url.toString();
    }

    /**
     * 构建完整的文件路径
     * 构建完整路径（按业务类型和日期分目录）
     * 格式：bizType/yyyy/MM/dd/fileName
     * @param path 业务类型路径（如：avatar、image、document等）
     * @param fileName 文件名
     * @return 完整的文件路径（包含业务类型和日期目录）
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
