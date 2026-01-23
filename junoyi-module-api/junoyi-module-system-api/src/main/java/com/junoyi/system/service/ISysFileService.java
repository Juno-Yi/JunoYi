package com.junoyi.system.service;

import com.junoyi.framework.file.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件业务接口类
 *
 * @author Fan
 */
public interface ISysFileService {

    /**
     * 根据业务类型上传文件
     *
     * @param file         上传的文件
     * @param businessType 业务类型
     * @return 文件信息
     */
    FileInfo uploadFile(MultipartFile file, String businessType);

}
