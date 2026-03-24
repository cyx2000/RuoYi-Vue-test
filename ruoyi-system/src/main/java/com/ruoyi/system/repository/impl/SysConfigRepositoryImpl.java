package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.repository.SysConfigRepository;

@Service
public class SysConfigRepositoryImpl implements SysConfigRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT scfg.config_id, scfg.config_name, scfg.config_key, scfg.config_value, scfg.config_type, scfg.create_by, scfg.create_time, scfg.update_by, scfg.update_time, scfg.remark FROM sys_config scfg WHERE 1=1";

    private String selectCountSql = "SELECT COUNT(1) FROM sys_config scfg WHERE 1=1";

    public SysConfigRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public SysConfig selectConfigByKey(String configKey) {
        String sql = baseSelectSql + " AND scfg.config_key=:inConfigKey";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inConfigKey", configKey);

        SysConfig queryResult = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysConfig.class));
        return queryResult;
    }

    @Override
    public SysConfig selectConfigById(Long configId) {
        String sql = baseSelectSql + " AND scfg.config_id=:inConfigId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inConfigId", configId);

        SysConfig queryResult = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysConfig.class));
        return queryResult;
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        StringBuilder addWhereBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(config, addWhereBuilder, parameters);

        List<SysConfig> list = dbService.queryForList(baseSelectSql + addWhereBuilder.toString(), parameters, new SimplePropertyRowMapper<>(SysConfig.class));

        return list;
    }

    private void setListSqlAndParams(final SysConfig inConfig, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String configName = inConfig.getConfigName();
        String configType = inConfig.getConfigType();
        String configKey = inConfig.getConfigKey();
        String beginDateTime = (String) inConfig.getParams().get("beginTime");
        String endDateTime = (String) inConfig.getParams().get("endTime");

        if(StringUtils.isNotEmpty(configName)) {
            inBuilder.append(" AND scfg.config_name LIKE CONCAT('%', :inConfigName, '%')");
            inParameters.addValue("inConfigName", configName);
        }
        if(StringUtils.isNotEmpty(configType)) {
            inBuilder.append(" AND scfg.config_type=:inConfigType");
            inParameters.addValue("inConfigType", configType);
        }
        if(StringUtils.isNotEmpty(configKey)) {
            inBuilder.append(" AND scfg.config_key LIKE CONCAT('%', :inConfigKey, '%')");
            inParameters.addValue("inConfigKey", configKey);
        }
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(scfg.create_time,'%Y%m%d') >= DATE_FORMAT(:inBeginTime,'%Y%m%d')");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(scfg.create_time,'%Y%m%d') <= DATE_FORMAT(:inEndTime,'%Y%m%d')");
            inParameters.addValue("inEndTime", endDateTime);
        }

    }

    @Override
    public TableDataInfo getPagedListResp(SysConfig config) {

        StringBuilder addWhereBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(config, addWhereBuilder, parameters);

        // 默认使用主键排序
        parameters.addValue("inOrderBy", "config_id");

        List<SysConfig> list = dbService.getPagedList(baseSelectSql + addWhereBuilder.toString(), parameters, new SimplePropertyRowMapper<>(SysConfig.class));

        TableDataInfo pagedResp = dbService.getPagedResult(list, selectCountSql + addWhereBuilder.toString(), parameters);
        return pagedResp;
    }

    @Override
    public int insertConfig(SysConfig config) {
        String configName = config.getConfigName();
        String configType = config.getConfigType();
        String configKey = config.getConfigKey();
        String configValue = config.getConfigValue();
        String createBy = config.getCreateBy();
        String remark = config.getRemark();
        String insertSql = "INSERT INTO sys_config (config_name,config_type,config_key,config_value,create_by,remark,create_time) VALUES(:inConfigName,:inConfigType,:inConfigKey,:inConfigValue,:inCreateBy,:inRemark,:inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inConfigName", configName);
        parameters.addValue("inConfigType", configType)
            .addValue("inConfigKey", configKey)
            .addValue("inConfigValue", configValue)
            .addValue("inCreateBy", createBy)
            .addValue("inRemark", remark)
            .addValue("inCreateTime", LocalDateTime.now());

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateConfig(SysConfig config) {
        String configName = config.getConfigName();
        String configType = config.getConfigType();
        String configKey = config.getConfigKey();
        String configValue = config.getConfigValue();
        String updateBy = config.getUpdateBy();
        String remark = config.getRemark();

        StringBuilder updateBuilder = new StringBuilder("UPDATE sys_config SET");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(configName)) {
            updateBuilder.append(" config_name=:inConfigName,");
            parameters.addValue("inConfigName", configName);
        }
        if(StringUtils.isNotEmpty(configType)) {
            updateBuilder.append(" config_type=:inConfigType,");
            parameters.addValue("inConfigType", configType);
        }
        if(StringUtils.isNotEmpty(configKey)) {
            updateBuilder.append(" config_key=:inConfigKey,");
            parameters.addValue("inConfigKey", configKey);
        }
        if(StringUtils.isNotEmpty(configValue)) {
            updateBuilder.append(" config_value=:inConfigValue,");
            parameters.addValue("inConfigValue", configValue);
        }
        if(StringUtils.isNotEmpty(remark)) {
            updateBuilder.append(" remark=:inRemark,");
            parameters.addValue("inRemark", remark);
        }
        updateBuilder.append(" update_by=:inUpdateBy,");
        parameters.addValue("inUpdateBy", updateBy);
        updateBuilder.append(" update_time=:inUpdateTime");
        parameters.addValue("inUpdateTime", LocalDateTime.now());

        updateBuilder.append(" WHERE config_id=:inConfigId");
        parameters.addValue("inConfigId", config.getConfigId());

        int[] updatedResList = dbService.batchUpdate(updateBuilder.toString(), new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    @Override
    public int deleteConfigById(Long configId) {
        String deleteSql = "DELETE FROM sys_config WHERE config_id=:inConfigId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("inConfigId", configId);
        int[] deletedResList = dbService.batchUpdate(deleteSql.toString(), new MapSqlParameterSource[]{parameters});
        return deletedResList[0];
    }

    @Override
    public int[] deleteConfigByIds(Long[] configIds) {
        String deleteSql = "DELETE FROM sys_config WHERE config_id=:inConfigId";

        ArrayList<MapSqlParameterSource> batchList = new ArrayList<MapSqlParameterSource>();
        for(Long configId: configIds) {
            batchList.add(new MapSqlParameterSource("inConfigId", configId));
        }

        int[] deletedResList = dbService.batchUpdate(deleteSql, (MapSqlParameterSource[])batchList.toArray());

        return deletedResList;
    }

}
