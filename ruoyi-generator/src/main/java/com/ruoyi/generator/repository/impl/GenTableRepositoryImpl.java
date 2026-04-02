package com.ruoyi.generator.repository.impl;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.repository.GenTableRepository;

@Repository
public class GenTableRepositoryImpl implements GenTableRepository {

    private DBService dbService;

    public GenTableRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<GenTable> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<GenTable> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(GenTable.class));
        return list;
    }

    @Override
    public TableDataInfo selectGenTableList(GenTable genTable) {
        String sql = "SELECT table_id, table_name, table_comment, sub_table_name, sub_table_fk_name, class_name, tpl_category, tpl_web_type, package_name, module_name, business_name, function_name, function_author, form_col_num, gen_type, gen_path, options, create_by, create_time, update_by, update_time, remark FROM gen_table WHERE 1=1";

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(genTable, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM gen_table WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = sql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    @Override
    public TableDataInfo selectDbTableList(GenTable genTable) {
        String sql = "SELECT table_name, table_comment, create_time, update_time FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND table_name NOT LIKE 'qrtz_%' AND table_name NOT LIKE 'gen_%' AND table_name NOT IN (SELECT table_name FROM gen_table)";

        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(genTable, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND table_name NOT LIKE 'qrtz_%' AND table_name NOT LIKE 'gen_%' AND table_name NOT IN (SELECT table_name FROM gen_table)";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        // 默认使用create_time排序
        parameters.setDefaultOrderByStr("create_time DESC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = sql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final GenTable inTable, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String tableName = inTable.getTableName();
        String tableComment = inTable.getTableComment();
        String beginDateTime = inTable.getBeginTimeParam();
        String endDateTime = inTable.getEndTimeParam();

        if(StringUtils.isNotEmpty(tableName)) {
            inBuilder.append(" AND lower(table_name) like lower(concat('%', :inTableName, '%'))");
            inParameters.addValue("inTableName", tableName);
        }
        if(StringUtils.isNotEmpty(tableComment)) {
            inBuilder.append(" AND lower(table_comment) like lower(concat('%', :inTableComm, '%'))");
            inParameters.addValue("inTableComm", tableComment);
        }
        if(StringUtils.isNotEmpty(beginDateTime) && StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND create_time BETWEEN :inBeginTime AND :inEndTime");
            inParameters.addValue("inBeginTime", beginDateTime);
            inParameters.addValue("inEndTime", endDateTime);
        }
    }

    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        String sql = "SELECT table_name, table_comment, create_time, update_time FROM INFORMATION_SCHEMA.TABLES WHERE table_name NOT LIKE 'qrtz_%' AND table_name NOT LIKE 'gen_%' AND TABLE_SCHEMA = (SELECT DATABASE()) AND table_name IN(:inTbNames)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTbNames", Arrays.asList(tableNames));

        return queryList(parameters, sql);
    }

    @Override
    public List<GenTable> selectGenTableAll() {
        String sql = "SELECT t.table_id, t.table_name, t.table_comment, t.sub_table_name, t.sub_table_fk_name, t.class_name, t.tpl_category, t.tpl_web_type, t.package_name, t.module_name, t.business_name, t.function_name, t.function_author, t.form_col_num, t.options, t.remark FROM gen_table t";

        List<GenTable> list = queryList(null, sql);

        sql = "SELECT c.column_id, c.table_id, c.column_name, c.column_comment, c.column_type, c.java_type, c.java_field, c.is_pk, c.is_increment, c.is_required, c.is_insert, c.is_edit, c.is_list, c.is_query, c.query_type, c.html_type, c.dict_type, c.sort FROM gen_table_column c WHERE c.table_id=:inTaId ORDER BY c.sort";

        for (GenTable genTable : list) {
            Long tableId = genTable.getTableId();

            MapSqlParameterSource parameters = new MapSqlParameterSource("inTaId", tableId);

            List<GenTableColumn> columns = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(GenTableColumn.class));

            genTable.setColumns(columns);
        }

        return list;
    }

    @Override
    public GenTable selectGenTableById(Long tableId) {
        String sql = "SELECT t.table_id, t.table_name, t.table_comment, t.sub_table_name, t.sub_table_fk_name, t.class_name, t.tpl_category, t.tpl_web_type, t.package_name, t.module_name, t.business_name, t.function_name, t.function_author, t.form_col_num, t.gen_type, t.gen_path, t.options, t.remark FROM gen_table t WHERE t.table_id=:inTaId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTaId", tableId);

        GenTable queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(GenTable.class));

        sql = "SELECT c.column_id, c.table_id, c.column_name, c.column_comment, c.column_type, c.java_type, c.java_field, c.is_pk, c.is_increment, c.is_required, c.is_insert, c.is_edit, c.is_list, c.is_query, c.query_type, c.html_type, c.dict_type, c.sort FROM gen_table_column c WHERE c.table_id=:inTaId ORDER BY c.sort";

        List<GenTableColumn> columns = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(GenTableColumn.class));

        queryObj.setColumns(columns);
        return queryObj;
    }

    @Override
    public GenTable selectGenTableByName(String tableName) {
        String sql = "SELECT t.table_id, t.table_name, t.table_comment, t.sub_table_name, t.sub_table_fk_name, t.class_name, t.tpl_category, t.tpl_web_type, t.package_name, t.module_name, t.business_name, t.function_name, t.function_author, t.form_col_num, t.gen_type, t.gen_path, t.options, t.remark FROM gen_table t WHERE t.table_name=:inTaName";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTaName", tableName);

        GenTable queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(GenTable.class));

        sql = "SELECT c.column_id, c.table_id, c.column_name, c.column_comment, c.column_type, c.java_type, c.java_field, c.is_pk, c.is_increment, c.is_required, c.is_insert, c.is_edit, c.is_list, c.is_query, c.query_type, c.html_type, c.dict_type, c.sort FROM gen_table_column c WHERE c.table_id=:inTaId ORDER BY c.sort";

        parameters.addValue("inTaId", queryObj.getTableId());

        List<GenTableColumn> columns = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(GenTableColumn.class));

        queryObj.setColumns(columns);
        return queryObj;
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public long insertGenTable(GenTable genTable) {
        String tableName = genTable.getTableName();
        String tableComment  = genTable.getTableComment();
        String tableClass = genTable.getClassName();
        String tableCategory = StringUtils.isEmpty(genTable.getTplCategory()) ? "crud" : genTable.getTplCategory();
        String tableWebType = genTable.getTplWebType();
        String tablePackName = genTable.getPackageName();
        String tableModuleName = genTable.getModuleName();
        String tableBusinessName = genTable.getBusinessName();
        String tableFuncName = genTable.getFunctionName();
        String tableFuncAuthor = genTable.getFunctionAuthor();
        Short tableFormNum = StringUtils.isNull(genTable.getFormColNum()) ? 1 : genTable.getFormColNum();
        String tableGenType = StringUtils.isEmpty(genTable.getGenType()) ? "0" : genTable.getGenType();
        String tableGenPath = StringUtils.isEmpty(genTable.getGenPath()) ? "/" : genTable.getGenPath();
        String tableRemark = StringUtils.isEmpty(genTable.getRemark()) ? "" : genTable.getRemark();
        String createBy = genTable.getCreateBy();

        String insertSql = "INSERT INTO gen_table(table_name, table_comment, class_name, tpl_category, tpl_web_type, package_name, module_name, business_name, function_name, function_author, form_col_num, gen_type, gen_path, remark, create_by, create_time) VALUES(:inTaName, :inTaComm, :inTaClass, :inTaCateg, :inTaWebTyp, :inTaPack, :inTaModule, :inTaBusi, :inTaFuName, :inTaFuAuthor, :inTaForNum, :inTaGeTyp, :inTaGePath, :inTaRemark, :inCreateBy, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inTaName", tableName);
        parameters.addValue("inTaComm", tableComment);
        parameters.addValue("inTaClass", tableClass);
        parameters.addValue("inTaCateg", tableCategory);
        parameters.addValue("inTaWebTyp", tableWebType);
        parameters.addValue("inTaPack", tablePackName);
        parameters.addValue("inTaModule", tableModuleName);
        parameters.addValue("inTaBusi", tableBusinessName);
        parameters.addValue("inTaFuName", tableFuncName);
        parameters.addValue("inTaFuAuthor", tableFuncAuthor);
        parameters.addValue("inTaForNum", tableFormNum);
        parameters.addValue("inTaGeTyp", tableGenType);
        parameters.addValue("inTaGePath", tableGenPath);
        parameters.addValue("inTaRemark", tableRemark);
        parameters.addValue("inCreateBy", createBy);

        long pK = dbService.insertAndReturnPk(insertSql, parameters);
        return pK;
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int updateGenTable(GenTable genTable) {
        Long tableId = genTable.getTableId();
        String tableName = genTable.getTableName();
        String tableComment  = genTable.getTableComment();
        String tableSubName   = genTable.getSubTableName();
        String tableSubFkName   = genTable.getSubTableFkName();
        String tableClass = genTable.getClassName();
        String tableFuncAuthor = genTable.getFunctionAuthor();
        Short tableFormNum = genTable.getFormColNum();
        String tableGenType = genTable.getGenType();
        String tableGenPath = genTable.getGenPath();
        String tableCategory = genTable.getTplCategory();
        String tableWebType = genTable.getTplWebType();
        String tablePackName = genTable.getPackageName();
        String tableModuleName = genTable.getModuleName();
        String tableBusinessName = genTable.getBusinessName();
        String tableFuncName = genTable.getFunctionName();
        String tableOptions = genTable.getOptions();
        String tableRemark = genTable.getRemark();
        String updateBy = genTable.getUpdateBy();

        StringBuffer updateBuffer = new StringBuffer("UPDATE gen_table SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.equals(tableCategory, "sub"))
        {
            if(StringUtils.isNotEmpty(tableSubName)) {
                updateBuffer.append(" sub_table_name=:inTaSub,");
                parameters.addValue("inTaSub", tableSubName);
            }
            if(StringUtils.isNotEmpty(tableSubFkName)) {
                updateBuffer.append(" sub_table_fk_name=:inTaSubFk,");
                parameters.addValue("inTaSubFk", tableSubFkName, Types.NULL);
            }
        } else if(StringUtils.isNotNull(tableSubName)) {
            updateBuffer.append(" sub_table_name=:inTaSub,");
            parameters.addValue("inTaSub", null, Types.NULL);
            updateBuffer.append(" sub_table_fk_name=:inTaSubFk,");
            parameters.addValue("inTaSubFk", null, Types.NULL);
        }

        if(StringUtils.isNotEmpty(tableName)) {
            updateBuffer.append(" table_name=:inTaName,");
            parameters.addValue("inTaName", tableName);
        }
        if(StringUtils.isNotEmpty(tableComment)) {
            updateBuffer.append(" table_comment=:inTaComm,");
            parameters.addValue("inTaComm", tableComment);
        }
        if(StringUtils.isNotEmpty(tableClass)) {
            updateBuffer.append(" class_name=:inTaClass,");
            parameters.addValue("inTaClass", tableClass);
        }
        if(StringUtils.isNotEmpty(tableFuncAuthor)) {
            updateBuffer.append(" function_author=:inTaFuAuthor,");
            parameters.addValue("inTaFuAuthor", tableFuncAuthor);
        }
        if(StringUtils.isNotNull(tableFormNum)) {
            updateBuffer.append(" form_col_num=:inTaForNum,");
            parameters.addValue("inTaForNum", tableFormNum);
        }
        if(StringUtils.isNotEmpty(tableGenType)) {
            updateBuffer.append(" gen_type=:inTaGeTyp,");
            parameters.addValue("inTaGeTyp", tableGenType);
        }
        if(StringUtils.isNotEmpty(tableGenPath)) {
            updateBuffer.append(" gen_path=:inTaGePath,");
            parameters.addValue("inTaGePath", tableGenPath);
        }
        if(StringUtils.isNotEmpty(tableCategory)) {
            updateBuffer.append(" tpl_category=:inTaCateg,");
            parameters.addValue("inTaCateg", tableCategory);
        }
        if(StringUtils.isNotEmpty(tableWebType)) {
            updateBuffer.append(" tpl_web_type=:inTaWebTyp,");
            parameters.addValue("inTaWebTyp", tableWebType);
        }
        if(StringUtils.isNotEmpty(tablePackName)) {
            updateBuffer.append(" package_name=:inTaPack,");
            parameters.addValue("inTaPack", tablePackName);
        }
        if(StringUtils.isNotEmpty(tableModuleName)) {
            updateBuffer.append(" module_name=:inTaModule,");
            parameters.addValue("inTaModule", tableModuleName);
        }
        if(StringUtils.isNotEmpty(tableBusinessName)) {
            updateBuffer.append(" business_name=:inTaBusi,");
            parameters.addValue("inTaBusi", tableBusinessName);
        }
        if(StringUtils.isNotEmpty(tableFuncName)) {
            updateBuffer.append(" function_name=:inTaFuName,");
            parameters.addValue("inTaFuName", tableFuncName);
        }
        if(StringUtils.isNotEmpty(tableOptions)) {
            updateBuffer.append(" options=:inTaOpt,");
            parameters.addValue("inTaOpt", tableOptions);
        }
        if(StringUtils.isNotEmpty(tableRemark)) {
            updateBuffer.append(" remark=:inTaRemark,");
            parameters.addValue("inTaRemark", tableRemark);
        }

        updateBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE table_id=:inTaId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inTaId", tableId);

        int[] updatedResList = dbService.batchUpdate(updateBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int deleteGenTableByIds(Long[] tableIds) {
        String deleteSql = "DELETE FROM gen_table WHERE table_id=:inTaId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[tableIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long tableId = tableIds[i];

            parametersList[i] = new MapSqlParameterSource("inTaId", tableId);
        }

        int[] insertResList = dbService.batchUpdate(deleteSql, parametersList);
        return insertResList[0];
    }

    @Override
    @Transactional()
    public int createTable(String sql) {
        int exec = dbService.execute(sql);
        return exec;
    }
}
