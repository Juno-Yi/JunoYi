package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 用户对象转换器
 * <p>
 * 使用示例：
 * <pre>
 * // 方式1：通过 INSTANCE 静态调用
 * SysUserVO vo = SysUserConverter.INSTANCE.toVo(user);
 *
 * // 方式2：通过 Spring 注入
 * &#64;Autowired
 * private SysUserConverter userConverter;
 * SysUserVO vo = userConverter.toVo(user);
 * </pre>
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface SysUserConverter extends BaseConverter<SysUserDTO, SysUser, SysUserVO> {

    /**
     * 静态实例（用于非 Spring 环境或静态方法中）
     */
    SysUserConverter INSTANCE = Mappers.getMapper(SysUserConverter.class);

    /**
     * Entity 转 VO
     * <p>
     * 可以在这里添加自定义映射规则
     */
    @Override
    @Mappings({
            // 示例：如果字段名不同，可以这样映射
            // @Mapping(source = "phonenumber", target = "phone"),
            // 示例：忽略某个字段
            // @Mapping(target = "password", ignore = true)
    })
    SysUserVO toVo(SysUser entity);
}
