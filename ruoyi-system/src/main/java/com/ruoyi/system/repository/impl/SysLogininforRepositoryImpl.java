package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.repository.SysLogininforRepository;

@Repository
public class SysLogininforRepositoryImpl implements SysLogininforRepository {

    private DBService dbService;

    public SysLogininforRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        String username = logininfor.getUserName();
        String loginStatus = logininfor.getStatus();
        String loginIp = logininfor.getIpaddr();
        String loginLocation = logininfor.getLoginLocation();
        String loginBrowser = logininfor.getBrowser();
        String loginOS = logininfor.getOs();
        String loginMsg = logininfor.getMsg();

        String insertSql = "INSERT INTO sys_logininfor(user_name, status, ipaddr, login_location, browser, os, msg, login_time) VALUES(:inUsername, :inLoginStatus, :inLoginIp, :inLoginLocation, :inLoginBrowser, :inLoginOS, :inLoginMsg, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUsername", username);
        parameters.addValue("inLoginStatus", loginStatus);
        parameters.addValue("inLoginIp", loginIp);
        parameters.addValue("inLoginLocation", loginLocation);
        parameters.addValue("inLoginBrowser", loginBrowser);
        parameters.addValue("inLoginOS", loginOS);
        parameters.addValue("inLoginMsg", loginMsg);

        dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
    }

    protected List<SysLogininfor> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysLogininfor> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysLogininfor.class));
        return list;
    }

    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        String sql = "SELECT info_id, user_name, ipaddr, login_location, browser, os, status, msg, login_time FROM sys_logininfor WHERE 1=1";

        StringBuilder sqlBuilder = new StringBuilder(sql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(logininfor, sqlBuilder, parameters);

        sqlBuilder.append(" ORDER BY info_id DESC");

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysLogininfor logininfor) {
        String sql = "SELECT info_id, user_name, ipaddr, login_location, browser, os, status, msg, login_time FROM sys_logininfor WHERE 1=1";

        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(logininfor, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_logininfor WHERE 1=1";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        // 默认使用info_id排序
        parameters.setDefaultOrderByStr("info_id DESC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String queryListSql = sql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, queryListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysLogininfor inLogininfor, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String loginIp = inLogininfor.getIpaddr();
        String loginStatus = inLogininfor.getStatus();
        String username = inLogininfor.getUserName();
        String beginDateTime = inLogininfor.getBeginTimeParam();
        String endDateTime = inLogininfor.getEndTimeParam();

        if(StringUtils.isNotEmpty(loginIp)) {
            inBuilder.append(" AND ipaddr LIKE CONCAT('%', :inLoginIp, '%')");
            inParameters.addValue("inLoginIp", loginIp);
        }
        if(StringUtils.isNotEmpty(loginStatus)) {
            inBuilder.append(" AND status=:inLoginStatus");
            inParameters.addValue("inLoginStatus", loginStatus);
        }
        if(StringUtils.isNotEmpty(username)) {
            inBuilder.append(" AND user_name LIKE CONCAT('%', :inUsername, '%')");
            inParameters.addValue("inUsername", username);
        }
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND login_time >= :inBeginTime");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND login_time <= :inEndTime");
            inParameters.addValue("inEndTime", endDateTime);
        }
    }

    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        String deleteSql = "DELETE FROM sys_logininfor WHERE info_id=:inInforId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[infoIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long inforId = infoIds[i];

            parametersList[i] = new MapSqlParameterSource("inInforId", inforId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public int cleanLogininfor() {
        String clearSql = "TRUNCATE TABLE sys_logininfor";

        int clearResList = dbService.execute(clearSql);
        return clearResList;
    }
}
