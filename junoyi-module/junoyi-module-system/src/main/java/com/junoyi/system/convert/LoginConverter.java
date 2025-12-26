package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.LoginDTO;
import com.junoyi.system.domain.bo.LoginBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 登录对象转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface LoginConverter {

    LoginConverter INSTANCE = Mappers.getMapper(LoginConverter.class);

    /**
     * LoginDTO 转 LoginBO
     */
    LoginBO toLoginBO(LoginDTO dto);
}
