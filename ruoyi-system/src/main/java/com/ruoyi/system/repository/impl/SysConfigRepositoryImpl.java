package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.repository.SysConfigRepository;

@Repository
public class SysConfigRepositoryImpl implements SysConfigRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT scfg.config_id, scfg.config_name, scfg.config_key, scfg.config_value, scfg.config_type, scfg.create_by, scfg.create_time, scfg.update_by, scfg.update_time, scfg.remark FROM sys_config scfg WHERE 1=1";

    public SysConfigRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysConfig> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysConfig> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysConfig.class));
        return list;
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
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(config, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    private void setListSqlAndParams(final SysConfig inConfig, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String configName = inConfig.getConfigName();
        String configType = inConfig.getConfigType();
        String configKey = inConfig.getConfigKey();
        String beginDateTime = inConfig.getBeginTimeParam();
        String endDateTime = inConfig.getEndTimeParam();

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
        if(StringUtils.isNotEmpty(beginDateTime) && StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND scfg.create_time BETWEEN :inBeginTime AND :inEndTime");
            inParameters.addValue("inBeginTime", beginDateTime);
            inParameters.addValue("inEndTime", endDateTime);
        }

    }

    @Override
    public TableDataInfo getPagedListResp(SysConfig config) {

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(config, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_config scfg WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String querListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, querListSql));
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
        String insertSql = "INSERT INTO sys_config(config_name, config_type, config_key, config_value, create_by, remark, create_time) VALUES(:inConfigName, :inConfigType, :inConfigKey, :inConfigValue, :inCreateBy, :inRemark, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inConfigName", configName);
        parameters.addValue("inConfigType", configType);
        parameters.addValue("inConfigKey", configKey);
        parameters.addValue("inConfigValue", configValue);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inRemark", remark);

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateConfig(SysConfig config) {
        Long configId = config.getConfigId();
        String configName = config.getConfigName();
        String configType = config.getConfigType();
        String configKey = config.getConfigKey();
        String configValue = config.getConfigValue();
        String updateBy = config.getUpdateBy();
        String remark = config.getRemark();

        StringBuffer updateBuffer = new StringBuffer("UPDATE sys_config SET");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(configName)) {
            updateBuffer.append(" config_name=:inConfigName,");
            parameters.addValue("inConfigName", configName);
        }
        if(StringUtils.isNotEmpty(configType)) {
            updateBuffer.append(" config_type=:inConfigType,");
            parameters.addValue("inConfigType", configType);
        }
        if(StringUtils.isNotEmpty(configKey)) {
            updateBuffer.append(" config_key=:inConfigKey,");
            parameters.addValue("inConfigKey", configKey);
        }
        if(StringUtils.isNotEmpty(configValue)) {
            updateBuffer.append(" config_value=:inConfigValue,");
            parameters.addValue("inConfigValue", configValue);
        }
        if(StringUtils.isNotEmpty(remark)) {
            updateBuffer.append(" remark=:inRemark,");
            parameters.addValue("inRemark", remark);
        }

        updateBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE config_id=:inConfigId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inConfigId", configId);

        int[] updatedResList = dbService.batchUpdate(updateBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    @Override
    public int deleteConfigById(Long configId) {
        return this.deleteConfigByIds(new Long[]{configId})[0];
    }

    @Override
    public int[] deleteConfigByIds(Long[] configIds) {
        String deleteSql = "DELETE FROM sys_config WHERE config_id=:inConfigId";

        MapSqlParameterSource[] paramsList = new MapSqlParameterSource[configIds.length];
        for (int i = 0; i < paramsList.length; i++) {
            Long configId = configIds[i];
            paramsList[i] = new MapSqlParameterSource("inConfigId", configId);
        }

        int[] deletedResList = dbService.batchUpdate(deleteSql, paramsList);

        return deletedResList;
    }

}
