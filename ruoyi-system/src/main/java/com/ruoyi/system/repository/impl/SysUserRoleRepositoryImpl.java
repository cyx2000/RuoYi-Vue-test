package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.repository.SysUserRoleRepository;

@Repository
public class SysUserRoleRepositoryImpl implements SysUserRoleRepository {

    private DBService dbService;

    public SysUserRoleRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public int deleteUserRoleByUserId(Long userId) {
        return this.deleteUserRole(new Long[]{userId});
    }

    @Override
    public int deleteUserRole(Long[] userIds) {
        String deleteSql = "DELETE FROM sys_user_role WHERE user_id=:inUserId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long userId = userIds[i];

            parametersList[i] = new MapSqlParameterSource("inUserId", userId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        String sql = "SELECT COUNT(1) FROM sys_user_role WHERE role_id=:inRoleId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleId", roleId);

        Integer roleCount = dbService.queryForObject(sql, parameters, Integer.class);
        return roleCount.intValue();
    }

    @Override
    public int batchUserRole(List<SysUserRole> userRoleList) {
        String insertSql = "INSERT INTO sys_user_role(role_id, user_id) VALUES(:inRoleId, :inUserID)";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userRoleList.size()];
        for (int i = 0; i < parametersList.length; i++) {
            SysUserRole userRole = userRoleList.get(i);
            Long roleId = userRole.getRoleId();
            Long userId = userRole.getUserId();

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inRoleId", roleId);
            parameters.addValue("inUserID", userId);

            parametersList[i] = parameters;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);

        return insertResList[0];
    }

    @Override
    public int deleteUserRoleInfo(SysUserRole userRole) {
        return this.deleteUserRoleInfos(userRole.getRoleId(), new Long[]{userRole.getUserId()});
    }

    @Override
    public int deleteUserRoleInfos(Long roleId, Long[] userIds) {
        String deleteSql = "DELETE FROM sys_user_role WHERE user_id=:inUserID AND role_id=:inRoleId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long userId = userIds[i];

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inRoleId", roleId);
            parameters.addValue("inUserID", userId);

            parametersList[i] = parameters;
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

}
