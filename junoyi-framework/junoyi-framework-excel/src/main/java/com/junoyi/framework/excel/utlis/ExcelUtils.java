package com.junoyi.framework.excel.utlis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.core.utils.file.FileUtils;
import com.junoyi.framework.excel.convert.ExcelBigNumberConvert;
import com.junoyi.framework.excel.core.CellMergeStrategy;
import com.junoyi.framework.excel.core.DefaultExcelListener;
import com.junoyi.framework.excel.core.ExcelListener;
import com.junoyi.framework.excel.core.ExcelResult;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类，提供基于EasyExcel的导入、导出功能。
 * 包括普通导入导出、模板填充导出、字段转换等功能。
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtils {

    /**
     * 导入Excel文件为对象列表（同步读取）
     *
     * @param is    输入流
     * @param clazz 数据实体类类型
     * @param <T>   泛型类型
     * @return 解析后的数据列表
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return EasyExcel.read(is).head(clazz).autoCloseStream(false).sheet().doReadSync();
    }

    /**
     * 导入Excel文件，并根据是否校验参数决定是否启用监听器进行数据验证
     *
     * @param is         输入流
     * @param clazz      数据实体类类型
     * @param isValidate 是否开启数据校验
     * @param <T>        泛型类型
     * @return 封装了结果与错误信息的对象
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<>(isValidate);
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 使用自定义监听器导入Excel文件
     *
     * @param is       输入流
     * @param clazz    数据实体类类型
     * @param listener 自定义监听器
     * @param <T>      泛型类型
     * @return 监听器中封装的结果对象
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 导出Excel到HTTP响应输出流，默认不合并单元格
     *
     * @param list     要导出的数据集合
     * @param sheetName 工作表名称
     * @param clazz    数据实体类类型
     * @param response HTTP响应对象
     * @param <T>      泛型类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        try {
            resetResponse(sheetName, response);
            ServletOutputStream os = response.getOutputStream();
            exportExcel(list, sheetName, clazz, false, os);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    /**
     * 导出Excel到HTTP响应输出流，支持设置是否合并相同行
     *
     * @param list     要导出的数据集合
     * @param sheetName 工作表名称
     * @param clazz    数据实体类类型
     * @param merge    是否合并相同行
     * @param response HTTP响应对象
     * @param <T>      泛型类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, boolean merge, HttpServletResponse response) {
        try {
            resetResponse(sheetName, response);
            ServletOutputStream os = response.getOutputStream();
            exportExcel(list, sheetName, clazz, merge, os);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    /**
     * 导出Excel到指定输出流，默认不合并单元格
     *
     * @param list     要导出的数据集合
     * @param sheetName 工作表名称
     * @param clazz    数据实体类类型
     * @param os       输出流
     * @param <T>      泛型类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os) {
        exportExcel(list, sheetName, clazz, false, os);
    }

    /**
     * 导出Excel到指定输出流，可控制是否合并相同行
     *
     * @param list     要导出的数据集合
     * @param sheetName 工作表名称
     * @param clazz    数据实体类类型
     * @param merge    是否合并相同行
     * @param os       输出流
     * @param <T>      泛型类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, boolean merge, OutputStream os) {
        ExcelWriterSheetBuilder builder = EasyExcel.write(os, clazz)
                .autoCloseStream(false)
                // 自动适配列宽
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                // 大数值自动转换防止失真
                .registerConverter(new ExcelBigNumberConvert())
                .sheet(sheetName);
        if (merge) {
            // 注册合并策略处理器
            builder.registerWriteHandler(new CellMergeStrategy(list, true));
        }
        builder.doWrite(list);
    }

    /**
     * 根据模板路径导出单个或多个简单对象数据到HTTP响应输出流
     *
     * @param data          填充数据列表
     * @param filename      文件名
     * @param templatePath  模板资源路径
     * @param response      HTTP响应对象
     */
    public static void exportTemplate(List<Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            resetResponse(filename, response);
            ServletOutputStream os = response.getOutputStream();
            exportTemplate(data, templatePath, os);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    /**
     * 根据模板路径将数据填充至输出流
     *
     * @param data         填充数据列表
     * @param templatePath 模板资源路径
     * @param os           输出流
     */
    public static void exportTemplate(List<Object> data, String templatePath, OutputStream os) {
        ClassPathResource templateResource = new ClassPathResource(templatePath);
        ExcelWriter excelWriter = EasyExcel.write(os)
                .withTemplate(templateResource.getStream())
                .autoCloseStream(false)
                // 大数值自动转换防止失真
                .registerConverter(new ExcelBigNumberConvert())
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据为空");
        }
        // 单表多数据导出，模板格式为{.属性}
        for (Object d : data) {
            excelWriter.fill(d, writeSheet);
        }
        excelWriter.finish();
    }

    /**
     * 支持多列表或多组数据的模板导出方法，适用于复杂结构的Excel模板
     *
     * @param data         多组键值对形式的数据映射
     * @param filename     下载时显示的文件名
     * @param templatePath 模板资源路径
     * @param response     HTTP响应对象
     */
    public static void exportTemplateMultiList(Map<String, Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            resetResponse(filename, response);
            ServletOutputStream os = response.getOutputStream();
            exportTemplateMultiList(data, templatePath, os);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    /**
     * 支持多列表或多组数据的模板导出方法，直接写入输出流
     *
     * @param data         多组键值对形式的数据映射
     * @param templatePath 模板资源路径
     * @param os           输出流
     */
    public static void exportTemplateMultiList(Map<String, Object> data, String templatePath, OutputStream os) {
        ClassPathResource templateResource = new ClassPathResource(templatePath);
        ExcelWriter excelWriter = EasyExcel.write(os)
                .withTemplate(templateResource.getStream())
                .autoCloseStream(false)
                // 大数值自动转换防止失真
                .registerConverter(new ExcelBigNumberConvert())
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据为空");
        }
        for (Map.Entry<String, Object> map : data.entrySet()) {
            // 设置列表后续还有数据
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            if (map.getValue() instanceof Collection) {
                // 多表导出必须使用 FillWrapper
                excelWriter.fill(new FillWrapper(map.getKey(), (Collection<?>) map.getValue()), fillConfig, writeSheet);
            } else {
                excelWriter.fill(map.getValue(), writeSheet);
            }
        }
        excelWriter.finish();
    }

    /**
     * 初始化HTTP响应头信息，用于浏览器下载Excel文件
     *
     * @param sheetName 工作表名称
     * @param response  HTTP响应对象
     */
    private static void resetResponse(String sheetName, HttpServletResponse response) {
        String filename = encodingFilename(sheetName);
        FileUtils.setAttachmentResponseHeader(response, filename);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }

    /**
     * 根据表达式将属性值转换为对应的描述文本
     *
     * @param propertyValue 属性原始值
     * @param converterExp  表达式字符串，如 "0=男,1=女"
     * @param separator     分隔符，例如逗号 ","
     * @return 转换后的内容
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(StringUtils.SEPARATOR);
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 反向查找：根据描述文本反查属性值
     *
     * @param propertyValue 描述文本
     * @param converterExp  表达式字符串，如 "0=男,1=女"
     * @param separator     分隔符，例如逗号 ","
     * @return 对应的属性值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(StringUtils.SEPARATOR);
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtils.containsAny(propertyValue, separator)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 编码生成唯一文件名
     *
     * @param filename 原始文件名
     * @return 加上UUID前缀的新文件名
     */
    public static String encodingFilename(String filename) {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx";
    }
}
