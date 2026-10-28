package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.api.SysDictApiImpl;
import com.junoyi.system.convert.SysDictDataConverter;
import com.junoyi.system.domain.dto.SysDictDataDTO;
import com.junoyi.system.domain.dto.SysDictDataQueryDTO;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.mapper.SysDictDataMapper;
import com.junoyi.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 字典数据服务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements ISysDictDataService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictDataServiceImpl.class);
    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictApiImpl sysDictApi;

    /**
     * 分页查询字典数据列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果对象
     */
    @Override
    public PageResult<SysDictDataVO> getDictDataList(SysDictDataQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(queryDTO.getDictType()), SysDictData::getDictType, queryDTO.getDictType())
                .like(StringUtils.isNotBlank(queryDTO.getDictLabel()), SysDictData::getDictLabel, queryDTO.getDictLabel())
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), SysDictData::getStatus, queryDTO.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByDesc(SysDictData::getCreateTime);

        Page<SysDictData> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<SysDictData> resultPage = sysDictDataMapper.selectPage(page, wrapper);

        List<SysDictDataVO> voList = SysDictDataConverter.toVoList(resultPage.getRecords());
        return PageResult.of(voList, resultPage.getTotal(), (int) resultPage.getCurrent(), (int) resultPage.getSize());
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        return SysDictDataConverter.toVoList(dictDataList);
    }

    /**
     * 根据ID查询字典数据详情
     *
     * @param dictCode 字典数据编码
     * @return 字典数据VO对象
     */
    @Override
    public SysDictDataVO getDictDataById(Long dictCode) {
        SysDictData dictData = sysDictDataMapper.selectById(dictCode);
        return dictData != null ? SysDictDataConverter.toVo(dictData) : null;
    }

    /**
     * 新增字典数据
     *
     * @param dictDataDTO 字典数据DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictData(SysDictDataDTO dictDataDTO) {
        SysDictData dictData = SysDictDataConverter.toEntity(dictDataDTO);

        // 设置默认值
        if (dictData.getStatus() == null) {
            dictData.setStatus("0");
        }
        if (dictData.getIsDefault() == null) {
            dictData.setIsDefault("N");
        }
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(0);
        }
        dictData.setCreateBy(SecurityUtils.getUserName());
        dictData.setCreateTime(DateUtils.getNowDate());

        sysDictDataMapper.insert(dictData);

        log.info("DictData", "添加字典数据: {} - {}", dictData.getDictType(), dictData.getDictLabel());

        // 刷新缓存
        sysDictApi.refreshDictCachesAfterCommit(Collections.singletonList(dictData.getDictType()));

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("create", "dict_data",
                "创建了字典数据「" + dictData.getDictLabel() + "」",
                String.valueOf(dictData.getDictCode()), dictData.getDictLabel(),
                JsonUtils.toJsonString(dictDataDTO)));
    }

    /**
     * 修改字典数据
     *
     * @param dictDataDTO 字典数据DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(SysDictDataDTO dictDataDTO) {
        SysDictData oldDictData = sysDictDataMapper.selectById(dictDataDTO.getDictCode());
        if (oldDictData == null) {
            throw new RuntimeException("字典数据不存在");
        }

        SysDictData dictData = SysDictDataConverter.toEntity(dictDataDTO);
        dictData.setDictCode(dictDataDTO.getDictCode());

        // 保留原有的字段值
        if (dictData.getStatus() == null) {
            dictData.setStatus(oldDictData.getStatus());
        }
        if (dictData.getIsDefault() == null) {
            dictData.setIsDefault(oldDictData.getIsDefault());
        }
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(oldDictData.getDictSort());
        }
        dictData.setUpdateBy(SecurityUtils.getUserName());
        dictData.setUpdateTime(DateUtils.getNowDate());

        sysDictDataMapper.updateById(dictData);

        log.info("DictData", "更新字典数据: {} - {}", dictData.getDictType(), dictData.getDictLabel());

        // 刷新缓存 - 如果字典类型改变了，需要刷新两个类型的缓存
        Set<String> typesToRefresh = new HashSet<>();
        typesToRefresh.add(oldDictData.getDictType());
        if (dictData.getDictType() != null && !dictData.getDictType().equals(oldDictData.getDictType())) {
            typesToRefresh.add(dictData.getDictType());
        }
        sysDictApi.refreshDictCachesAfterCommit(typesToRefresh);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("update", "dict_data",
                "更新了字典数据「" + dictData.getDictLabel() + "」",
                String.valueOf(dictData.getDictCode()), dictData.getDictLabel(),
                JsonUtils.toJsonString(dictDataDTO)));
    }

    /**
     * 删除字典数据
     *
     * @param dictCode 字典数据编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long dictCode) {
        SysDictData dictData = sysDictDataMapper.selectById(dictCode);
        if (dictData == null) {
            throw new RuntimeException("字典数据不存在");
        }

        String dictType = dictData.getDictType();
        sysDictDataMapper.deleteById(dictCode);

        log.info("DictData", "删除字典数据: {} - {}", dictData.getDictType(), dictData.getDictLabel());

        // 刷新缓存
        sysDictApi.refreshDictCachesAfterCommit(Collections.singletonList(dictType));

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("delete", "dict_data",
                "删除了字典数据「" + dictData.getDictLabel() + "」",
                String.valueOf(dictCode), dictData.getDictLabel()));
    }

    /**
     * 批量删除字典数据
     *
     * @param dictCodes 字典数据编码列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataList(List<Long> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return;
        }
        // 收集需要刷新缓存的字典类型
        Set<String> typesToRefresh = new HashSet<>();
        List<SysDictData> existing = sysDictDataMapper.selectBatchIds(dictCodes);
        existing.forEach(item -> typesToRefresh.add(item.getDictType()));
        if (!existing.isEmpty()) {
            sysDictDataMapper.deleteBatchIds(existing.stream().map(SysDictData::getDictCode).toList());
        }

        log.info("DictData", "批量删除字典数据: {} 条", existing.size());

        // 刷新所有相关字典类型的缓存
        sysDictApi.refreshDictCachesAfterCommit(typesToRefresh);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("delete", "dict_data",
                "批量删除了 " + existing.size() + " 条字典数据",
                dictCodes.toString(), null));
    }
}
