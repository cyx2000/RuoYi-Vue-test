package com.ruoyi.i18n.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.domain.LangTransTag;
import com.ruoyi.i18n.repository.LangTransTagRepository;

/**
 * 翻译标签Repository实现
 *
 * @author winter123
 * @date 2026-04-02
 */
@Repository
public class LangTransTagRepositoryImpl implements LangTransTagRepository
{
    private DBService dbService;

    private String baseSelectSql = "SELECT a.tag_id, a.tag_type, a.module, a.label, a.to_app, a.create_by, a.create_time, a.update_by, a.update_time FROM lang_trans_tag a WHERE 1=1";

    public LangTransTagRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<LangTransTag> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<LangTransTag> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(LangTransTag.class));
        return list;
    }

    /**
     * 查询翻译标签
     *
     * @param tagId 翻译标签主键
     * @return 翻译标签
     */
    @Override
    public LangTransTag selectLangTransTagByTagId(Integer tagId) {
        String sql = baseSelectSql + " AND a.tag_id=:inTagId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTagId", tagId);

        LangTransTag queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(LangTransTag.class));
        return queryObj;
    }

    /**
     * 根据条件查询翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签
     */
    @Override
    public LangTransTag selectLangTransTag(LangTransTag langTransTag) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTransTag, sqlBuilder, parameters);

        LangTransTag queryObj = dbService.queryForObject(sqlBuilder.toString(), parameters, new SimplePropertyRowMapper<>(LangTransTag.class));
        return queryObj;
    }

    /**
     * 根据条件查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签集合
     */
    @Override
    public List<LangTransTag> selectLangTransTagList(LangTransTag langTransTag) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTransTag, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    /**
     * 查询翻译标签列表
     *
     * @param tagIds 翻译标签id列表
     * @return 翻译标签集合
     */
    @Override
    public List<LangTransTag> selectLangTransTagListByIds(List<Integer> tagIds)
    {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inTagIds", tagIds);

        String sql = baseSelectSql + " AND a.tag_id IN (:inTagIds)";

        return queryList(parameters, sql);
    }

    /**
     * 查询翻译标签总个数
     *
     * @return 总数
     */
    public long selectCountLangTransTag()
    {
        String selectCountSql = "SELECT COUNT(1) FROM lang_trans_tag a WHERE 1=1";
        long total = dbService.getTotalRows(selectCountSql, null);

        return total;
    }

    /**
     * 根据条件查询模块下的翻译标签id列表
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签id集合
     */
    @Override
    public List<Integer> selectModuleLangTransTagIds(LangTransTag langTransTag)
    {
        StringBuilder sqlBuilder = new StringBuilder("SELECT a.tag_id FROM lang_trans_tag a WHERE 1=1");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTransTag, sqlBuilder, parameters);

        SqlRowSet rs = dbService.getNamedJdbc().queryForRowSet(sqlBuilder.toString(), parameters);

        List<Integer> ids = new ArrayList<>();

        while (rs.next()) {
            ids.add(rs.getInt("tag_id"));
        }

        return ids;
    }

    /**
     * 根据条件分页查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 分页完成的翻译标签集合
     */
    @Override
    public TableDataInfo getPagedListResp(LangTransTag langTransTag) {

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTransTag, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM lang_trans_tag a WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final LangTransTag langTransTag, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String tagType = langTransTag.getTagType();
        String module = langTransTag.getModule();
        String label = langTransTag.getLabel();
        String toApp = langTransTag.getToApp();
        String createBy = langTransTag.getCreateBy();
        String beginDateTime = langTransTag.getBeginTimeParam();
        String endDateTime = langTransTag.getEndTimeParam();

        if(StringUtils.isNotEmpty(tagType)) {
            inBuilder.append(" AND a.tag_type=:inTagType");
            inParameters.addValue("inTagType", tagType);
        }
        if(StringUtils.isNotEmpty(module)) {
            inBuilder.append(" AND a.module=:inModule");
            inParameters.addValue("inModule", module);
        }
        if(StringUtils.isNotEmpty(label)) {
            inBuilder.append(" AND a.label=:inLabel");
            inParameters.addValue("inLabel", label);
        }
        if(StringUtils.isNotEmpty(toApp)) {
            inBuilder.append(" AND a.to_app=:inToApp");
            inParameters.addValue("inToApp", toApp);
        }
        if(StringUtils.isNotEmpty(createBy)) {
            inBuilder.append(" AND a.create_by=:inCreateBy");
            inParameters.addValue("inCreateBy", createBy);
        }
        if(StringUtils.isNotEmpty(beginDateTime) && StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND a.create_time BETWEEN :inBeginTime AND :inEndTime");
            inParameters.addValue("inBeginTime", beginDateTime);
            inParameters.addValue("inEndTime", endDateTime);
        }
    }
    /**
     * 新增翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    @Override
    public int insertLangTransTag(LangTransTag langTransTag) {
        int[] insertResList = this.batchInsertLangTransTag(Arrays.asList(langTransTag));
        return insertResList[0];
    }

    /**
     * 批量新增翻译标签
     *
     * @param langTransTags 翻译标签列表
     * @return 结果
     */
    @Override
    public int[] batchInsertLangTransTag(List<LangTransTag> langTransTags)
    {
        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[langTransTags.size()];

        for (int i = 0; i < parametersList.length; ++i) {
            LangTransTag langTransTag = langTransTags.get(i);
            String tagType = langTransTag.getTagType(); // 类型（比如java，file）
            String module = langTransTag.getModule(); // 模块（比如exception，write）
            String label = langTransTag.getLabel(); // 标签（比如serli，最后拼成java.excep.serli）
            String toApp = langTransTag.getToApp(); // 发给客户端（0不是，1是）
            String createBy = langTransTag.getCreateBy(); // 创建者

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inTagType", tagType);
            parameters.addValue("inModule", module);
            parameters.addValue("inLabel", label);
            parameters.addValue("inToApp", toApp);
            parameters.addValue("inCreateBy", createBy);

            parametersList[i] = parameters;
        }

        String insertSql = "INSERT INTO lang_trans_tag(tag_type,module,label,to_app,create_by,create_time) VALUES(:inTagType, :inModule, :inLabel, :inToApp, :inCreateBy, SYSDATE())";

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList;
    }

    /**
     * 新增翻译标签并返回主键
     *
     * @param langTransTag 翻译标签
     * @return Id
     */
    @Override
    public long insertLangTransTagAndReturnId(LangTransTag langTransTag)
    {
        String tagType = langTransTag.getTagType(); // 类型（比如java，file）
        String module = langTransTag.getModule(); // 模块（比如exception，write）
        String label = langTransTag.getLabel(); // 标签（比如serli，最后拼成java.excep.serli）
        String toApp = langTransTag.getToApp(); // 发给客户端（N不是，Y是）
        String createBy = langTransTag.getCreateBy(); // 创建者

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inTagType", tagType);
        parameters.addValue("inModule", module);
        parameters.addValue("inLabel", label);
        parameters.addValue("inToApp", toApp);
        parameters.addValue("inCreateBy", createBy);

        String insertSql = "INSERT INTO lang_trans_tag(tag_type,module,label,to_app,create_by,create_time) VALUES(:inTagType, :inModule, :inLabel, :inToApp, :inCreateBy, SYSDATE())";

        long pk = dbService.insertAndReturnPk(insertSql, parameters);
        return pk;
    }

    /**
     * 修改翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    @Override
    public int updateLangTransTag(LangTransTag langTransTag) {
        Integer tagId = langTransTag.getTagId();
        String tagType = langTransTag.getTagType();
        String module = langTransTag.getModule();
        String label = langTransTag.getLabel();
        String toApp = langTransTag.getToApp();
        String updateBy = langTransTag.getUpdateBy();

        StringBuffer updateBuffer = new StringBuffer("UPDATE lang_trans_tag SET");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(tagType))
        {
            updateBuffer.append(" tag_type=:inTagType,");
            parameters.addValue("inTagType", tagType);
        }
        if(StringUtils.isNotEmpty(module))
        {
            updateBuffer.append(" module=:inModule,");
            parameters.addValue("inModule", module);
        }
        if(StringUtils.isNotEmpty(label))
        {
            updateBuffer.append(" label=:inLabel,");
            parameters.addValue("inLabel", label);
        }
        if(StringUtils.isNotEmpty(toApp))
        {
            updateBuffer.append(" to_app=:inToApp,");
            parameters.addValue("inToApp", toApp);
        }

        int res = 0;
        if(parameters.hasValues()) {
            updateBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE tag_id=:inTagId");

            parameters.addValue("inTagId", tagId);
            parameters.addValue("inUpdateBy", updateBy);

            int[] updatedResList = dbService.batchUpdate(updateBuffer.toString(), new MapSqlParameterSource[]{parameters});
            res = updatedResList[0];
        }
        return res;
    }

    /**
     * 删除翻译标签
     *
     * @param tagId 翻译标签主键
     * @return 结果
     */
    @Override
    public int deleteLangTransTagByTagId(Integer tagId) {
        return this.deleteLangTransTagByTagIds(new Integer[]{tagId});
    }

    /**
     * 批量删除翻译标签
     *
     * @param tagIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteLangTransTagByTagIds(Integer[] tagIds) {
        String deleteSql = "DELETE FROM lang_trans_tag WHERE tag_id=:inTagId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[tagIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Integer tagId = tagIds[i];
            parametersList[i] = new MapSqlParameterSource("inTagId", tagId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }
}
