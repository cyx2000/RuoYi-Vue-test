package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.repository.SysNoticeReadRepository;

@Service
public class SysNoticeReadRepositoryImpl implements SysNoticeReadRepository {

    private DBService dbService;

    public SysNoticeReadRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public int insertNoticeRead(SysNoticeRead noticeRead) {
        Long noticeId = noticeRead.getNoticeId();
        Long userId = noticeRead.getUserId();

        return this.insertNoticeReadBatch(userId, new Long[]{noticeId});
    }

    @Override
    public int selectUnreadCount(Long userId) {
        String sql = "SELECT COUNT(1) FROM sys_notice n WHERE n.status = '0' AND NOT EXISTS (SELECT 1 FROM sys_notice_read r WHERE r.notice_id = n.notice_id AND r.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        Integer queryObj = dbService.queryForObject(sql, parameters, Integer.class);
        return queryObj.intValue();
    }

    @Override
    public int selectIsRead(Long noticeId, Long userId) {
        String sql = "SELECT COUNT(1) FROM sys_notice_read WHERE notice_id=:inNoticeId AND user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inNoticeId", noticeId);
        parameters.addValue("inUserId", userId);

        Integer queryObj = dbService.queryForObject(sql, parameters, Integer.class);
        return queryObj.intValue();
    }

    @Override
    public int insertNoticeReadBatch(Long userId, Long[] noticeIds) {

        LocalDateTime readTime = LocalDateTime.now(ZoneId.of("UTC"));

        String insertSql = "INSERT IGNORE INTO sys_notice_read(notice_id, user_id, read_time) VALUES(:inNoticeId, :inUserId, :inReadTime)";

        MapSqlParameterSource[] parametersList =  new MapSqlParameterSource[noticeIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long noticeId = noticeIds[i];

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inNoticeId", noticeId);
            parameters.addValue("inUserId", userId);
            parameters.addValue("inReadTime", readTime);

            parametersList[i] = parameters;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList[0];
    }

    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit) {
        String sql = "SELECT n.notice_id AS noticeId, n.notice_title AS noticeTitle, n.notice_type AS noticeType, n.status, n.create_by AS createBy, n.create_time AS createTime, case WHEN r.notice_id IS NOT NULL THEN TRUE ELSE FALSE END AS isRead FROM sys_notice n LEFT JOIN sys_notice_read r ON r.notice_id = n.notice_id AND r.user_id=:inUserId WHERE n.status = '0' ORDER BY n.notice_id DESC LIMIT :inLimit";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUserId", userId);
        parameters.addValue("inLimit", limit);

        List<SysNotice> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysNotice.class));
        return list;
    }

    @Override
    public int deleteByNoticeIds(Long[] noticeIds) {
        String deleteSql = "DELETE FROM sys_notice_read WHERE notice_id=:inNoticeId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[noticeIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long noticeId = noticeIds[i];
            parametersList[i] = new MapSqlParameterSource("inNoticeId", noticeId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

}
