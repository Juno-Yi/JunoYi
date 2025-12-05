package com.junoyi.framework.core.utils.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传工具类
 *
 * @author Fan
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadUtils {

    // 默认上传的地址
    private static String defaultBaseFileDir = System.getProperty("user.home");

    // 默认文件名最大长度
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 文件上传到本地目录
     *
     * @param baseDir 本地目录路径
     * @param file 上传的文件
     * @return 返回上传好的文件的相对路径
     * @throws IOException IO异常
     */
    public static String upload(String baseDir, MultipartFile file) throws IOException {
        try{
            return upload(baseDir,file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e){
            throw new IOException(e.getMessage(),e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          基础路径
     * @param file             上传的文件
     * @param allowedExtension 允许的文件扩展名
     * @return 返回上传成功的文件路径
     * @throws IOException 文件上传失败
     */
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension) throws IOException  {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 验证文件扩展名
        assertAllowed(file, allowedExtension);
        // 生成文件名
        String pathName = extractFilename(file);

        // 创建完整路径
        File desc = getAbsoluteFile(baseDir, pathName);
        // 保存文件
        file.transferTo(desc);
        // 返回相对路径
        return getPathFileName(baseDir, pathName);
    }

    /**
     * 验证文件是否允许上传
     *
     * @param file             文件
     * @param allowedExtension 允许的扩展名
     * @throws IOException 文件验证失败
     */
    public static void assertAllowed(MultipartFile file, String[] allowedExtension) throws IOException {
        // 文件大小验证
        long size = file.getSize();
        if (size > MimeTypeUtils.DEFAULT_MAX_SIZE)
            throw new IOException("文件大小超出限制，最大允许 " + (MimeTypeUtils.DEFAULT_MAX_SIZE / 1024 / 1024) + "MB");

        // 文件名长度验证
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.length() > DEFAULT_FILE_NAME_LENGTH)
            throw new IOException("文件名称长度不能超过 " + DEFAULT_FILE_NAME_LENGTH + " 个字符");

        // 文件扩展名验证
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension))
            throw new IOException("不支持的文件类型，仅支持: " + String.join(", ", allowedExtension));
    }


    /**
     * 判断文件扩展名是否允许
     *
     * @param extension        文件扩展名
     * @param allowedExtension 允许的扩展名列表
     * @return true-允许，false-不允许
     */
    public static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension)
            if (str.equalsIgnoreCase(extension))
                return true;
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null)
            return "";
        String extension = FileUtil.extName(fileName);
        if (extension == null || extension.isEmpty())
            extension = MimeTypeUtils.getExtension(file.getContentType());
        return extension;
    }

    /**
     * 编码文件名（按日期生成路径 + UUID文件名）
     *
     * @param file 文件
     * @return 编码后的文件名
     */
    public static String extractFilename(MultipartFile file) {
        return DateUtil.format(DateUtil.date(), "yyyy/MM/dd") + "/" + encodingFilename(file);
    }

    /**
     * 编码文件名（UUID + 扩展名）
     *
     * @param file 文件
     * @return UUID文件名
     */
    private static String encodingFilename(MultipartFile file) {
        return IdUtil.fastSimpleUUID() + "." + getExtension(file);
    }

    /**
     * 获取绝对文件对象
     *
     * @param uploadDir 上传目录
     * @param fileName  文件名
     * @return 文件对象
     * @throws IOException IO异常
     */
    private static File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);
        if (!desc.exists())
            if (!desc.getParentFile().exists())
                desc.getParentFile().mkdirs();
        return desc;
    }

    /**
     * 获取文件路径（相对路径）
     *
     * @param uploadDir 上传目录
     * @param fileName  文件名
     * @return 文件路径
     */
    private static String getPathFileName(String uploadDir, String fileName) {
        int dirLastIndex = uploadDir.length() + 1;
        String currentDir = File.separator + fileName;
        String pathFileName = uploadDir + currentDir;
        return pathFileName.substring(dirLastIndex);
    }
}