package com.junoyi.system.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.constant.CacheConstants;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.system.convert.SysDictDataConverter;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysDictDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统字典 API 实现类
 * 供其他模块调用的字典服务实现
 * 使用Redis缓存提升性能
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDictApiImpl implements SysDictApi {

    private static final String MISSING_DICT_ITEM = "\u0000__MISSING_DICT_ITEM__";
    private static final Duration MISSING_ITEM_TTL = Duration.ofMinutes(10);

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictApiImpl.class);

    private final SysDictDataMapper sysDictDataMapper;

    /**
     * 根据字典类型查询字典数据
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_DATA + dictType;
        List<SysDictDataVO> cachedData = RedisUtils.getCacheList(cacheKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            log.debug("Retrieved dictionary data from cache: {}", dictType);
            return cachedData;
        }

        // 缓存未命中,查询数据库
        log.debug("Cache miss, querying dictionary data from database: {}", dictType);
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        List<SysDictDataVO> voList = SysDictDataConverter.toVoList(dictDataList);

        // 存入缓存
        if (!voList.isEmpty()) {
            cacheDictData(dictType, voList);
            log.debug("Dictionary data cached: {}", dictType);
        }

        return voList;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签,如果不存在返回 null
     */
    @Override
    public String getDictLabel(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_LABEL + dictType + ":" + dictValue;
        String cachedLabel = RedisUtils.getCacheObject(cacheKey);
        if (cachedLabel != null) {
            return MISSING_DICT_ITEM.equals(cachedLabel) ? null : cachedLabel;
        }

        // 按类型加载，避免不同值在缓存未命中时逐项查询数据库。
        SysDictDataVO item = getDictItem(dictType, dictValue);
        String label = item != null ? item.getDictLabel() : null;
        if (label == null) RedisUtils.setCacheObject(cacheKey, MISSING_DICT_ITEM, MISSING_ITEM_TTL);
        else RedisUtils.setCacheObject(cacheKey, label);

        return label;
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值,如果不存在返回 null
     */
    @Override
    public String getDictValue(String dictType, String dictLabel) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictLabel)) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_VALUE + dictType + ":" + dictLabel;
        String cachedValue = RedisUtils.getCacheObject(cacheKey);
        if (cachedValue != null) {
            return MISSING_DICT_ITEM.equals(cachedValue) ? null : cachedValue;
        }

        String value = null;
        for (SysDictDataVO item : getDictDataByType(dictType)) {
            if (dictLabel.equals(item.getDictLabel())) {
                value = item.getDictValue();
                break;
            }
        }
        if (value == null) RedisUtils.setCacheObject(cacheKey, MISSING_DICT_ITEM, MISSING_ITEM_TTL);
        else RedisUtils.setCacheObject(cacheKey, value);

        return value;
    }

    /**
     * 检查字典数据是否存在
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsDictData(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return false;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .eq(SysDictData::getStatus, "0");

        Long count = sysDictDataMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 批量根据字典类型查询字典数据
     *
     * @param dictTypes 字典类型列表
     * @return 字典类型为key,字典数据列表为value的Map
     */
    @Override
    public Map<String, List<SysDictDataVO>> getDictDataByTypes(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return new HashMap<>();
        }

        Set<String> types = dictTypes.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, List<SysDictDataVO>> result = new HashMap<>();
        List<String> missingTypes = new ArrayList<>();
        for (String type : types) {
            List<SysDictDataVO> cached = RedisUtils.getCacheList(CacheConstants.DICT_DATA + type);
            if (cached != null && !cached.isEmpty()) result.put(type, cached);
            else missingTypes.add(type);
        }
        if (missingTypes.isEmpty()) return result;

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDictData::getDictType, missingTypes)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        List<SysDictDataVO> voList = SysDictDataConverter.toVoList(dictDataList);
        Map<String, List<SysDictDataVO>> loaded = voList.stream()
                .collect(Collectors.groupingBy(SysDictDataVO::getDictType));
        for (String type : missingTypes) {
            List<SysDictDataVO> items = loaded.getOrDefault(type, List.of());
            result.put(type, items);
            if (!items.isEmpty()) cacheDictData(type, items);
        }
        return result;
    }

    /** 在数据库提交后刷新缓存，避免回滚或并发读造成缓存与数据库不一致。 */
    public void refreshDictCachesAfterCommit(Collection<String> dictTypes) {
        Set<String> types = dictTypes.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (types.isEmpty()) return;
        Runnable refresh = () -> types.forEach(this::refreshDictCache);
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    refresh.run();
                }
            });
        } else {
            refresh.run();
        }
    }

    private void cacheDictData(String dictType, List<SysDictDataVO> items) {
        RedisUtils.setCacheList(CacheConstants.DICT_DATA + dictType, items);
        Map<String, String> pointValues = new HashMap<>();
        for (SysDictDataVO item : items) {
            if (item.getDictValue() != null && item.getDictLabel() != null) {
                pointValues.putIfAbsent(CacheConstants.DICT_LABEL + dictType + ":" + item.getDictValue(),
                        item.getDictLabel());
                pointValues.putIfAbsent(CacheConstants.DICT_VALUE + dictType + ":" + item.getDictLabel(),
                        item.getDictValue());
            }
        }
        RedisUtils.setCacheObjects(pointValues);
    }

    /**
     * 刷新指定字典类型的缓存
     *
     * @param dictType 字典类型
     */
    public void refreshDictCache(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return;
        }

        log.info("Refreshing dictionary cache: {}", dictType);

        // 删除字典数据列表缓存
        String dataCacheKey = CacheConstants.DICT_DATA + dictType;
        RedisUtils.deleteObject(dataCacheKey);

        // 删除该字典类型的所有标签和值缓存
        String labelPattern = CacheConstants.DICT_LABEL + dictType + ":*";
        String valuePattern = CacheConstants.DICT_VALUE + dictType + ":*";
        RedisUtils.deleteKeys(labelPattern);
        RedisUtils.deleteKeys(valuePattern);

        // 重新加载到缓存
        getDictDataByType(dictType);

        log.info("Dictionary cache refresh completed: {}", dictType);
    }

    /**
     * 刷新所有字典缓存
     */
    public void refreshAllDictCache() {
        log.info("Starting to refresh all dictionary caches");

        // 删除所有字典相关缓存
        RedisUtils.deleteKeys(CacheConstants.DICT_DATA + "*");
        RedisUtils.deleteKeys(CacheConstants.DICT_LABEL + "*");
        RedisUtils.deleteKeys(CacheConstants.DICT_VALUE + "*");

        // 查询所有字典类型
        List<SysDictData> allDictData = sysDictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getStatus, "0")
                        .orderByAsc(SysDictData::getDictSort)
        );

        // 按字典类型分组并缓存
        Map<String, List<SysDictData>> groupedData = allDictData.stream()
                .collect(Collectors.groupingBy(SysDictData::getDictType));

        for (Map.Entry<String, List<SysDictData>> entry : groupedData.entrySet()) {
            String dictType = entry.getKey();
            List<SysDictDataVO> voList = SysDictDataConverter.toVoList(entry.getValue());

            cacheDictData(dictType, voList);
        }

        log.info("All dictionary caches refreshed, total {} dictionary types cached", groupedData.size());
    }

    /**
     * 根据字典类型和字典值获取字典项（包含listClass等完整信息）
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典数据VO,如果不存在返回 null
     */
    @Override
    public SysDictDataVO getDictItem(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return null;
        }

        // 获取该字典类型的所有数据
        List<SysDictDataVO> dictDataList = getDictDataByType(dictType);

        // 查找匹配的字典项
        return dictDataList.stream()
                .filter(item -> dictValue.equals(item.getDictValue()))
                .findFirst()
                .orElse(null);
    }
}
