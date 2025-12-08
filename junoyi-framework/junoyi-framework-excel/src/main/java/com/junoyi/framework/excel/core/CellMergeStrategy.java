package com.junoyi.framework.excel.core;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.junoyi.framework.core.utils.reflect.ReflectUtils;
import com.junoyi.framework.excel.annotation.CellMerge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 自定义单元格合并策略，用于在导出 Excel 时根据指定字段的值进行相邻相同值的合并。
 * 支持通过 {@link CellMerge} 注解标记需要合并的字段，并结合 {@link ExcelProperty} 设置表头层级。
 *
 * @author Fan
 */
public class CellMergeStrategy extends AbstractMergeStrategy {

    /**
     * 待处理的数据列表
     */
    private final List<?> list;

    /**
     * 是否包含标题行（即是否存在多级表头）
     */
    private final boolean hasTitle;

    /**
     * 合并起始行索引，受是否有标题影响
     */
    private int rowIndex;

    /**
     * 构造方法
     *
     * @param list     数据列表
     * @param hasTitle 是否含有标题行
     */
    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.list = list;
        this.hasTitle = hasTitle;
        // 行合并开始下标
        this.rowIndex = hasTitle ? 1 : 0;
    }

    /**
     * 实现具体的合并逻辑，在每次写入单元格时触发。
     *
     * @param sheet           当前工作表对象
     * @param cell            当前单元格对象
     * @param head            表头信息
     * @param relativeRowIndex 相对于数据体的行索引
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        List<CellRangeAddress> cellList = handle(list, hasTitle);
        // 判断是否需要执行合并操作
        if (CollUtil.isNotEmpty(cellList)) {
            // 只在第一列第一个数据行触发一次添加所有合并区域的操作
            if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == 0) {
                for (CellRangeAddress item : cellList) {
                    sheet.addMergedRegion(item);
                }
            }
        }
    }

    /**
     * 处理数据列表并生成需要合并的单元格范围地址列表
     *
     * @param list     数据列表
     * @param hasTitle 是否含有标题行
     * @return 需要合并的单元格范围地址列表
     */
    @SneakyThrows
    private List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        List<CellRangeAddress> cellList = new ArrayList<>();
        if (CollUtil.isEmpty(list)) {
            return cellList;
        }
        Field[] fields = ReflectUtils.getFields(list.get(0).getClass(), field -> !"serialVersionUID".equals(field.getName()));

        // 存储带有 @CellMerge 注解的字段及其对应的列索引
        List<Field> mergeFields = new ArrayList<>();
        List<Integer> mergeFieldsIndex = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(CellMerge.class)) {
                CellMerge cm = field.getAnnotation(CellMerge.class);
                mergeFields.add(field);
                mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());
                if (hasTitle) {
                    ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                    rowIndex = Math.max(rowIndex, property.value().length);
                }
            }
        }

        Map<Field, RepeatCell> map = new HashMap<>();
        // 遍历每一行数据，判断每个需合并字段是否与上一行相同，决定是否合并
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < mergeFields.size(); j++) {
                Field field = mergeFields.get(j);
                Object val = ReflectUtils.invokeGetter(list.get(i), field.getName());

                int colNum = mergeFieldsIndex.get(j);
                if (!map.containsKey(field)) {
                    map.put(field, new RepeatCell(val, i));
                } else {
                    RepeatCell repeatCell = map.get(field);
                    Object cellValue = repeatCell.getValue();
                    if (cellValue == null || "".equals(cellValue)) {
                        // 空值跳过不合并
                        continue;
                    }
                    if (!cellValue.equals(val)) {
                        if (i - repeatCell.getCurrent() > 1) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex - 1, colNum, colNum));
                        }
                        map.put(field, new RepeatCell(val, i));
                    } else if (i == list.size() - 1) {
                        if (i > repeatCell.getCurrent()) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex, colNum, colNum));
                        }
                    }
                }
            }
        }
        return cellList;
    }

    /**
     * 内部辅助类，记录某个字段当前值及所在行号，用于连续相同值的合并判断
     */
    @Data
    @AllArgsConstructor
    static class RepeatCell {

        /**
         * 字段值
         */
        private Object value;

        /**
         * 值首次出现的行索引
         */
        private int current;

    }

}
