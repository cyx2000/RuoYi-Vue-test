package com.ruoyi.quartz.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.repository.SysJobRepository;

@Repository
public class SysJobRepositoryImpl implements SysJobRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark FROM sys_job WHERE 1=1";

    public SysJobRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysJob> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysJob> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysJob.class));
        return list;
    }

    @Override
    public List<SysJob> selectJobList(SysJob job) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(job, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysJob job) {
        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(job, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_job WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysJob inJob, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String jobName = inJob.getJobName();
        String jobGroup = inJob.getJobGroup();
        String jobStatus = inJob.getStatus();
        String jobInvokeTarget =inJob.getInvokeTarget();

        if(StringUtils.isNotEmpty(jobName)) {
            inBuilder.append(" AND job_name LIKE CONCAT('%', :inJobName, '%')");
            inParameters.addValue("inJobName", jobName);
        }
        if(StringUtils.isNotEmpty(jobGroup)) {
            inBuilder.append(" AND job_group=:inJobGroup");
            inParameters.addValue("inJobGroup", jobGroup);
        }
        if(StringUtils.isNotEmpty(jobStatus)) {
            inBuilder.append(" AND status=:inJobStatus");
            inParameters.addValue("inJobStatus", jobStatus);
        }
        if(StringUtils.isNotEmpty(jobInvokeTarget)) {
            inBuilder.append(" AND invoke_target LIKE CONCAT('%', :inJobInTarget, '%')");
            inParameters.addValue("inJobInTarget", jobInvokeTarget);
        }
    }

    @Override
    public List<SysJob> selectJobAll() {
        return queryList(null, baseSelectSql);
    }

    @Override
    public SysJob selectJobById(Long jobId) {
        String sql = baseSelectSql + " AND job_id=:inJobId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inJobId", jobId);

        SysJob queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysJob.class));
        return queryObj;
    }

    @Override
    @Transactional
    public int deleteJobById(Long jobId) {
        return this.deleteJobByIds(new Long[]{jobId});
    }

    @Override
    @Transactional
    public int deleteJobByIds(Long[] jobIds) {
        String deleteSql = "DELETE FROM sys_job WHERE job_id=:inJobId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[jobIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long jobId = jobIds[i];

            parametersList[i] = new MapSqlParameterSource("inJobId", jobId);
        }

        int[] insertResList = dbService.batchUpdate(deleteSql, parametersList);
        return insertResList[0];
    }

    @Override
    @Transactional
    public int updateJob(SysJob job) {
        Long jobId = job.getJobId();
        String jobName = job.getJobName();
        String jobGroup = job.getJobGroup();
        String jobInvokeTarget =job.getInvokeTarget();
        String jobExpression = job.getCronExpression();
        String jobMisfire = job.getMisfirePolicy();
        String jobConcurrent = job.getConcurrent();
        String jobStatus = job.getStatus();
        String jobRemark = job.getRemark();
        String updateBy = job.getUpdateBy();

        StringBuffer updateBuffer = new StringBuffer("UPDATE sys_job SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(jobName)) {
            updateBuffer.append(" job_name=:inJobName,");
            parameters.addValue("inJobName", jobName);
        }
        if(StringUtils.isNotEmpty(jobGroup)) {
            updateBuffer.append(" job_group=:inJobGroup,");
            parameters.addValue("inJobGroup", jobGroup);
        }
        if(StringUtils.isNotEmpty(jobInvokeTarget)) {
            updateBuffer.append(" invoke_target=:inJobInTarget,");
            parameters.addValue("inJobInTarget", jobInvokeTarget);
        }
        if(StringUtils.isNotEmpty(jobExpression)) {
            updateBuffer.append(" cron_expression=:inJobExpress,");
            parameters.addValue("inJobExpress", jobExpression);
        }
        if(StringUtils.isNotEmpty(jobMisfire)) {
            updateBuffer.append(" misfire_policy=:inJobMisfire,");
            parameters.addValue("inJobMisfire", jobMisfire);
        }
        if(StringUtils.isNotEmpty(jobConcurrent)) {
            updateBuffer.append(" concurrent=:inJobConcur,");
            parameters.addValue("inJobConcur", jobConcurrent);
        }
        if(StringUtils.isNotEmpty(jobStatus)) {
            updateBuffer.append(" status=:inJobStatus,");
            parameters.addValue("inJobStatus", jobStatus);
        }
        if(StringUtils.isNotEmpty(jobRemark)) {
            updateBuffer.append(" remark=:inJobRemark,");
            parameters.addValue("inJobRemark", jobRemark);
        }

        updateBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE job_id=:inJobId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inJobId", jobId);

        int[] updatedResList = dbService.batchUpdate(updateBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    @Override
    @Transactional
    public int insertJob(SysJob job) {
        String jobName = job.getJobName();
        String jobGroup = job.getJobGroup();
        String jobInvokeTarget =job.getInvokeTarget();
        String jobExpression = job.getCronExpression();
        String jobMisfire = job.getMisfirePolicy();
        String jobConcurrent = job.getConcurrent();
        String jobStatus = job.getStatus();
        String jobRemark = job.getRemark();
        String createBy = job.getCreateBy();

        String insertSql = "INSERT INTO sys_job(job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, remark, create_by, create_time) VALUES(:inJobName, :inJobGroup, :inJobInTarget, :inJobExpress, :inJobMisfire, :inJobConcur, :inJobStatus, :inJobRemark, :inCreateBy, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inJobName", jobName);
        parameters.addValue("inJobGroup", jobGroup);
        parameters.addValue("inJobInTarget", jobInvokeTarget);
        parameters.addValue("inJobExpress", jobExpression);
        parameters.addValue("inJobMisfire", jobMisfire);
        parameters.addValue("inJobConcur", jobConcurrent);
        parameters.addValue("inJobStatus", jobStatus);
        parameters.addValue("inJobRemark", jobRemark);
        parameters.addValue("inCreateBy", createBy);

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

}
