package com.ruoyi.i18n.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

    private String baseSelectSql = "SELECT a.tag_id, a.tag_type, a.module, a.label, a.to_app, a.status, a.create_by, a.create_time, a.update_by, a.update_time FROM lang_trans_tag a WHERE 1=1";

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
    public LangTransTag selectLangTransTagByTagId(Integer tagId) {
        String sql = baseSelectSql + " AND a.tag_id=:inTagId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTagId", tagId);

        LangTransTag queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(LangTransTag.class));
        return queryObj;
    }

    /**
     * 根据条件查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签集合
     */
    public List<LangTransTag> selectLangTransTagList(LangTransTag langTransTag) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(langTransTag, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    /**
     * 根据条件分页查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 分页完成的翻译标签集合
     */
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
            inBuilder.append(" AND a.tag_type LIKE CONCAT('%', :inTagType, '%')");
            inParameters.addValue("inTagType", tagType);
        }
        if(StringUtils.isNotEmpty(module)) {
            inBuilder.append(" AND a.module LIKE CONCAT('%', :inModule, '%')");
            inParameters.addValue("inModule", module);
        }
        if(StringUtils.isNotEmpty(label)) {
            inBuilder.append(" AND a.label LIKE CONCAT('%', :inLabel, '%')");
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
    public int insertLangTransTag(LangTransTag langTransTag) {
        String tagType = langTransTag.getTagType(); // 类型（比如java，file）
        String module = langTransTag.getModule(); // 模块（比如exception，write）
        String label = langTransTag.getLabel(); // 标签（比如serli，最后拼成java.excep.serli）
        String toApp = langTransTag.getToApp(); // 发给客户端（0不是，1是）
        Integer status = langTransTag.getStatus(); // 语言状态（0正常 1停用 2删除 3是删除和停用）
        String createBy = langTransTag.getCreateBy(); // 创建者

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inTagType", tagType);
        parameters.addValue("inModule", module);
        parameters.addValue("inLabel", label);
        parameters.addValue("inToApp", toApp);
        parameters.addValue("inStatus", status);
        parameters.addValue("inCreateBy", createBy);

        String insertSql = "INSERT INTO lang_trans_tag(tag_type,module,label,to_app,status,create_by,create_time) VALUES(:inTagType, :inModule, :inLabel, :inToApp, :inStatus, :inCreateBy, SYSDATE())";

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    /**
     * 修改翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    public int updateLangTransTag(LangTransTag langTransTag) {
        Integer tagId = langTransTag.getTagId();
        String tagType = langTransTag.getTagType();
        String module = langTransTag.getModule();
        String label = langTransTag.getLabel();
        String toApp = langTransTag.getToApp();
        Integer status = langTransTag.getStatus();
        String updateBy = langTransTag.getUpdateBy();

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(tagType))
        {
            parameters.addValue("inTagType", tagType);
        }
        if(StringUtils.isNotEmpty(module))
        {
            parameters.addValue("inModule", module);
        }
        if(StringUtils.isNotEmpty(label))
        {
            parameters.addValue("inLabel", label);
        }
        if(StringUtils.isNotEmpty(toApp))
        {
            parameters.addValue("inToApp", toApp);
        }
        if(StringUtils.isNotNull(status))
        {
            parameters.addValue("inStatus", status);
        }
        if(StringUtils.isNotEmpty(updateBy))
        {
            parameters.addValue("inUpdateBy", updateBy);
        }

        String updateSql = "UPDATE lang_trans_tag SET tag_type=:inTagType, module=:inModule, label=:inLabel, to_app=:inToApp, status=:inStatus, update_by=:inUpdateBy, update_time=SYSDATE() WHERE tag_id=:inTagId";

        parameters.addValue(":inTagId", tagId);

        int[] updatedResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    /**
     * 删除翻译标签
     *
     * @param tagId 翻译标签主键
     * @return 结果
     */
    public int deleteLangTransTagByTagId(Integer tagId) {
        return this.deleteLangTransTagByTagIds(new Integer[]{tagId});
    }

    /**
     * 批量删除翻译标签
     *
     * @param tagIds 需要删除的数据主键集合
     * @return 结果
     */
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
