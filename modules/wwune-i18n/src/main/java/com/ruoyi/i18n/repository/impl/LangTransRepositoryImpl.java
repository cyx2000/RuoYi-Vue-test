package com.ruoyi.i18n.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.domain.LangTrans;
import com.ruoyi.i18n.repository.LangTransRepository;

/**
 * 翻译文本Repository实现
 *
 * @author winter123
 * @date 2026-04-02
 */
@Repository
public class LangTransRepositoryImpl implements LangTransRepository
{
    private DBService dbService;

    private String baseSelectSql = "SELECT a.lang_id, a.tag_id, a.trans_text, a.create_by, a.create_time, a.update_by, a.update_time FROM lang_trans a WHERE 1=1";

    public LangTransRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<LangTrans> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<LangTrans> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(LangTrans.class));
        return list;
    }

    /**
     * 查询翻译文本
     *
     * @param langId 翻译文本主键
     * @return 翻译文本
     */
    public LangTrans selectLangTransByLangId(Integer langId) {
        String sql = baseSelectSql + " AND a.lang_id=:inLangId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inLangId", langId);

        LangTrans queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(LangTrans.class));
        return queryObj;
    }

    /**
     * 根据条件查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 翻译文本集合
     */
    public List<LangTrans> selectLangTransList(LangTrans langTrans) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTrans, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    /**
     * 根据条件分页查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 分页完成的翻译文本集合
     */
    public TableDataInfo getPagedListResp(LangTrans langTrans) {

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTrans, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM lang_trans a WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final LangTrans langTrans, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        Integer langId= langTrans.getLangId();
        Integer tagId = langTrans.getTagId();
        String createBy = langTrans.getCreateBy();
        String beginDateTime = langTrans.getBeginTimeParam();
        String endDateTime = langTrans.getEndTimeParam();

        if(StringUtils.isNotNull(langId)) {
            inBuilder.append(" AND a.lang_id=:inLangId");
            inParameters.addValue("inLangId", langId);
        }
        if(StringUtils.isNotNull(tagId)) {
            inBuilder.append(" AND a.tag_id=:inTagId");
            inParameters.addValue("inTagId", tagId);
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
     * 新增翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    public int insertLangTrans(LangTrans langTrans) {
        Integer longId = langTrans.getLangId(); // 语言序号
        Integer tagId = langTrans.getTagId(); // 标签序号
        String transText = langTrans.getTransText(); // 翻译文本
        String createBy = langTrans.getCreateBy(); // 创建者

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inLangId", longId);
        parameters.addValue("inTagId", tagId);
        parameters.addValue("inTransText", transText);
        parameters.addValue("inCreateBy", createBy);

        String insertSql = "INSERT INTO lang_trans(lang_id, tag_id,trans_text,create_by,create_time) VALUES(:inLangId, :inTagId, :inTransText, :inCreateBy, SYSDATE())";

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    /**
     * 修改翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    public int updateLangTrans(LangTrans langTrans) {
        Integer langId = langTrans.getLangId();
        Integer tagId = langTrans.getTagId();
        String transText = langTrans.getTransText();
        String updateBy = langTrans.getUpdateBy();

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotNull(tagId))
        {
            parameters.addValue("inTagId", tagId);
        }
        if(StringUtils.isNotEmpty(transText))
        {
            parameters.addValue("inTransText", transText);
        }
        if(StringUtils.isNotEmpty(updateBy))
        {
            parameters.addValue("inUpdateBy", updateBy);
        }

        String updateSql = "UPDATE lang_trans SET tag_id=:inTagId, trans_text=:inTransText, update_by=:inUpdateBy, update_time=SYSDATE() WHERE lang_id=:inLangId";

        parameters.addValue("inLangId", langId);

        int[] updatedResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    /**
     * 删除翻译文本
     *
     * @param langId 翻译文本主键
     * @return 结果
     */
    public int deleteLangTransByLangId(Integer langId) {
        return this.deleteLangTransByLangIds(new Integer[]{langId});
    }

    /**
     * 批量删除翻译文本
     *
     * @param langIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLangTransByLangIds(Integer[] langIds) {
        String deleteSql = "DELETE FROM lang_trans WHERE lang_id=:inLangId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[langIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Integer langId = langIds[i];
            parametersList[i] = new MapSqlParameterSource("inLangId", langId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }
}
