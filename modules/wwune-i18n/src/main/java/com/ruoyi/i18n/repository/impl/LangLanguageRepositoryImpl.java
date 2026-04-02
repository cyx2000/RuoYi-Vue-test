package com.ruoyi.i18n.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.domain.LangLanguage;
import com.ruoyi.i18n.repository.LangLanguageRepository;

/**
 * 语言Repository实现
 *
 * @author winter123
 * @date 2026-04-02
 */
@Repository
public class LangLanguageRepositoryImpl implements LangLanguageRepository
{
    private DBService dbService;

    private String baseSelectSql = "SELECT a.lang_id, a.lang_tag, a.sort, a.status, a.is_default, a.remark, a.create_by, a.create_time FROM lang_language a WHERE 1=1";

    public LangLanguageRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<LangLanguage> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<LangLanguage> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(LangLanguage.class));
        return list;
    }

    /**
     * 查询语言
     *
     * @param langId 语言主键
     * @return 语言
     */
    public LangLanguage selectLangLanguageByLangId(Integer langId) {
        String sql = baseSelectSql + " AND a.lang_id=:inLangId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inLangId", langId);

        LangLanguage queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(LangLanguage.class));
        return queryObj;
    }

    /**
     * 查询语言列表
     *
     * @param langLanguage 语言
     * @return 语言集合
     */
    public List<LangLanguage> selectLangLanguageList(LangLanguage langLanguage) {
        String sql = baseSelectSql + " ORDER BY a.sort ASC";

        return queryList(null, sql);
    }

    /**
     * 分页查询语言列表
     *
     * @param langLanguage 语言
     * @return 分页完成的语言集合
     */
    public TableDataInfo getPagedListResp(LangLanguage langLanguage) {
        String selectCountSql = "SELECT COUNT(1) FROM lang_language a WHERE 1=1";

        TableDataInfo pagedResp = dbService.getPagedRespInfo(selectCountSql, null);

        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        parameters.setDefaultOrderByStr("a.sort ASC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    /**
     * 新增语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    public int insertLangLanguage(LangLanguage langLanguage) {
        String langTag = langLanguage.getLangTag(); // 语言标签
        Integer sort = langLanguage.getSort(); // 展示顺序
        Integer status = langLanguage.getStatus(); // 语言状态（0正常 1停用 2删除 3是删除和停用）
        String isDefault = langLanguage.getIsDefault(); // 默认语言（0不是默认，1是默认）
        String remark = langLanguage.getRemark(); // 备注
        String createBy = langLanguage.getCreateBy(); // 创建者

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inLangTag", langTag);
        parameters.addValue("inSort", sort);
        parameters.addValue("inStatus", status);
        parameters.addValue("inIsDefault", isDefault);
        parameters.addValue("inRemark", remark);
        parameters.addValue("inCreateBy", createBy);

        String insertSql = "INSERT INTO lang_language(lang_tag,sort,status,is_default,remark,create_by,create_time) VALUES(:inLangTag, :inSort, :inStatus, :inIsDefault, :inRemark, :inCreateBy, SYSDATE())";

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    /**
     * 修改语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    public int updateLangLanguage(LangLanguage langLanguage) {
        Integer langId = langLanguage.getLangId();
        String langTag = langLanguage.getLangTag();
        Integer sort = langLanguage.getSort();
        Integer status = langLanguage.getStatus();
        String isDefault = langLanguage.getIsDefault();
        String remark = langLanguage.getRemark();

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(langTag))
        {
            parameters.addValue("inLangTag", langTag);
        }
        if(StringUtils.isNotNull(sort))
        {
            parameters.addValue("inSort", sort);
        }
        if(StringUtils.isNotNull(status))
        {
            parameters.addValue("inStatus", status);
        }
        if(StringUtils.isNotEmpty(isDefault))
        {
            parameters.addValue("inIsDefault", isDefault);
        }
        if(StringUtils.isNotEmpty(remark))
        {
            parameters.addValue("inRemark", remark);
        }

        String updateSql = "UPDATE lang_language SET lang_tag=:inLangTag, sort=:inSort, status=:inStatus, is_default=:inIsDefault, remark=:inRemark WHERE lang_id=:inLangId";

        parameters.addValue(":inLangId", langId);

        int[] updatedResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    /**
     * 删除语言
     *
     * @param langId 语言主键
     * @return 结果
     */
    public int deleteLangLanguageByLangId(Integer langId) {
        return this.deleteLangLanguageByLangIds(new Integer[]{langId});
    }

    /**
     * 批量删除语言
     *
     * @param langIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLangLanguageByLangIds(Integer[] langIds) {
        String deleteSql = "DELETE FROM lang_language WHERE lang_id=:inLangId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[langIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Integer langId = langIds[i];
            parametersList[i] = new MapSqlParameterSource("inLangId", langId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }
}
