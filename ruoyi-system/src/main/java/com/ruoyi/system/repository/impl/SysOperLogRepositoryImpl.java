package com.ruoyi.system.repository.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.repository.SysOperLogRepository;

@Service
public class SysOperLogRepositoryImpl implements SysOperLogRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT oper_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time FROM sys_oper_log WHERE 1=1";

    public SysOperLogRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysOperLog> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysOperLog> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysOperLog.class));
        return list;
    }

    @Override
    public void insertOperlog(SysOperLog operLog) {
        String operTitle = operLog.getTitle();
        Integer operBusinessType = operLog.getBusinessType();
        String operMethod = operLog.getMethod();
        String operRequestMethod =operLog.getRequestMethod();
        Integer operType = operLog.getOperatorType();
        String operName = operLog.getOperName();
        String operDeptName = operLog.getDeptName();
        String operUrl = operLog.getOperUrl();
        String operIp = operLog.getOperIp();
        String operLocation = operLog.getOperLocation();
        String operParam = operLog.getOperParam();
        String operJsonResult = operLog.getJsonResult();
        Integer operStatus = operLog.getStatus();
        String operErrorMsg = StringUtils.isEmpty(operLog.getErrorMsg()) ? "" : operLog.getErrorMsg(); // 设置空字符串，避免null导致异常：Parameter metadata not available for the given statement
        Long operCostTime = operLog.getCostTime();

        String insertSql = "INSERT INTO sys_oper_log(title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, cost_time, oper_time) VALUES(:inOperTitile, :inOperBusiType, :inOperMethod, :inOperReqMethod, :inOperType, :inOperName, :inOperDeptName, :inOperUrl, :inOperIp, :inOperLocation, :inOperParam, :inOperJsonResult, :inOperStatus, :inOperErrorMsg, :inOperCostTime, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inOperTitile", operTitle);
        parameters.addValue("inOperBusiType", operBusinessType);
        parameters.addValue("inOperMethod", operMethod);
        parameters.addValue("inOperReqMethod", operRequestMethod);
        parameters.addValue("inOperType", operType);
        parameters.addValue("inOperName", operName);
        parameters.addValue("inOperDeptName", operDeptName);
        parameters.addValue("inOperUrl", operUrl);
        parameters.addValue("inOperIp", operIp);
        parameters.addValue("inOperLocation", operLocation);
        parameters.addValue("inOperParam", operParam);
        parameters.addValue("inOperJsonResult", operJsonResult);
        parameters.addValue("inOperStatus", operStatus);
        parameters.addValue("inOperErrorMsg", operErrorMsg);
        parameters.addValue("inOperCostTime", operCostTime);

        dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(operLog, sqlBuilder, parameters);

        // 默认使用oper_id排序
        sqlBuilder.append(" ORDER BY oper_id DESC");

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysOperLog logininfor) {
        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(logininfor, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_oper_log WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        // 默认使用oper_id排序
        parameters.setDefaultOrderByStr("oper_id DESC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysOperLog inOperLog, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String operIp = inOperLog.getOperIp();
        String operTitle = inOperLog.getTitle();
        Integer operBusinessType = inOperLog.getBusinessType();
        Integer[] operBusinessTypes = inOperLog.getBusinessTypes();
        Integer operStatus = inOperLog.getStatus();
        String operName = inOperLog.getOperName();
        String beginDateTime = inOperLog.getBeginTimeParam();
        String endDateTime = inOperLog.getEndTimeParam();

        if(StringUtils.isNotEmpty(operIp)) {
            inBuilder.append(" AND oper_ip LIKE CONCAT('%', :inOperIp, '%')");
            inParameters.addValue("inOperIp", operIp);
        }
        if(StringUtils.isNotEmpty(operTitle)) {
            inBuilder.append(" AND title LIKE CONCAT('%', :inOperTitile, '%')");
            inParameters.addValue("inOperTitile", operTitle);
        }
        if(StringUtils.isNotNull(operBusinessType)) {
            inBuilder.append(" AND business_type=:inOperBusiType");
            inParameters.addValue("inOperBusiType", operBusinessType);
        }
        if(StringUtils.isNotEmpty(operBusinessTypes)) {
            inBuilder.append(" AND business_type IN(:inOperBusiTypes)");
            inParameters.addValue("inOperBusiTypes", Arrays.asList(operBusinessTypes));
        }
        if(StringUtils.isNotNull(operStatus)) {
            inBuilder.append(" AND status=:inOperStatus");
            inParameters.addValue("inOperStatus", operStatus);
        }
        if(StringUtils.isNotEmpty(operName)) {
            inBuilder.append(" AND oper_name LIKE CONCAT('%', :inOperName, '%')");
            inParameters.addValue("inOperName", operName);
        }
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND oper_time >= :inBeginTime");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND oper_time <= :inEndTime");
            inParameters.addValue("inEndTime", endDateTime);
        }
    }

    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        String deleteSql = "DELETE FROM sys_oper_log WHERE oper_id=:inOperId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[operIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long operId = operIds[i];

            parametersList[i] = new MapSqlParameterSource("inOperId", operId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public SysOperLog selectOperLogById(Long operId) {
        String sql = baseSelectSql + " AND oper_id=:inOperId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inOperId", operId);

        SysOperLog queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysOperLog.class));
        return queryObj;
    }

    @Override
    public void cleanOperLog() {
        String clearSql = "TRUNCATE TABLE sys_oper_log";

        dbService.execute(clearSql);
    }

}
