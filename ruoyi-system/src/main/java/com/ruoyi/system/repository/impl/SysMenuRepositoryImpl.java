package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysMenuRepository;

@Service
public class SysMenuRepositoryImpl implements SysMenuRepository{

    private DBService dbService;

    private String baseSelectSql = "SELECT menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, IFNULL(perms,'') AS perms, icon, create_time FROM sys_menu WHERE 1=1";

    public SysMenuRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    protected List<SysMenu> queryList(final MapSqlParameterSource parameters, final String querySql) {
        List<SysMenu> list = dbService.queryForList(querySql, parameters, new SimplePropertyRowMapper<>(SysMenu.class));
        return list;
    }
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {

        StringBuilder sqlBuilder = new StringBuilder(baseSelectSql);
        NamedSqlParameterSource parameters = new NamedSqlParameterSource();

        setListSqlAndParams(menu, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    private void setListSqlAndParams(final SysMenu inMenu, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String menuName = inMenu.getMenuName();
        String menuVisible = inMenu.getVisible();
        String menuStatus = inMenu.getStatus();

        if(StringUtils.isNotEmpty(menuName)) {
            inBuilder.append(" AND menu_name LIKE CONCAT('%', :inMenuName, '%')");
            inParameters.addValue("inMenuName", menuName);
        }
        if(StringUtils.isNotEmpty(menuVisible)) {
            inBuilder.append(" AND visible=:inMenuVisible");
            inParameters.addValue("inMenuVisible", menuVisible);
        }
        if(StringUtils.isNotEmpty(menuStatus)) {
            inBuilder.append(" AND status=:inMenuStatus");
            inParameters.addValue("inMenuStatus", menuStatus);
        }

        inBuilder.append(" ORDER BY parent_id, order_num");
    }

    @Override
    public List<String> selectMenuPerms() {
        String sql = "SELECT DISTINCT m.perms FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id";

        List<String> list = dbService.queryForList(sql, null, String.class);
        return list;
    }

    @Override
    public List<SysMenu> selectMenuListByUserId(SysMenu menu) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT DISTINCT m.menu_id, m.parent_id, m.menu_name, m.path, m.component, m.`query`, m.route_name, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame, m.is_cache, m.menu_type, m.icon, m.order_num, m.create_time FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id LEFT JOIN sys_role ro ON ur.role_id = ro.role_id WHERE ur.user_id=:inUserId");
        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", menu.getParams().get("userId"));

        setListSqlAndParams(menu, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    @Override
    public List<String> selectMenuPermsByRoleId(Long roleId) {
        String sql = "SELECT DISTINCT m.perms FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id WHERE m.status = '0' AND rm.role_id=:inRoleId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleId", roleId);

        List<String> list = dbService.queryForList(sql, parameters, String.class);
        return list;
    }

    @Override
    public List<String> selectMenuPermsByUserId(Long userId) {
        String sql = "SELECT DISTINCT m.perms FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE m.status = '0' AND r.status = '0' AND ur.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        List<String> list = dbService.queryForList(sql, parameters, String.class);
        return list;
    }

    @Override
    public List<SysMenu> selectMenuTreeAll() {
        String sql = "SELECT DISTINCT m.menu_id, m.parent_id, m.menu_name, m.path, m.component, m.`query`, m.route_name, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame, m.is_cache, m.menu_type, m.icon, m.order_num, m.create_time FROM sys_menu m WHERE m.menu_type IN ('M', 'C') AND m.status = 0 ORDER BY m.parent_id, m.order_num";

        return queryList(null, sql);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        String sql = "SELECT DISTINCT m.menu_id, m.parent_id, m.menu_name, m.path, m.component, m.`query`, m.route_name, m.visible, m.status, IFNULL(m.perms,'') AS perms, m.is_frame, m.is_cache, m.menu_type, m.icon, m.order_num, m.create_time FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id LEFT JOIN sys_role ro ON ur.role_id = ro.role_id LEFT JOIN sys_user u ON ur.user_id = u.user_id WHERE u.user_id=:inUserId AND m.menu_type IN ('M', 'C') AND m.status = 0  AND ro.status = 0 ORDER BY m.parent_id, m.order_num";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        return queryList(parameters, sql);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId, boolean menuCheckStrictly) {
        String sql = "SELECT m.menu_id FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id WHERE rm.role_id=:inRoleId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleId", roleId);

        if(menuCheckStrictly) {
            sql = sql + " AND m.menu_id NOT IN (SELECT m.parent_id FROM sys_menu m INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id AND rm.role_id=:inRoleId)";
        }

        sql = sql + " ORDER BY m.parent_id, m.order_num";

        List<Long> list = dbService.queryForList(sql, parameters, Long.class);
        return list;
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        String sql = baseSelectSql + " AND menu_id=:inMenuId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuId", menuId);

        SysMenu queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysMenu.class));
        return queryObj;
    }

    @Override
    public int hasChildByMenuId(Long menuId) {
        String sql = "SELECT COUNT(1) FROM sys_menu WHERE parent_id=:inMenuId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuId", menuId);

        Integer queryObj = dbService.queryForObject(sql, parameters, Integer.class);
        return queryObj.intValue();
    }

    @Override
    public int insertMenu(SysMenu menu) {
        Long parentId = menu.getParentId();
        String menuName = menu.getMenuName();
        Integer menuOrder = menu.getOrderNum();
        String menuPath = menu.getPath();
        String menuComp = menu.getComponent();
        String menuQuery = menu.getQuery();
        String menuRoute = menu.getRouteName();
        String menuFrame = menu.getIsFrame();
        String menuCache = menu.getIsCache();
        String menuType = menu.getMenuType();
        String menuVisible = menu.getVisible();
        String menuStatus = menu.getStatus();
        String menuPerms = menu.getPerms();
        String menuIcon = menu.getIcon();
        String menuRemark = menu.getRemark();
        String createBy = menu.getCreateBy();

        String insertSql = "INSERT INTO sys_menu(parent_id, menu_name, order_num, path, component, `query`, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, remark, create_by, create_time) VALUES(:inParentId, :inMenuName, :inMenuOrder, :inMenuPath, :inMenuComp, :inMenuQuery, :inMenuRoute, :inMenuFrame, :inMenuCache, :inMenuType, :inMenuVisi, :inMenuStatus, :inMenuPerms, :inMenuIcon, :inMenuRemark, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inParentId", parentId);
        parameters.addValue("inMenuName", menuName);
        parameters.addValue("inMenuOrder", menuOrder);
        parameters.addValue("inMenuPath", menuPath);
        parameters.addValue("inMenuComp", menuComp);
        parameters.addValue("inMenuQuery", menuQuery);
        parameters.addValue("inMenuRoute", menuRoute);
        parameters.addValue("inMenuFrame", menuFrame);
        parameters.addValue("inMenuCache", menuCache);
        parameters.addValue("inMenuType", menuType);
        parameters.addValue("inMenuVisi", menuVisible);
        parameters.addValue("inMenuStatus", menuStatus);
        parameters.addValue("inMenuPerms", menuPerms);
        parameters.addValue("inMenuIcon", menuIcon);
        parameters.addValue("inMenuRemark", menuRemark);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insertSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];
    }

    @Override
    public int updateMenu(SysMenu menu) {
        Long menuId = menu.getMenuId();
        String menuName = menu.getMenuName();
        Long parentId = menu.getParentId();
        Integer menuOrder = menu.getOrderNum();
        String menuPath = menu.getPath();
        String menuComp = menu.getComponent();
        String menuQuery = menu.getQuery();
        String menuRoute = menu.getRouteName();
        String menuFrame = menu.getIsFrame();
        String menuCache = menu.getIsCache();
        String menuType = menu.getMenuType();
        String menuVisible = menu.getVisible();
        String menuStatus = menu.getStatus();
        String menuPerms = menu.getPerms();
        String menuIcon = menu.getIcon();
        String menuRemark = menu.getRemark();
        String updateBy = menu.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_menu SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotEmpty(menuName)) {
            updateSqlBuffer.append(" menu_name=:inMenuName,");
            parameters.addValue("inMenuName", menuName);
        }
        if(StringUtils.isNotNull(parentId)) {
            updateSqlBuffer.append(" parent_id=:inParentId,");
            parameters.addValue("inParentId", parentId);
        }
        if(StringUtils.isNotNull(menuOrder)) {
            updateSqlBuffer.append(" order_num=:inMenuOrder,");
            parameters.addValue("inMenuOrder", menuOrder);
        }
        if(StringUtils.isNotEmpty(menuPath)) {
            updateSqlBuffer.append(" path=:inMenuPath,");
            parameters.addValue("inMenuPath", menuPath);
        }
        if(StringUtils.isNotEmpty(menuComp)) {
            updateSqlBuffer.append(" component=:inMenuComp,");
            parameters.addValue("inMenuComp", menuComp);
        }
        if(StringUtils.isNotEmpty(menuQuery)) {
            updateSqlBuffer.append(" `query`=:inMenuQuery,");
            parameters.addValue("inMenuQuery", menuQuery);
        }
        if(StringUtils.isNotEmpty(menuRoute)) {
            updateSqlBuffer.append(" route_name=:inMenuRoute,");
            parameters.addValue("inMenuRoute", menuRoute);
        }
        if(StringUtils.isNotEmpty(menuFrame)) {
            updateSqlBuffer.append(" is_frame=:inMenuFrame,");
            parameters.addValue("inMenuFrame", menuFrame);
        }
        if(StringUtils.isNotEmpty(menuCache)) {
            updateSqlBuffer.append(" is_cache=:inMenuCache,");
            parameters.addValue("inMenuCache", menuCache);
        }
        if(StringUtils.isNotEmpty(menuType)) {
            updateSqlBuffer.append(" menu_type=:inMenuType,");
            parameters.addValue("inMenuType", menuType);
        }
        if(StringUtils.isNotEmpty(menuVisible)) {
            updateSqlBuffer.append(" visible=:inMenuVisi,");
            parameters.addValue("inMenuVisi", menuVisible);
        }
        if(StringUtils.isNotEmpty(menuStatus)) {
            updateSqlBuffer.append(" status=:inMenuStatus,");
            parameters.addValue("inMenuStatus", menuStatus);
        }
        if(StringUtils.isNotEmpty(menuPerms)) {
            updateSqlBuffer.append(" perms=:inMenuPerms,");
            parameters.addValue("inMenuPerms", menuPerms);
        }
        if(StringUtils.isNotEmpty(menuIcon)) {
            updateSqlBuffer.append(" icon=:inMenuIcon,");
            parameters.addValue("inMenuIcon", menuIcon);
        }
        if(StringUtils.isNotEmpty(menuRemark)) {
            updateSqlBuffer.append(" remark=:inMenuRemark,");
            parameters.addValue("inMenuRemark", menuRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE menu_id=:inMenuId");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inMenuId", menuId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public void updateMenuSort(SysMenu menu) {
        String updateSql = "UPDATE sys_menu SET order_num=:inMenuOrder WHERE menu_id=:inMenuId";

        Long menuId = menu.getMenuId();
        Integer menuOrder = menu.getOrderNum();

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuId", menuId);
        parameters.addValue("inMenuOrder", menuOrder);

        dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
    }

    @Override
    public int deleteMenuById(Long menuId) {
        String deleteSql = "DELETE FROM sys_menu WHERE menu_id=:inMenuId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuId", menuId);

        int[] deleteResList = dbService.batchUpdate(deleteSql, new MapSqlParameterSource[]{parameters});;
        return deleteResList[0];
    }

    @Override
    public SysMenu checkMenuNameUnique(String menuName, Long parentId) {
        String sql = baseSelectSql + " AND menu_name=:inMenuName AND parent_id=inParentId LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inParentId", parentId);
        parameters.addValue("inMenuName", menuName);

        SysMenu queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysMenu.class));
        return queryObj;
    }

    @Override
    public List<SysMenu> selectMenusByPathOrRouteName(String path, String routeName) {
        String sql = baseSelectSql + " AND menu_type IN ('M', 'C') AND (path=:inMenuPath OR path=:inMenuRoute OR route_name=:inMenuPath OR route_name=:inMenuRoute)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inMenuPath", path);
        parameters.addValue("inMenuRoute", routeName);

        return queryList(parameters, sql);
    }

}
