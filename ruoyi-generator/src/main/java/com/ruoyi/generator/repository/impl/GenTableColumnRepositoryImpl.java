package com.ruoyi.generator.repository.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.repository.GenTableColumnRepository;

@Repository
public class GenTableColumnRepositoryImpl implements GenTableColumnRepository {

    private DBService dbService;

    public GenTableColumnRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<GenTableColumn> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<GenTableColumn> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(GenTableColumn.class));
        return list;
    }

    @Override
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName) {
        String sql = "SELECT column_name, (CASE WHEN (IS_NULLABLE = 'no' && COLUMN_KEY != 'PRI') THEN '1' ELSE '0' END) AS is_required, (CASE WHEN COLUMN_KEY = 'PRI' THEN '1' ELSE '0' END) AS is_pk, ORDINAL_POSITION AS sort, column_comment, (CASE WHEN EXTRA = 'AUTO_INCREMENT' THEN '1' ELSE '0' END) AS is_increment, column_type FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME=(:inTaName) ORDER BY ORDINAL_POSITION";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTaName", tableName);

        return queryList(parameters, sql);
    }

    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId) {
        String sql = "SELECT column_id, table_id, column_name, column_comment, column_type, java_type, java_field, is_pk, is_increment, is_required, is_insert, is_edit, is_list, is_query, query_type, html_type, dict_type, sort, create_by, create_time, update_by, update_time FROM gen_table_column WHERE table_id=:inTaId ORDER BY sort";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inTaId", tableId);

        return queryList(parameters, sql);
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int[] batchInsertGenTableColumn(List<GenTableColumn> genTableColumns) {
        String insertSql = "INSERT INTO gen_table_column(table_id, column_name, column_comment, column_type, java_type, java_field, is_pk, is_increment, is_required, is_insert, is_edit, is_list, is_query, query_type, html_type, dict_type, sort, create_by, create_time) VALUES(:inTaId, :inTColName, :inTColComm, :inTColTyp, :inTJaTyp, :inTJaFie, :inTIsPk, :inTIsIncr, :inTIsRequ, :inTIsInsert, :inTIsEdit, :inTIsList, :inTIsQuery, :inTQueTyp, :inTHtmlTyp, :inTDictTyp, :inTSort, :inCreateBy, SYSDATE())";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[genTableColumns.size()];

        for (int i = 0; i < parametersList.length; i++) {
            GenTableColumn genTableColumn = genTableColumns.get(i);

            Long tableId = genTableColumn.getTableId();
            String tColumnName = genTableColumn.getColumnName();
            String tColumnComment = genTableColumn.getColumnComment();
            String tColumnType = genTableColumn.getColumnType();
            String tJavaType = genTableColumn.getJavaType();
            String tJavaField = genTableColumn.getJavaField();
            String tIsPk = genTableColumn.getIsPk();
            String tIsIncrement = genTableColumn.getIsIncrement();
            String tIsRequired = genTableColumn.getIsRequired();
            String tIsInsert = genTableColumn.getIsInsert();
            String tIsEdit = StringUtils.isEmpty(genTableColumn.getIsEdit()) ? "0" : "1";
            String tIsList = StringUtils.isEmpty(genTableColumn.getIsList()) ? "0" : "1";
            String tIsQuery = StringUtils.isEmpty(genTableColumn.getIsQuery()) ? "0" : "1";
            String tQueryType = genTableColumn.getQueryType();
            String tHtmlType = genTableColumn.getHtmlType();
            String tDictType = StringUtils.isEmpty(genTableColumn.getDictType()) ? "" : genTableColumn.getDictType();
            Integer tSort = genTableColumn.getSort();
            String createBy = genTableColumn.getCreateBy();

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inTaId", tableId);
            parameters.addValue("inTColName", tColumnName);
            parameters.addValue("inTColComm", tColumnComment);
            parameters.addValue("inTColTyp", tColumnType);
            parameters.addValue("inTJaTyp", tJavaType);
            parameters.addValue("inTJaFie", tJavaField);
            parameters.addValue("inTIsPk", tIsPk);
            parameters.addValue("inTIsIncr", tIsIncrement);
            parameters.addValue("inTIsRequ", tIsRequired);
            parameters.addValue("inTIsInsert", tIsInsert);
            parameters.addValue("inTIsEdit", tIsEdit);
            parameters.addValue("inTIsList", tIsList);
            parameters.addValue("inTIsQuery", tIsQuery);
            parameters.addValue("inTQueTyp", tQueryType);
            parameters.addValue("inTHtmlTyp", tHtmlType);
            parameters.addValue("inTDictTyp", tDictType);
            parameters.addValue("inTSort", tSort);
            parameters.addValue("inCreateBy", createBy);

            parametersList[i] = parameters;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList;
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int insertGenTableColumn(GenTableColumn genTableColumn) {
        return this.batchInsertGenTableColumn(Arrays.asList(genTableColumn))[0];
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int updateGenTableColumn(GenTableColumn genTableColumn) {
        Long tColumnId = genTableColumn.getColumnId();
        String tColumnComment = genTableColumn.getColumnComment();
        String tJavaType = genTableColumn.getJavaType();
        String tJavaField = genTableColumn.getJavaField();
        String tIsInsert = genTableColumn.getIsInsert();
        String tIsEdit = genTableColumn.getIsEdit();
        String tIsList = genTableColumn.getIsList();
        String tIsQuery = genTableColumn.getIsQuery();
        String tIsRequired = genTableColumn.getIsRequired();
        String tQueryType = genTableColumn.getQueryType();
        String tHtmlType = genTableColumn.getHtmlType();
        String tDictType = genTableColumn.getDictType();
        Integer tSort = genTableColumn.getSort();
        String updateBy = genTableColumn.getUpdateBy();

        StringBuffer updateBuffer = new StringBuffer("UPDATE gen_table_column SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(tColumnComment)) {
            updateBuffer.append(" column_comment=:inTColComm,");
            parameters.addValue("inTColComm", tColumnComment);
        }
        if(StringUtils.isNotEmpty(tJavaType)) {
            updateBuffer.append(" java_type=:inTJaTyp,");
            parameters.addValue("inTJaTyp", tJavaType);
        }
        if(StringUtils.isNotEmpty(tJavaField)) {
            updateBuffer.append(" java_field=:inTJaFie,");
            parameters.addValue("inTJaFie", tJavaField);
        }
        if(StringUtils.isNotEmpty(tIsInsert)) {
            updateBuffer.append(" is_insert=:inTIsInsert,");
            parameters.addValue("inTIsInsert", tIsInsert);
        }
        if(StringUtils.isNotEmpty(tIsEdit)) {
            updateBuffer.append(" is_edit=:inTIsEdit,");
            parameters.addValue("inTIsEdit", tIsEdit);
        }
        if(StringUtils.isNotEmpty(tIsList)) {
            updateBuffer.append(" is_list=:inTIsList,");
            parameters.addValue("inTIsList", tIsList);
        }
        if(StringUtils.isNotEmpty(tIsQuery)) {
            updateBuffer.append(" is_query=:inTIsQuery,");
            parameters.addValue("inTIsQuery", tIsQuery);
        }
        if(StringUtils.isNotEmpty(tIsRequired)) {
            updateBuffer.append(" is_required=:inTIsRequ,");
            parameters.addValue("inTIsRequ", tIsRequired);
        }
        if(StringUtils.isNotEmpty(tQueryType)) {
            updateBuffer.append(" query_type=:inTQueTyp,");
            parameters.addValue("inTQueTyp", tQueryType);
        }
        if(StringUtils.isNotEmpty(tHtmlType)) {
            updateBuffer.append(" html_type=:inTHtmlTyp,");
            parameters.addValue("inTHtmlTyp", tHtmlType);
        }
        if(StringUtils.isNotEmpty(tDictType)) {
            updateBuffer.append(" dict_type=:inTDictTyp,");
            parameters.addValue("inTDictTyp", tDictType);
        }
        if(StringUtils.isNotNull(tSort)) {
            updateBuffer.append(" sort=:inTSort,");
            parameters.addValue("inTSort", tSort);
        }

        updateBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE column_id=:inTColuId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inTColuId", tColumnId);

        int[] updatedResList = dbService.batchUpdate(updateBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updatedResList[0];
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int deleteGenTableColumns(List<GenTableColumn> genTableColumns) {
        String deleteSql = "DELETE FROM gen_table_column WHERE column_id=:inTColuId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[genTableColumns.size()];
        for (int i = 0; i < parametersList.length; i++) {
            GenTableColumn column = genTableColumns.get(i);
            Long tColumnId = column.getColumnId();

            parametersList[i] = new MapSqlParameterSource("inTColuId", tColumnId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public int deleteGenTableColumnByIds(Long[] tableIds) {
        String deleteSql = "DELETE FROM gen_table_column WHERE table_id=:inTaId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[tableIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long tableId = tableIds[i];

            parametersList[i] = new MapSqlParameterSource("inTaId", tableId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }
}
