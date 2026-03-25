package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysDictDataRepository;

@Service
public class SysDictDataRepositoryImpl implements SysDictDataRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark FROM sys_dict_data WHERE 1=1";

    private String selectCountSql = "SELECT COUNT(1) FROM sys_dict_data WHERE 1=1";

    public SysDictDataRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        StringBuilder addWhereBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(dictData, addWhereBuilder, parameters);

        List<SysDictData> list = dbService.queryForList(addWhereBuilder.toString(), parameters, new SimplePropertyRowMapper<>(SysDictData.class));

        return list;
    }

    @Override
    public TableDataInfo getPagedListResp(SysDictData dictData) {
        StringBuilder addWhereBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(dictData, addWhereBuilder, parameters);

        // 默认使用dict_sort排序
        parameters.setDefaultOrderByStr("dict_sort ASC");;

        String querListSql = baseSelectSql + addWhereBuilder.toString();

        List<SysDictData> list = dbService.getPagedList(querListSql, parameters, new SimplePropertyRowMapper<>(SysDictData.class));

        String queryCountSql = selectCountSql + addWhereBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedResult(list, queryCountSql, parameters);
        return pagedResp;
    }

    private void setListSqlAndParams(final SysDictData inDict, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String dictType = inDict.getDictType();
        String dictLabel = inDict.getDictLabel();
        String dictStatus = inDict.getStatus();

        if(StringUtils.isNotEmpty(dictType)) {
            inBuilder.append(" AND dict_type=:inDictType");
            inParameters.addValue("inDictType", dictType);
        }
        if(StringUtils.isNotEmpty(dictLabel)) {
            inBuilder.append(" AND dict_label LIKE CONCAT('%', :inDictLabel, '%')");
            inParameters.addValue("inDictLabel", dictLabel);
        }
        if(StringUtils.isNotEmpty(dictStatus)) {
            inBuilder.append(" AND status=:inDictStatus");
            inParameters.addValue("inDictStatus", dictStatus);
        }
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        String sql = baseSelectSql + " AND status = '0' AND dict_type=:inDictType order by dict_sort asc";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictType", dictType);

        List<SysDictData> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysDictData.class));

        return list;
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        String sql = "SELECT dict_label FROM sys_dict_data WHERE dict_type=:inDictType AND dict_value=:inDictValue";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictType", dictType);
        parameters.addValue("inDictValue", dictValue);

        String queryObj = dbService.queryForObject(sql, parameters, String.class);

        return queryObj;
    }

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        String sql = baseSelectSql + " AND dict_code=:inDictCode";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictCode", dictCode);

        SysDictData queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDictData.class));
        return queryObj;
    }

    @Override
    public int countDictDataByType(String dictType) {
        String sql = "SELECT COUNT(1) FROM sys_dict_data WHERE dict_type=:inDictType";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictType", dictType);

        Integer queryObj = dbService.queryForObject(sql, parameters, Integer.class);

        return queryObj.intValue();
    }

    @Override
    public int deleteDictDataById(Long dictCode) {
        return this.deleteDictDataByIds(new Long[]{dictCode});
    }

    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        String deleteSql = "DELETE FROM sys_dict_data WHERE dict_code=:inDictCode";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[dictCodes.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long dictCode = dictCodes[i];
            parametersList[i] = new MapSqlParameterSource("inDictCode", dictCode);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);

        return deleteResList[0];
    }

    @Override
    public int insertDictData(SysDictData dictData) {
        Long dictSort = dictData.getDictSort();
        String dictLabel = dictData.getDictLabel();
        String dictValue = dictData.getDictValue();
        String dictType = dictData.getDictType();
        String dictCss = dictData.getCssClass();
        String dictList = dictData.getListClass();
        String dictDefault = dictData.getIsDefault();
        String dictStatus = dictData.getStatus();
        String dictRemark = dictData.getRemark();
        String createBy = dictData.getCreateBy();

        String insertSql = "INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, remark, create_by, create_time) VALUES(:inDictSort, :inDictLabel, :inDictValue, :inDictType, :inDictCss, :inDictList, :inDictDefault, :inDictStatus, :inDictRemark, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictSort", dictSort);
        parameters.addValue("inDictLabel", dictLabel);
        parameters.addValue("inDictValue", dictValue);
        parameters.addValue("inDictType", dictType);
        parameters.addValue("inDictCss", dictCss);
        parameters.addValue("inDictList", dictList);
        parameters.addValue("inDictDefault", dictDefault);
        parameters.addValue("inDictStatus", dictStatus);
        parameters.addValue("inDictRemark", dictRemark);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateDictData(SysDictData dictData) {
        Long dictCode = dictData.getDictCode();
        Long dictSort = dictData.getDictSort();
        String dictLabel = dictData.getDictLabel();
        String dictValue = dictData.getDictValue();
        String dictType = dictData.getDictType();
        String dictCss = dictData.getCssClass();
        String dictList = dictData.getListClass();
        String dictDefault = dictData.getIsDefault();
        String dictStatus = dictData.getStatus();
        String dictRemark = dictData.getRemark();
        String updateBy = dictData.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_dict_data SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotNull(dictSort)) {
            updateSqlBuffer.append(" dict_sort=:inDictSort,");
            parameters.addValue("inDictSort", dictSort);
        }
        if(StringUtils.isNotEmpty(dictLabel)) {
            updateSqlBuffer.append(" dict_label=:inDictLabel,");
            parameters.addValue("inDictLabel", dictLabel);
        }
        if(StringUtils.isNotEmpty(dictValue)) {
            updateSqlBuffer.append(" dict_value=:inDictValue,");
            parameters.addValue("inDictValue", dictValue);
        }
        if(StringUtils.isNotEmpty(dictType)) {
            updateSqlBuffer.append(" dict_type=:inDictType,");
            parameters.addValue("inDictType", dictType);
        }
        if(StringUtils.isNotEmpty(dictCss)) {
            updateSqlBuffer.append(" css_class=:inDictCss,");
            parameters.addValue("inDictCss", dictCss);
        }
        if(StringUtils.isNotEmpty(dictList)) {
            updateSqlBuffer.append(" list_class=:inDictList,");
            parameters.addValue("inDictList", dictList);
        }
        if(StringUtils.isNotEmpty(dictDefault)) {
            updateSqlBuffer.append(" is_default=:inDictDefault,");
            parameters.addValue("inDictDefault", dictDefault);
        }
        if(StringUtils.isNotEmpty(dictStatus)) {
            updateSqlBuffer.append(" status=:inDictStatus,");
            parameters.addValue("inDictStatus", dictStatus);
        }
        if(StringUtils.isNotEmpty(dictRemark)) {
            updateSqlBuffer.append(" remark=:inDictRemark,");
            parameters.addValue("inDictRemark", dictRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE dict_code=:inDictCode");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inDictCode", dictCode);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int updateDictDataType(String oldDictType, String newDictType) {
        String updateSql = "UPDATE sys_dict_data SET dict_type=:inDictType WHERE dict_type=:inOldDictType";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictType", newDictType);
        parameters.addValue("inOldDictType", oldDictType);

        int[] updateResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

}
