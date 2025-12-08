package com.junoyi.framework.excel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.junoyi.framework.core.service.DictService;
import com.junoyi.framework.core.utils.SpringUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.excel.annotation.ExcelDictFormat;
import com.junoyi.framework.excel.utlis.ExcelUtils;

import java.lang.reflect.Field;


/**
 * Excel字典转换器，用于在Excel导入导出时将字段值与字典标签进行相互转换。
 * <p>
 * 支持通过注解 {@link ExcelDictFormat} 配置字典类型或表达式来实现数据的映射关系。
 *
 * @author Fan
 */
public class ExcelDictConvert implements Converter<Object> {

    /**
     * 指定该转换器支持的Java类型为Object，表示可以处理任意对象类型。
     *
     * @return 支持的Java类型
     */
    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    /**
     * 指定该转换器支持的Excel单元格数据类型。此处返回null表示不限制特定类型。
     *
     * @return 支持的Excel单元格数据类型枚举
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    /**
     * 将Excel中的单元格数据转换为Java对象属性值（读取方向）。
     * 根据字段上的 {@link ExcelDictFormat} 注解配置，从字典服务或表达式中查找对应的值。
     *
     * @param cellData             Excel单元格数据
     * @param contentProperty      Excel内容属性信息，包括字段等元数据
     * @param globalConfiguration  全局配置信息
     * @return 转换后的Java对象属性值
     */
    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 获取字段上标注的ExcelDictFormat注解
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String label = cellData.getStringValue();
        String value;

        // 判断是否使用了dictType指定字典类型
        if (StringUtils.isBlank(type)) {
            // 使用readConverterExp表达式方式进行反向查找
            value = ExcelUtils.reverseByExp(label, anno.readConverterExp(), anno.separator());
        } else {
            // 通过Spring容器获取DictService实例，并调用其方法获取字典值
            value = SpringUtils.getBean(DictService.class).getDictValue(type, label, anno.separator());
        }

        // 将结果转换为目标字段的实际类型并返回
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    /**
     * 将Java对象属性值转换为Excel单元格显示文本（写入方向）。
     * 同样根据 {@link ExcelDictFormat} 注解决定是使用表达式还是字典服务进行转换。
     *
     * @param object               Java对象属性值
     * @param contentProperty      Excel内容属性信息，包括字段等元数据
     * @param globalConfiguration  全局配置信息
     * @return 写入Excel的数据封装对象
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 处理空值情况，直接返回空白字符串
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        }

        // 获取字段上的注解信息
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.dictType();
        String value = Convert.toStr(object);
        String label;

        // 判断是否使用dictType方式
        if (StringUtils.isBlank(type)) {
            // 使用表达式方式正向查找标签
            label = ExcelUtils.convertByExp(value, anno.readConverterExp(), anno.separator());
        } else {
            // 调用字典服务获取对应标签
            label = SpringUtils.getBean(DictService.class).getDictLabel(type, value, anno.separator());
        }

        // 返回带有标签的写入单元格数据对象
        return new WriteCellData<>(label);
    }

    /**
     * 获取字段上标记的 {@link ExcelDictFormat} 注解。
     *
     * @param field 字段对象
     * @return 注解实例，若未找到则可能为null
     */
    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}
