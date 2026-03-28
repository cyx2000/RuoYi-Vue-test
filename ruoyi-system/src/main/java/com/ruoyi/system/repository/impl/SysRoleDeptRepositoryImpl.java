package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.system.domain.SysRoleDept;
import com.ruoyi.system.repository.SysRoleDeptRepository;

@Service
public class SysRoleDeptRepositoryImpl implements SysRoleDeptRepository {

    private DBService dbService;

    public SysRoleDeptRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public int deleteRoleDeptByRoleId(Long roleId) {
        return this.deleteRoleDept(new Long[]{roleId});
    }

    @Override
    public int deleteRoleDept(Long[] roleIds) {
        String deleteSql = "DELETE FROM sys_role_dept WHERE role_id=:inRoleId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[roleIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long roleId = roleIds[i];

            parametersList[i] = new MapSqlParameterSource("inRoleId", roleId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public int selectCountRoleDeptByDeptId(Long deptId) {
        String sql = "SELECT COUNT(1) FROM sys_role_dept WHERE dept_id=:inDeptId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        Integer roleCount = dbService.queryForObject(sql, parameters, Integer.class);
        return roleCount.intValue();
    }

    @Override
    public int batchRoleDept(List<SysRoleDept> roleDeptList) {
        String insertSql = "INSERT INTO sys_role_dept(role_id, dept_id) VALUES(:inRoleId, :inDeptID)";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[roleDeptList.size()];
        for (int i = 0; i < parametersList.length; i++) {
            SysRoleDept roleDept = roleDeptList.get(i);
            Long roleId = roleDept.getRoleId();
            Long deptId = roleDept.getDeptId();

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("inRoleId", roleId);
            params.addValue("inDeptID", deptId);

            parametersList[i] = params;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList[0];
    }

}
