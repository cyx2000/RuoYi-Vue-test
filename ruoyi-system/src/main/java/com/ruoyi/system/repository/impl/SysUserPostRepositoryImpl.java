package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.system.domain.SysUserPost;
import com.ruoyi.system.repository.SysUserPostRepository;

@Repository
public class SysUserPostRepositoryImpl implements SysUserPostRepository {

    private DBService dbService;

    public SysUserPostRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    @Transactional
    public int deleteUserPostByUserId(Long userId) {
        return this.deleteUserPost(new Long[]{userId});
    }

    @Override
    public int countUserPostById(Long postId) {
        String sql = "SELECT COUNT(1) FROM sys_user_post WHERE post_id=:inPostId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inPostId", postId);

        Integer queryObj = dbService.queryForObject(sql, parameters, Integer.class);
        return queryObj.intValue();
    }

    @Override
    @Transactional
    public int deleteUserPost(Long[] userIds) {
        String deleteSql = "DELETE FROM sys_user_post WHERE user_id=:inUserId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long userId = userIds[i];

            parametersList[i] = new MapSqlParameterSource("inUserId", userId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    @Transactional
    public int batchUserPost(List<SysUserPost> userPostList) {
        String insertSql = "INSERT INTO sys_user_post(user_id, post_id) VALUES(:inUserId, :inPostId)";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userPostList.size()];
        for (int i = 0; i < parametersList.length; i++) {
            SysUserPost userPost = userPostList.get(i);
            Long userId = userPost.getUserId();
            Long postId = userPost.getPostId();

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inPostId", postId);
            parameters.addValue("inUserId", userId);
            parameters.addValue("inPostId", postId);

            parametersList[i] = parameters;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList[0];
    }

}
