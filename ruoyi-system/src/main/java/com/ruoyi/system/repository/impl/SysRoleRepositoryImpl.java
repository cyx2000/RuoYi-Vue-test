package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysRoleRepository;

@Repository
public class SysRoleRepositoryImpl implements SysRoleRepository{

    private DBService dbService;

    private String baseSelectSql = "SELECT DISTINCT r.role_id, r.role_name, r.role_key, r.role_sort, r.data_scope, r.menu_check_strictly, r.dept_check_strictly, r.status, r.del_flag, r.create_time, r.remark FROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id LEFT JOIN sys_user u ON u.user_id = ur.user_id LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE r.del_flag = '0'";

    public SysRoleRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysRole> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysRole> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return list;
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(role, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public TableDataInfo getPagedListResp(SysRole role) {
        StringBuilder sqlBuilder = new StringBuilder();
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(role, sqlBuilder, parameters);

        String selectCountSql = "SELECT DISTINCT COUNT(1) FROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id LEFT JOIN sys_user u ON u.user_id = ur.user_id LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE r.del_flag = '0'";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        // 默认使用role_sort排序
        parameters.setDefaultOrderByStr("role_sort ASC");

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        String querListSql = baseSelectSql + sqlBuilder.toString();

        pagedResp.setRows(queryList(parameters, querListSql));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysRole inRole, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String roleName = inRole.getRoleName();
        String roleStatus = inRole.getStatus();
        String roleKey = inRole.getRoleKey();
        String beginDateTime = inRole.getBeginTimeParam();
        String endDateTime = inRole.getEndTimeParam();

        if(StringUtils.isNotEmpty(roleName)) {
            inBuilder.append(" AND r.role_name LIKE CONCAT('%', :inRoleName, '%')");
            inParameters.addValue("inRoleName", roleName);
        }
        if(StringUtils.isNotEmpty(roleStatus)) {
            inBuilder.append(" AND r.status=:inRoleStatus");
            inParameters.addValue("inRoleStatus", roleStatus);
        }
        if(StringUtils.isNotEmpty(roleKey)) {
            inBuilder.append(" AND r.role_key LIKE CONCAT('%', :inRoleKey, '%')");
            inParameters.addValue("inRoleKey", roleKey);
        }
        if(StringUtils.isNotEmpty(beginDateTime) && StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND r.create_time BETWEEN :inBeginTime AND :inEndTime");
            inParameters.addValue("inBeginTime", beginDateTime);
            inParameters.addValue("inEndTime", endDateTime);
        }

        inBuilder.append(inRole.getDataScopeFilterParam());
    }

    @Override
    public List<SysRole> selectRolePermissionByUserId(Long userId) {
        String sql = baseSelectSql + " AND ur.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        return queryList(parameters, sql);
    }

    @Override
    public List<SysRole> selectRoleAll() {
        String sql = baseSelectSql + " OR 1=1";

        return queryList(null, sql);
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        String sql = "SELECT r.role_id FROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id LEFT JOIN sys_user u ON u.user_id = ur.user_id WHERE u.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        List<Long> list = dbService.queryForList(sql, parameters, Long.class);
        return list;
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        String sql = baseSelectSql + " AND r.role_id=:inRoleId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleId", roleId);

        SysRole queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return queryObj;
    }

    @Override
    public List<SysRole> selectRolesByUserName(String userName) {
        String sql = baseSelectSql + " AND u.user_name=:inUserName";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserName", userName);

        return queryList(parameters, sql);
    }

    @Override
    public SysRole checkRoleNameUnique(String roleName) {
        String sql = baseSelectSql + " AND r.role_name=:inRoleName LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleName", roleName);

        SysRole queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return queryObj;
    }

    @Override
    public SysRole checkRoleKeyUnique(String roleKey) {
        String sql = baseSelectSql + " AND r.role_key=:inRoleKey LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleKey", roleKey);

        SysRole queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return queryObj;
    }

    @Override
    @Transactional
    public int updateRole(SysRole role) {
        Long roleId = role.getRoleId();
        String roleName = role.getRoleName();
        String roleKey = role.getRoleKey();
        Integer roleSort = role.getRoleSort();
        String roleDataScope = role.getDataScope();
        Boolean roleMenuCheckStrictly = role.isMenuCheckStrictly();
        Boolean roleDeptCheckStrictly = role.isDeptCheckStrictly();
        String roleStatus = role.getStatus();
        String roleRemark = role.getRemark();
        String updateBy = role.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_role SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(roleName)) {
            updateSqlBuffer.append(" role_name=:inRoleName,");
            parameters.addValue("inRoleName", roleName);
        }
        if(StringUtils.isNotEmpty(roleKey)) {
            updateSqlBuffer.append(" role_key=:inRoleKey,");
            parameters.addValue("inRoleKey", roleKey);
        }
        if(StringUtils.isNotNull(roleSort)) {
            updateSqlBuffer.append(" role_sort=:inRoleSort,");
            parameters.addValue("inRoleSort", roleSort);
        }
        if(StringUtils.isNotEmpty(roleDataScope)) {
            updateSqlBuffer.append(" data_scope=:inRoleDataScope,");
            parameters.addValue("inRoleDataScope", roleDataScope);
        }
        if(StringUtils.isNotNull(roleMenuCheckStrictly)) {
            updateSqlBuffer.append(" menu_check_strictly=:inRoleMenuCheck,");
            parameters.addValue("inRoleMenuCheck", roleMenuCheckStrictly);
        }
        if(StringUtils.isNotNull(roleDeptCheckStrictly)) {
            updateSqlBuffer.append(" dept_check_strictly=:inRoleDeptCheck,");
            parameters.addValue("inRoleDeptCheck", roleDeptCheckStrictly);
        }
        if(StringUtils.isNotEmpty(roleStatus)) {
            updateSqlBuffer.append(" status=:inRoleStatus,");
            parameters.addValue("inRoleStatus", roleStatus);
        }
        if(StringUtils.isNotEmpty(roleRemark)) {
            updateSqlBuffer.append(" remark=:inRoleRemark,");
            parameters.addValue("inRoleRemark", roleRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE role_id=:inRoleId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inRoleId", roleId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    @Transactional
    public int insertRole(SysRole role) {
        String roleName = role.getRoleName();
        String roleKey = role.getRoleKey();
        Integer roleSort = role.getRoleSort();
        String roleDataScope = role.getDataScope();
        Boolean roleMenuCheckStrictly = role.isMenuCheckStrictly();
        Boolean roleDeptCheckStrictly = role.isDeptCheckStrictly();
        String roleStatus = role.getStatus();
        String roleRemark = role.getRemark();
        String createBy = role.getCreateBy();

        String insertSql = "INSERT INTO sys_role(role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, remark, create_by, create_time) VALUES(:inRoleName, :inRoleKey, :inRoleSort, :inRoleDataScope, :inRoleMenuCheck, :inRoleDeptCheck, :inRoleStatus, :inRoleRemark, :inCreateBy, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inRoleName", roleName);
        parameters.addValue("inRoleKey", roleKey);
        parameters.addValue("inRoleSort", roleSort);
        parameters.addValue("inRoleDataScope", roleDataScope);
        parameters.addValue("inRoleMenuCheck", roleMenuCheckStrictly);
        parameters.addValue("inRoleDeptCheck", roleDeptCheckStrictly);
        parameters.addValue("inRoleStatus", roleStatus);
        parameters.addValue("inRoleRemark", roleRemark);
        parameters.addValue("inCreateBy", createBy);

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    @Transactional
    public int deleteRoleById(Long roleId) {
        return this.deleteRoleByIds(new Long[]{roleId});
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        String deleteSql = "UPDATE sys_role SET del_flag = '2' WHERE role_id=:inRoleId";

        MapSqlParameterSource[] paramsList = new MapSqlParameterSource[roleIds.length];
        for (int i = 0; i < paramsList.length; i++) {
            Long roleId = roleIds[i];

            paramsList[i] = new MapSqlParameterSource("inRoleId", roleId);
        }

        int[] deletedResList = dbService.batchUpdate(deleteSql, paramsList);
        return deletedResList[0];
    }

}
