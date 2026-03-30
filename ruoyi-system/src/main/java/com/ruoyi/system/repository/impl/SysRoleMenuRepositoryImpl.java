package com.ruoyi.system.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.system.domain.SysRoleMenu;
import com.ruoyi.system.repository.SysRoleMenuRepository;

@Repository
public class SysRoleMenuRepositoryImpl implements SysRoleMenuRepository {

    private DBService dbService;

    public SysRoleMenuRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public int checkMenuExistRole(Long menuId) {
        String sql = "SELECT COUNT(1) FROM sys_role_menu WHERE menu_id=:inMenuId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuId", menuId);

        Integer roleCount = dbService.queryForObject(sql, parameters, Integer.class);
        return roleCount.intValue();
    }

    @Override
    public int deleteRoleMenuByRoleId(Long roleId) {
        return this.deleteRoleMenu(new Long[]{roleId});
    }

    @Override
    public int deleteRoleMenu(Long[] roleIds) {
        String deleteSql = "DELETE FROM sys_role_menu WHERE role_id=:inRoleId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[roleIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long roleId = roleIds[i];

            parametersList[i] = new MapSqlParameterSource("inRoleId", roleId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList) {
        String insertSql = "INSERT INTO sys_role_menu(role_id, menu_id) VALUES(:inRoleId, :inMenuID)";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[roleMenuList.size()];
        for (int i = 0; i < parametersList.length; i++) {
            SysRoleMenu roleMenu = roleMenuList.get(i);
            Long roleId = roleMenu.getRoleId();
            Long menuId = roleMenu.getMenuId();

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("inRoleId", roleId);
            parameters.addValue("inMenuID", menuId);

            parametersList[i] = parameters;
        }

        int[] insertResList = dbService.batchUpdate(insertSql, parametersList);
        return insertResList[0];
    }

}
