package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysRoleRepository;

@Service
public class SysRoleRepositoryImpl implements SysRoleRepository{

    private DBService dbService;

    private String baseSelectSql = "SELECT DISTINCT r.role_id, r.role_name, r.role_key, r.role_sort, r.data_scope, r.menu_check_strictly, r.dept_check_strictly, r.status, r.del_flag, r.create_time, r.remark FROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id LEFT JOIN sys_user u ON u.user_id = ur.user_id LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE r.del_flag = '0'";

    private String selectCountSql = "SELECT DISTINCT COUNT(1) FROM sys_role r LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id LEFT JOIN sys_user u ON u.user_id = ur.user_id LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE r.del_flag = '0'";

    public SysRoleRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        StringBuilder addWhereBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(role, addWhereBuilder, parameters);

        List<SysRole> list = dbService.queryForList(addWhereBuilder.toString(), parameters, new SimplePropertyRowMapper<>(SysRole.class));

        return list;
    }

    @Override
    public TableDataInfo getPagedListResp(SysRole role) {
        StringBuilder addWhereBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(role, addWhereBuilder, parameters);

        // 默认使用主键排序
        parameters.addValue("inOrderBy", "role_id");

        String querListSql = baseSelectSql + addWhereBuilder.toString();

        List<SysRole> list = dbService.getPagedList(querListSql, parameters, new SimplePropertyRowMapper<>(SysRole.class));

        String queryCountSql = selectCountSql + addWhereBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedResult(list, queryCountSql, parameters);
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
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(r.create_time,'%Y%m%d') >= DATE_FORMAT(:inBeginTime,'%Y%m%d')");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(r.create_time,'%Y%m%d') <= DATE_FORMAT(:inEndTime,'%Y%m%d')");
            inParameters.addValue("inEndTime", endDateTime);
        }

        inBuilder.append(inRole.getDataScopeFilterParam());
    }

    @Override
    public List<SysRole> selectRolePermissionByUserId(Long userId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        String sql = baseSelectSql + " AND ur.user_id=:inUserId";

        List<SysRole> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return list;
    }

    @Override
    public List<SysRole> selectRoleAll() {

        String sql = baseSelectSql + " OR 1=1";

        List<SysRole> list = dbService.queryForList(sql, null, new SimplePropertyRowMapper<>(SysRole.class));
        return list;
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        String sql = "SELECT r.role_id FROM sys_role r LEFT JOIN sys_user_role ur on ur.role_id = r.role_id LEFT JOIN sys_user u on u.user_id = ur.user_id WHERE u.user_id=:inUserId";

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

        List<SysRole> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysRole.class));
        return list;
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

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE role_id=:inRoleId");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inRoleId", roleId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
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

        String insertSql = "INSERT INTO sys_role(role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, remark, create_by, create_time) VALUES(:inRoleName, :inRoleKey, :inRoleSort, :inRoleDataScope, :inRoleMenuCheck, :inRoleDeptCheck, :inRoleStatus, :inRoleRemark, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleName", roleName);
        parameters.addValue("inRoleKey", roleKey);
        parameters.addValue("inRoleSort", roleSort);
        parameters.addValue("inRoleDataScope", roleDataScope);
        parameters.addValue("inRoleMenuCheck", roleMenuCheckStrictly);
        parameters.addValue("inRoleDeptCheck", roleDeptCheckStrictly);
        parameters.addValue("inRoleStatus", roleStatus);
        parameters.addValue("inRoleRemark", roleRemark);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int deleteRoleById(Long roleId) {
        return this.deleteRoleByIds(new Long[]{roleId});
    }

    @Override
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
