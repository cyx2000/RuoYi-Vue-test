package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.repository.SysPostRepository;

@Repository
public class SysPostRepositoryImpl implements SysPostRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT post_id, post_code, post_name, post_sort, status, create_by, create_time, remark FROM sys_post WHERE 1=1";

    public SysPostRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysPost> queryList(final MapSqlParameterSource parameters, final String querySql) {
         List<SysPost> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysPost.class));
        return list;
    }

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(post, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysPost post) {
        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(post, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_post WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String querListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, querListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysPost inPost, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String postCode = inPost.getPostCode();
        String postStatus = inPost.getStatus();
        String postName = inPost.getPostName();

        if(StringUtils.isNotEmpty(postCode)) {
            inBuilder.append(" AND post_code LIKE CONCAT('%', :inPostCode, '%')");
            inParameters.addValue("inPostCode", postCode);
        }
        if(StringUtils.isNotEmpty(postStatus)) {
            inBuilder.append(" AND status=:inPostStatus");
            inParameters.addValue("inPostStatus", postStatus);
        }
        if(StringUtils.isNotEmpty(postName)) {
            inBuilder.append(" AND post_name LIKE CONCAT('%', :inPostName, '%')");
            inParameters.addValue("inPostName", postName);
        }
    }

    @Override
    public List<SysPost> selectPostAll() {
        return queryList(null, baseSelectSql);
    }

    @Override
    public SysPost selectPostById(Long postId) {
        String sql = baseSelectSql + " AND post_id=:inPostId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inPostId",postId);

        SysPost queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysPost.class));
        return queryObj;
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        String sql = "SELECT p.post_id FROM sys_post p LEFT JOIN sys_user_post up ON up.post_id = p.post_id LEFT JOIN sys_user u ON u.user_id = up.user_id WHERE u.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        List<Long> list = dbService.queryForList(sql, parameters, Long.class);
        return list;
    }

    @Override
    public List<SysPost> selectPostsByUserName(String userName) {
        String sql = "SELECT p.post_id, p.post_name, p.post_code FROM sys_post p LEFT JOIN sys_user_post up ON up.post_id = p.post_id LEFT JOIN sys_user u ON u.user_id = up.user_id WHERE u.user_name=:inUsername";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUsername", userName);

        return queryList(parameters, sql);
    }

    @Override
    public int deletePostById(Long postId) {
        return this.deletePostByIds(new Long[]{postId});
    }

    @Override
    public int deletePostByIds(Long[] postIds) {
        String deleteSql = "DELETE FROM sys_post WHERE post_id=:inPostId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[postIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long postId = postIds[i];

            parametersList[i] = new MapSqlParameterSource("inPostId", postId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public int updatePost(SysPost post) {
        Long postId = post.getPostId();
        String postCode = post.getPostCode();
        String postName = post.getPostName();
        Integer postSort = post.getPostSort();
        String postStatus = post.getStatus();
        String postRemark = post.getRemark();
        String updateBy = post.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_post SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(postCode)) {
            updateSqlBuffer.append(" post_code=:inPostCode,");
            parameters.addValue("inPostCode", postCode);
        }
        if(StringUtils.isNotEmpty(postName)) {
            updateSqlBuffer.append(" post_name=:inPostName,");
            parameters.addValue("inPostName", postName);
        }
        if(StringUtils.isNotNull(postSort)) {
            updateSqlBuffer.append(" post_sort=:inPostSort,");
            parameters.addValue("inPostSort", postSort);
        }
        if(StringUtils.isNotEmpty(postStatus)) {
            updateSqlBuffer.append(" status=:inPostStatus,");
            parameters.addValue("inPostStatus", postStatus);
        }
        if(StringUtils.isNotEmpty(postRemark)) {
            updateSqlBuffer.append(" remark=:inPostRemark,");
            parameters.addValue("inPostRemark", postRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE post_id=:inPostId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inPostId", postId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int insertPost(SysPost post) {
        String postCode = post.getPostCode();
        String postName = post.getPostName();
        Integer postSort = post.getPostSort();
        String postStatus = post.getStatus();
        String postRemark = post.getRemark();
        String createBy = post.getCreateBy();

        String insertSql = "INSERT INTO sys_post(post_code, post_name, post_sort, status, remark, create_by, create_time) VALUES(:inPostCode, :inPostName, :inPostSort, :inPostStatus, :inPostRemark, :inCreateBy, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inPostCode", postCode);
        parameters.addValue("inPostName", postName);
        parameters.addValue("inPostSort", postSort);
        parameters.addValue("inPostStatus", postStatus);
        parameters.addValue("inPostRemark", postRemark);
        parameters.addValue("inCreateBy", createBy);

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public SysPost checkPostNameUnique(String postName) {
        String sql = baseSelectSql + " AND post_name=:inPostName LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inPostName", postName);

        SysPost queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysPost.class));
        return queryObj;
    }

    @Override
    public SysPost checkPostCodeUnique(String postCode) {
        String sql = baseSelectSql + " AND post_code=:inPostCode LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inPostCode", postCode);

        SysPost queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysPost.class));
        return queryObj;
    }

}
