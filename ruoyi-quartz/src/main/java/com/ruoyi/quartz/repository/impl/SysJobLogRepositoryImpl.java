package com.ruoyi.quartz.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.repository.SysJobLogRepository;

@Repository
public class SysJobLogRepositoryImpl implements SysJobLogRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT job_log_id, job_name, job_group, invoke_target, job_message, status, exception_info, start_time, end_time, create_time FROM sys_job_log WHERE 1=1";

    public SysJobLogRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysJobLog> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysJobLog> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysJobLog.class));
        return list;
    }

    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(jobLog, sqlBuilder, parameters);

        // 默认使用job_log_id排序
        sqlBuilder.append(" ORDER BY job_log_id DESC");

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysJobLog jobLog) {
        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(jobLog, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_job_log WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        // 默认使用job_log_id排序
        parameters.setDefaultOrderByStr("job_log_id DESC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysJobLog inJobLog, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String jLogName = inJobLog.getJobName();
        String jLogGroup = inJobLog.getJobGroup();
        String jLogStatus = inJobLog.getStatus();
        String jLogInvokeTarget =inJobLog.getInvokeTarget();
        String beginDateTime = inJobLog.getBeginTimeParam();
        String endDateTime = inJobLog.getEndTimeParam();

        if(StringUtils.isNotEmpty(jLogName)) {
            inBuilder.append(" AND job_name LIKE CONCAT('%', :inJLogName, '%')");
            inParameters.addValue("inJLogName", jLogName);
        }
        if(StringUtils.isNotEmpty(jLogGroup)) {
            inBuilder.append(" AND job_group=:inJLogGroup");
            inParameters.addValue("inJLogGroup", jLogGroup);
        }
        if(StringUtils.isNotEmpty(jLogStatus)) {
            inBuilder.append(" AND status=:inJLogStatus");
            inParameters.addValue("inJLogStatus", jLogStatus);
        }
        if(StringUtils.isNotEmpty(jLogInvokeTarget)) {
            inBuilder.append(" AND invoke_target LIKE CONCAT('%', :inJLogInTarget, '%')");
            inParameters.addValue("inJLogInTarget", jLogInvokeTarget);
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
    public List<SysJobLog> selectJobLogAll() {
        return queryList(null, baseSelectSql);
    }

    @Override
    public SysJobLog selectJobLogById(Long jobLogId) {
        String sql = baseSelectSql + " AND job_log_id=:inJLogId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inJLogId", jobLogId);

        SysJobLog queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysJobLog.class));
        return queryObj;
    }

    @Override
    public int insertJobLog(SysJobLog jobLog) {
        String jLogName = jobLog.getJobName();
        String jLogGroup = jobLog.getJobGroup();
        String jLogInvokeTarget =jobLog.getInvokeTarget();
        String jLogMessage = jobLog.getJobMessage();
        String jLogStatus = jobLog.getStatus();
        String jLogExcep = StringUtils.isEmpty(jobLog.getExceptionInfo()) ? "" : jobLog.getExceptionInfo();
        LocalDateTime jLogStartTime = jobLog.getStartTime();
        LocalDateTime jLogEndTime = jobLog.getEndTime();

        String insertSql = "INSERT INTO sys_job_log(job_name, job_group, invoke_target, job_message, status, exception_info, start_time, end_time, create_time) VALUES(:inJLogName, :inJLogGro, :inJLogInv, :inJLogMess, :inJLogStatus, :inJLogExcep, :inJLogStart, :inJLogEnd, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inJLogName", jLogName);
        parameters.addValue("inJLogGro", jLogGroup);
        parameters.addValue("inJLogInv", jLogInvokeTarget);
        parameters.addValue("inJLogMess", jLogMessage);
        parameters.addValue("inJLogStatus", jLogStatus);
        parameters.addValue("inJLogExcep", jLogExcep);
        parameters.addValue("inJLogStart", jLogStartTime);
        parameters.addValue("inJLogEnd", jLogEndTime);

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int deleteJobLogByIds(Long[] logIds) {
        String deleteSql = "DELETE FROM sys_job_log WHERE job_log_id=:inJLogId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[logIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long jLogId = logIds[i];

            parametersList[i] = new MapSqlParameterSource("inJLogId", jLogId);
        }

        int[] insertResList = dbService.batchUpdate(deleteSql, parametersList);
        return insertResList[0];
    }

    @Override
    public int deleteJobLogById(Long jobLogId) {
        return this.deleteJobLogByIds(new Long[]{jobLogId});
    }

    @Override
    public void cleanJobLog() {
        String clearSql = "TRUNCATE TABLE sys_job_log";

        dbService.execute(clearSql);
    }
}
