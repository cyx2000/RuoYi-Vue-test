package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.repository.SysNoticeRepository;

@Service
public class SysNoticeRepositoryImpl implements SysNoticeRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT notice_id, notice_title, notice_type, CAST(notice_content AS char) AS notice_content, status, create_by, create_time, update_by, update_time, remark FROM sys_notice WHERE 1=1";

    public SysNoticeRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysNotice> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysNotice> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysNotice.class));
        return list;
    }
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        String sql = baseSelectSql + " AND notice_id=:inNoticeId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inNoticeId", noticeId);

        SysNotice queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysNotice.class));
        return queryObj;
    }

    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(notice, sqlBuilder, parameters);

        sqlBuilder.append("ORDER BY notice_id DESC");

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysNotice notice) {
        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(notice, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_notice WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        parameters.setDefaultOrderByStr("notice_id DESC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String querListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, querListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysNotice inNotice, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String noticeTitle = inNotice.getNoticeTitle();
        String noticeType = inNotice.getNoticeType();
        String createBy = inNotice.getCreateBy();

        if(StringUtils.isNotEmpty(noticeTitle)) {
            inBuilder.append(" AND notice_title LIKE CONCAT('%', :inNoticeTitle, '%')");
            inParameters.addValue("inNoticeTitle", noticeTitle);
        }
        if(StringUtils.isNotEmpty(noticeType)) {
            inBuilder.append(" AND notice_type=:inNoticeType");
            inParameters.addValue("inNoticeType", noticeType);
        }
        if(StringUtils.isNotEmpty(createBy)) {
            inBuilder.append(" AND create_by LIKE CONCAT('%', :inCreateBy, '%')");
            inParameters.addValue("inCreateBy", createBy);
        }
    }

    @Override
    public int insertNotice(SysNotice notice) {
        String noticeTitle = notice.getNoticeTitle();
        String noticeType = notice.getNoticeType();
        String noticeContent = notice.getNoticeContent();
        String noticeStatus = notice.getStatus();
        String noticeRemark = notice.getRemark();
        String createBy = notice.getCreateBy();

        String insertSql = "INSERT INTO sys_notice(notice_title, notice_type, notice_content, status, remark, create_by, create_time) VALUES(:inNoticeTitle, :inNoticeType, :inNoticeContent, :inNoticeStatus, :inNoticeRemark, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inNoticeTitle", noticeTitle);
        parameters.addValue("inNoticeType", noticeType);
        parameters.addValue("inNoticeContent", noticeContent);
        parameters.addValue("inNoticeStatus", noticeStatus);
        parameters.addValue("inNoticeRemark", noticeRemark);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateNotice(SysNotice notice) {
        Long noticeId = notice.getNoticeId();
        String noticeTitle = notice.getNoticeTitle();
        String noticeType = notice.getNoticeType();
        String noticeContent = notice.getNoticeContent();
        String noticeStatus = notice.getStatus();
        String noticeRemark = notice.getRemark();
        String updateBy = notice.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_notice SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(noticeTitle)) {
            updateSqlBuffer.append(" notice_title=:inNoticeTitle,");
            parameters.addValue("inNoticeTitle", noticeTitle);
        }
        if(StringUtils.isNotEmpty(noticeType)) {
            updateSqlBuffer.append(" notice_type=:inNoticeType,");
            parameters.addValue("inNoticeType", noticeType);
        }
        if(StringUtils.isNotEmpty(noticeContent)) {
            updateSqlBuffer.append(" notice_content=:inNoticeContent,");
            parameters.addValue("inNoticeContent", noticeContent);
        }
        if(StringUtils.isNotEmpty(noticeStatus)) {
            updateSqlBuffer.append(" status=:inNoticeStatus,");
            parameters.addValue("inNoticeStatus", noticeStatus);
        }
        if(StringUtils.isNotEmpty(noticeRemark)) {
            updateSqlBuffer.append(" remark=:inNoticeRemark,");
            parameters.addValue("inNoticeRemark", noticeRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE notice_id=:inNoticeId");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inNoticeId", noticeId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int deleteNoticeById(Long noticeId) {
        return this.deleteNoticeByIds(new Long[]{noticeId});
    }

    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        String deleteSql = "DELETE FROM sys_notice WHERE notice_id=:inNoticeId";
        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[noticeIds.length];

        for (int i = 0; i < parametersList.length; i++) {
            Long noticeId = noticeIds[i];
            parametersList[i] = new MapSqlParameterSource("inNoticeId", noticeId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);

        return deleteResList[0];
    }

}
