package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysDictTypeRepository;

@Service
public class SysDictTypeRepositoryImpl implements SysDictTypeRepository {

    private DBService dbService;

    private String baseSelectSql = "select dict_id, dict_name, dict_type, status, create_by, create_time, remark from sys_dict_type WHERE 1=1";

    public SysDictTypeRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysDictType> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysDictType> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysDictType.class));
        return list;
    }

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(dictType, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysDictType dictType) {
        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(dictType, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_dict_type WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String querListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, querListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysDictType inDictType, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String dictTyName = inDictType.getDictName();
        String dictTyStatus = inDictType.getStatus();
        String dictType = inDictType.getDictType();
        String beginDateTime = inDictType.getBeginTimeParam();
        String endDateTime = inDictType.getEndTimeParam();

        if(StringUtils.isNotEmpty(dictTyName)) {
            inBuilder.append(" AND dict_name LIKE CONCAT('%', :inDictTyName, '%')");
            inParameters.addValue("inDictTyName", dictTyName);
        }
        if(StringUtils.isNotEmpty(dictTyStatus)) {
            inBuilder.append(" AND status=:inDictTyStatus");
            inParameters.addValue("inDictTyStatus", dictTyStatus);
        }
        if(StringUtils.isNotEmpty(dictType)) {
            inBuilder.append(" AND dict_type=:inDictType");
            inParameters.addValue("inDictType", dictType);
        }
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(create_time,'%Y%m%d') >= DATE_FORMAT(:inBeginTime,'%Y%m%d')");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(create_time,'%Y%m%d') <= DATE_FORMAT(:inEndTime,'%Y%m%d')");
            inParameters.addValue("inEndTime", endDateTime);
        }
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        return queryList(null, baseSelectSql);
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        String sql = baseSelectSql + " AND dict_id=:inDictId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictId", dictId);

        SysDictType queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDictType.class));
        return queryObj;
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        String sql = baseSelectSql + " AND dict_type=:inDictTyType";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictTyType", dictType);

        SysDictType queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDictType.class));
        return queryObj;
    }

    @Override
    public int deleteDictTypeById(Long dictId) {
        return this.deleteDictTypeByIds(new Long[]{dictId});
    }

    @Override
    public int deleteDictTypeByIds(Long[] dictIds) {
        String deleteSql = "DELETE FROM sys_dict_type WHERE dict_id=:inDictId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[dictIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long dictId = dictIds[i];
            parametersList[i] = new MapSqlParameterSource("inDictId", dictId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);

        return deleteResList[0];
    }

    @Override
    public int insertDictType(SysDictType dictType) {
        String dictTyName = dictType.getDictName();
        String dictTyType = dictType.getDictType();
        String dictTyStatus = dictType.getStatus();
        String dictTyRemark = dictType.getRemark();
        String createBy = dictType.getCreateBy();

        String insertSql = "INSERT INTO sys_dict_type(dict_name, dict_type, status, remark, create_by, create_time) VALUES(:inDictTyName, :inDictTyType, :inDicTytStatus, :inDictTyRemark, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictTyName", dictTyName);
        parameters.addValue("inDictTyType", dictTyType);
        parameters.addValue("inDicTytStatus", dictTyStatus);
        parameters.addValue("inDictTyRemark", dictTyRemark);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateDictType(SysDictType dictType) {
        Long dictId = dictType.getDictId();
        String dictTyName = dictType.getDictName();
        String dictTyType = dictType.getDictType();
        String dictTyStatus = dictType.getStatus();
        String dictTyRemark = dictType.getRemark();
        String updateBy = dictType.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_dict_type SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotNull(dictTyName)) {
            updateSqlBuffer.append(" dict_name=:inDictTyName,");
            parameters.addValue("inDictTyName", dictTyName);
        }
        if(StringUtils.isNotEmpty(dictTyType)) {
            updateSqlBuffer.append(" dict_type=:inDictTyType,");
            parameters.addValue("inDictTyType", dictTyType);
        }
        if(StringUtils.isNotEmpty(dictTyStatus)) {
            updateSqlBuffer.append(" status=:inDicTytStatus,");
            parameters.addValue("inDicTytStatus", dictTyStatus);
        }
        if(StringUtils.isNotEmpty(dictTyRemark)) {
            updateSqlBuffer.append(" remark=:inDictTyRemark,");
            parameters.addValue("inDictTyRemark", dictTyRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE dict_id=:inDictId");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inDictId", dictId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public SysDictType checkDictTypeUnique(String dictType) {
        String sql = baseSelectSql + " AND dict_type=:inDictTyType";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDictTyType", dictType);

        SysDictType queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDictType.class));
        return queryObj;
    }

}
