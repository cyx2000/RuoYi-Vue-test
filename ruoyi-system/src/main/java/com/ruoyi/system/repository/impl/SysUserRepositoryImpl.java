package com.ruoyi.system.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysUserRepository;

@Service
public class SysUserRepositoryImpl implements SysUserRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT u.user_id, u.dept_id, u.user_name, u.nick_name, u.email, u.avatar, u.phonenumber, u.password, u.sex, u.status, u.del_flag, u.login_ip, u.login_date, u.pwd_update_date, u.create_by, u.create_time, u.remark,  d.dept_id, d.parent_id, d.ancestors, d.dept_name, d.order_num, d.leader, d.status AS dept_status, r.role_id, r.role_name, r.role_key, r.role_sort, r.data_scope, r.status AS role_status FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE u.del_flag = '0'";

    public SysUserRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public List<SysUser> selectUserList(SysUser sysUser) {
        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(sysUser, sqlBuilder, parameters);

        return queryList(parameters, sqlBuilder.toString());
    }

    protected List<SysUser> queryList(final MapSqlParameterSource parameters, final String querySql) {
        String sql = "SELECT u.user_id, u.dept_id, u.nick_name, u.user_name, u.email, u.avatar, u.phonenumber, u.sex, u.status, u.del_flag, u.login_ip, u.login_date, u.create_by, u.create_time, u.remark, d.dept_name, d.leader FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE u.del_flag = '0'";

        String queryListSql = sql + querySql;

        SqlRowSet sqlRs = dbService.getNamedJdbc().queryForRowSet(queryListSql, parameters);

        List<SysUser> list = new ArrayList<>();

        while (sqlRs.next()) {
            SysUser user = new SysUser();
            setUserParams(user, sqlRs);

            String deptName = sqlRs.getString("dept_name");
            String deptLeader = sqlRs.getString("leader");

            SysDept dept = new SysDept();
            dept.setDeptName(deptName);
            dept.setLeader(deptLeader);
            user.setDept(dept);

            list.add(user);
        }
        return list;
    }

    @Override
    public TableDataInfo getPagedListResp(SysUser sysUser) {

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        setListSqlAndParams(sysUser, sqlBuilder, parameters);

        String selectCountSql = "SELECT COUNT(1) FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE u.del_flag = '0'";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        dbService.buildPagedSqlAndSetParameters(sqlBuilder, parameters);

        pagedResp.setRows(queryList(parameters, sqlBuilder.toString()));
        return pagedResp;
    }

    private void setListSqlAndParams(final SysUser inUser, StringBuilder inBuilder, MapSqlParameterSource inParameters) {
        String username = inUser.getUserName();
        String userStatus = inUser.getStatus();
        String userPhone = inUser.getPhonenumber();
        String beginDateTime = inUser.getBeginTimeParam();
        String endDateTime = inUser.getEndTimeParam();
        Long deptId = inUser.getDeptId();

        if(StringUtils.isNotEmpty(username)) {
            inBuilder.append(" AND u.user_name LIKE CONCAT('%', :inUsername, '%')");
            inParameters.addValue("inUsername", username);
        }
        if(StringUtils.isNotEmpty(userStatus)) {
            inBuilder.append(" AND u.status=:inUserStatus");
            inParameters.addValue("inUserStatus", userStatus);
        }
        if(StringUtils.isNotEmpty(userPhone)) {
            inBuilder.append(" AND u.phonenumber LIKE CONCAT('%', :inUserPhone, '%')");
            inParameters.addValue("inUserPhone", userPhone);
        }
        if(StringUtils.isNotEmpty(beginDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(u.create_time,'%Y%m%d') >= DATE_FORMAT(:inBeginTime,'%Y%m%d')");
            inParameters.addValue("inBeginTime", beginDateTime);
        }
        if(StringUtils.isNotEmpty(endDateTime)) {
            inBuilder.append(" AND DATE_FORMAT(u.create_time,'%Y%m%d') <= DATE_FORMAT(:inEndTime,'%Y%m%d')");
            inParameters.addValue("inEndTime", endDateTime);
        }
        if(StringUtils.isNotNull(deptId) && deptId != 0) {
            inBuilder.append(" AND (u.dept_id=:inDeptId OR u.dept_id IN ( SELECT t.dept_id FROM sys_dept t WHERE FIND_IN_SET(:inDeptId, ancestors) ))");
            inParameters.addValue("inDeptId", deptId);
        }

        inBuilder.append(inUser.getDataScopeFilterParam());
    }

    @Override
    public TableDataInfo selectAllocatedList(SysUser user) {
        Long roleId = user.getRoleId();
        String username = user.getUserName();
        String userPhone = user.getPhonenumber();

        String sql = "SELECT DISTINCT u.user_id, u.dept_id, u.user_name, u.nick_name, u.email, u.phonenumber, u.status, u.create_time FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE u.del_flag = '0' AND r.role_id=:inRoleId";

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inRoleId", roleId);

        if(StringUtils.isNotEmpty(username)) {
            sqlBuilder.append(" AND u.user_name LIKE CONCAT('%', :inUsername, '%')");
            parameters.addValue("inUsername", username);
        }
        if(StringUtils.isNotEmpty(userPhone)) {
            sqlBuilder.append(" AND u.phonenumber LIKE CONCAT('%', :inUserPhone, '%')");
            parameters.addValue("inUserPhone", userPhone);
        }

        sqlBuilder.append(user.getDataScopeFilterParam());

        String selectCountSql = "SELECT DISTINCT COUNT(1) FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE u.del_flag = '0' AND r.role_id=:inRoleId";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        sql = sql + sqlBuilder.toString();

        List<SysUser> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysUser.class));

        pagedResp.setRows(list);
        return pagedResp;
    }

    @Override
    public TableDataInfo selectUnallocatedList(SysUser user) {
        Long roleId = user.getRoleId();
        String username = user.getUserName();
        String userPhone = user.getPhonenumber();

        String sql = "SELECT DISTINCT u.user_id, u.dept_id, u.user_name, u.nick_name, u.email, u.phonenumber, u.status, u.create_time FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE u.del_flag = '0' AND (r.role_id!=:inRoleId OR r.role_id IS NULL) nd u.user_id NOT IN (SELECT u.user_id FROM sys_user u INNER JOIN sys_user_role ur ON u.user_id = ur.user_id AND r.role_id=:inRoleId";

        StringBuilder sqlBuilder = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inRoleId", roleId);

        if(StringUtils.isNotEmpty(username)) {
            sqlBuilder.append(" AND u.user_name LIKE CONCAT('%', :inUsername, '%')");
            parameters.addValue("inUsername", username);
        }
        if(StringUtils.isNotEmpty(userPhone)) {
            sqlBuilder.append(" AND u.phonenumber LIKE CONCAT('%', :inUserPhone, '%')");
            parameters.addValue("inUserPhone", userPhone);
        }

        sqlBuilder.append(user.getDataScopeFilterParam());

        String selectCountSql = "SELECT DISTINCT COUNT(1) FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN sys_role r ON r.role_id = ur.role_id WHERE u.del_flag = '0' AND (r.role_id!=:inRoleId OR r.role_id IS NULL) nd u.user_id NOT IN (SELECT u.user_id FROM sys_user u INNER JOIN sys_user_role ur ON u.user_id = ur.user_id AND r.role_id=:inRoleId";
        String queryCountSql = selectCountSql + sqlBuilder.toString();

        TableDataInfo pagedResp = dbService.getPagedRespInfo(queryCountSql, parameters);

        sql = sql + sqlBuilder.toString();

        List<SysUser> list = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysUser.class));

        pagedResp.setRows(list);
        return pagedResp;
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        String sql = baseSelectSql + " AND u.user_name=:inUsername";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUsername", userName);

        SysUser queryObj = querySingleUser(sql, parameters);
        return queryObj;
    }

    @Override
    public SysUser selectUserById(Long userId) {
        String sql = baseSelectSql + " AND u.user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserId", userId);

        SysUser queryObj = querySingleUser(sql, parameters);
        return queryObj;
    }

    protected void setUserParams(SysUser inUser, SqlRowSet inRs) {
        Long userId = inRs.getLong("user_id");
        Long deptId = inRs.getLong("dept_id");
        String username = inRs.getString("user_name");
        String userNickname = inRs.getString("nick_name");
        String userEmail = inRs.getString("email");
        String userPhone = inRs.getString("phonenumber");
        String userSex = inRs.getString("sex");
        String userAvatar = inRs.getString("avatar");
        String userStatus = inRs.getString("status");
        String userDelFlag = inRs.getString("del_flag");
        String userLoginIp = inRs.getString("login_ip");
        Date userLoginDate = inRs.getDate("login_date");
        String userRemark = inRs.getString("remark");
        String createBy = inRs.getString("create_by");
        Date createTime = inRs.getDate("create_time");

        inUser.setUserId(userId);
        inUser.setDeptId(deptId);
        inUser.setUserName(username);
        inUser.setNickName(userNickname);
        inUser.setEmail(userEmail);
        inUser.setPhonenumber(userPhone);
        inUser.setSex(userSex);
        inUser.setAvatar(userAvatar);
        inUser.setStatus(userStatus);
        inUser.setDelFlag(userDelFlag);
        inUser.setLoginIp(userLoginIp);
        inUser.setLoginDate(userLoginDate);
        inUser.setRemark(userRemark);
        inUser.setCreateBy(createBy);
        inUser.setCreateTime(createTime);
    }

    protected SysUser querySingleUser(String inSql, MapSqlParameterSource parameters) {
        SqlRowSet sqlRs = dbService.getNamedJdbc().queryForRowSet(inSql, parameters);

        SysUser user = new SysUser();
        user.setRoles(new ArrayList<>());
        while (sqlRs.next()) {
            if(StringUtils.isNull(user.getUserId())) {
                String userPwd = sqlRs.getString("password");
                Date userPwdUpdateDate = sqlRs.getDate("pwd_update_date");
                user.setPassword(userPwd);
                user.setPwdUpdateDate(userPwdUpdateDate);
                setUserParams(user, sqlRs);

                Long deptId = sqlRs.getLong("dept_id");
                Long parentId = sqlRs.getLong("parent_id");
                String deptName = sqlRs.getString("dept_name");
                String deptAncestors = sqlRs.getString("ancestors");
                int deptOrderNum = sqlRs.getInt("order_num");
                String deptLeader = sqlRs.getString("leader");
                String deptStatus = sqlRs.getString("dept_status");

                SysDept userDept = new SysDept();
                userDept.setDeptId(deptId);
                userDept.setParentId(parentId);
                userDept.setDeptName(deptName);
                userDept.setAncestors(deptAncestors);
                userDept.setOrderNum(deptOrderNum);
                userDept.setLeader(deptLeader);
                userDept.setStatus(deptStatus);
                user.setDept(userDept);
            }
            Long roleId = sqlRs.getLong("role_id");
            String roleName = sqlRs.getString("role_name");
            String roleKey = sqlRs.getString("role_key");
            int roleSort = sqlRs.getInt("role_sort");
            String roleDataScope = sqlRs.getString("data_scope");
            String roleStatus = sqlRs.getString("role_status");

            SysRole userRole = new SysRole();
            userRole.setRoleId(roleId);
            userRole.setRoleName(roleName);
            userRole.setRoleKey(roleKey);
            userRole.setRoleSort(roleSort);
            userRole.setDataScope(roleDataScope);
            userRole.setStatus(roleStatus);
            user.getRoles().add(userRole);
        }
        return user;
    }

    @Override
    public int insertUser(SysUser user) {
        Long deptId = user.getDeptId();
        String username = user.getUserName();
        String userNickName = user.getNickName();
        String userEmail = user.getEmail();
        String userAvatar = user.getAvatar();
        String userPhone = user.getPhonenumber();
        String userSex =  user.getSex();
        String userPwd = user.getPassword();
        String userStatus = user.getStatus();
        Date userPwdUpdateDate = user.getPwdUpdateDate();
        String userRemark = user.getRemark();
        String createBy = user.getCreateBy();

        String insertSql = "INSERT INTO sys_user(dept_id, user_name, nick_name, email, avatar, phonenumber, sex, password, status, pwd_update_date, remark, create_by, create_time) VALUES(:inDeptId, :inUsername, :inUserNick, :inUserEmail, :inUserAvatar, :inUserPhone, :inUserSex, :inUserPwd, :inUserStatus, :inUserPwdUpdate, :inUserRemark, :inCreateBy, SYSDATE())";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inDeptId", deptId);
        parameters.addValue("inUsername", username);
        parameters.addValue("inUserNick", userNickName);
        parameters.addValue("inUserEmail", userEmail);
        parameters.addValue("inUserAvatar", userAvatar);
        parameters.addValue("inUserPhone", userPhone);
        parameters.addValue("inUserSex", userSex);
        parameters.addValue("inUserPwd", userPwd);
        parameters.addValue("inUserStatus", userStatus);
        parameters.addValue("inUserPwdUpdate", userPwdUpdateDate);
        parameters.addValue("inUserRemark", userRemark);
        parameters.addValue("inCreateBy", createBy);

        long pK = dbService.insertAndReturnPk(insertSql, parameters);
        user.setUserId(pK);
        return pK > 0L ? 1 : -1;
    }

    @Override
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        Long deptId = user.getDeptId();
        String userNickName = user.getNickName();
        String userEmail = user.getEmail();
        String userAvatar = user.getAvatar();
        String userPhone = user.getPhonenumber();
        String userSex =  user.getSex();
        String userPwd = user.getPassword();
        String userStatus = user.getStatus();
        String userLoginIp = user.getLoginIp();
        Date userLoginDate = user.getLoginDate();
        String userRemark = user.getRemark();
        String updateBy = user.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_user SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotNull(deptId) && deptId != 0) {
            updateSqlBuffer.append(" dept_id=:inDeptId,");
            parameters.addValue("inDeptId", deptId);
        }
        if(StringUtils.isNotEmpty(userNickName)) {
            updateSqlBuffer.append(" nick_name=:inUserNick,");
            parameters.addValue("inUserNick", userNickName);
        }
        if(StringUtils.isNotEmpty(userEmail)) {
            updateSqlBuffer.append(" email=:inUserEmail,");
            parameters.addValue("inUserEmail", userEmail);
        }
        if(StringUtils.isNotEmpty(userAvatar)) {
            updateSqlBuffer.append(" avatar=:inUserAvatar,");
            parameters.addValue("inUserAvatar", userAvatar);
        }
        if(StringUtils.isNotEmpty(userPhone)) {
            updateSqlBuffer.append(" phonenumber=:inUserPhone,");
            parameters.addValue("inUserPhone", userPhone);
        }
        if(StringUtils.isNotEmpty(userSex)) {
            updateSqlBuffer.append(" sex=:inUserSex,");
            parameters.addValue("inUserSex", userSex);
        }
        if(StringUtils.isNotEmpty(userPwd)) {
            updateSqlBuffer.append(" password=:inUserPwd,");
            parameters.addValue("inUserPwd", userPwd);
        }
        if(StringUtils.isNotEmpty(userStatus)) {
            updateSqlBuffer.append(" status=:inUserStatus,");
            parameters.addValue("inUserStatus", userStatus);
        }
        if(StringUtils.isNotEmpty(userLoginIp)) {
            updateSqlBuffer.append(" login_ip=:inUserLogIp,");
            parameters.addValue("inUserLogIp", userLoginIp);
        }
        if(StringUtils.isNotNull(userLoginDate)) {
            updateSqlBuffer.append(" login_date=:inUserLogDate,");
            parameters.addValue("inUserLogDate", userLoginDate);
        }
        if(StringUtils.isNotEmpty(userRemark)) {
            updateSqlBuffer.append(" remark=:inUserRemark,");
            parameters.addValue("inUserRemark", userRemark);
        }

        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=SYSDATE() WHERE user_id=:inUserId");

        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUserId", userId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int updateUserAvatar(Long userId, String avatar) {
        String updateSql = "UPDATE sys_user SET avatar=:inUserAvatar, update_time=SYSDATE() WHERE user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUserId", userId);
        parameters.addValue("inUserAvatar", avatar);

        int[] updateResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int updateUserStatus(Long userId, String status) {
        String updateSql = "UPDATE sys_user SET status=:inUserStatus, update_time=SYSDATE() WHERE user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUserId", userId);
        parameters.addValue("inUserStatus", status);

        int[] updateResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int updateLoginInfo(Long userId, String loginIp, Date loginDate) {
        String updateSql = "UPDATE sys_user SET login_ip=:inUserLogIp, login_date=:inUserLogDate WHERE user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUserId", userId);
        parameters.addValue("inUserLogIp", loginIp);
        parameters.addValue("inUserLogDate", loginDate);

        int[] updateResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int resetUserPwd(Long userId, String password) {
        String updateSql = "UPDATE sys_user SET pwd_update_date=SYSDATE(), password=:inUserPwd, update_time=SYSDATE() WHERE user_id=:inUserId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("inUserId", userId);
        parameters.addValue("inUserPwd", password);

        int[] updateResList = dbService.batchUpdate(updateSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public int deleteUserById(Long userId) {
        return this.deleteUserByIds(new Long[]{userId});
    }

    @Override
    public int deleteUserByIds(Long[] userIds) {
        String deleteSql = "UPDATE sys_user SET del_flag = '2' WHERE user_id=:inUserId";

        MapSqlParameterSource[] parametersList = new MapSqlParameterSource[userIds.length];
        for (int i = 0; i < parametersList.length; i++) {
            Long userId = userIds[i];

            parametersList[i] = new MapSqlParameterSource("inUserId", userId);
        }

        int[] deleteResList = dbService.batchUpdate(deleteSql, parametersList);
        return deleteResList[0];
    }

    @Override
    public SysUser checkUserNameUnique(String userName) {
        String sql = "SELECT user_id, user_name FROM sys_user WHERE user_name=:inUsername AND del_flag = '0' LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUsername", userName);

        SysUser queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysUser.class));
        return queryObj;
    }

    @Override
    public SysUser checkPhoneUnique(String phonenumber) {
        String sql = "SELECT user_id, phonenumber FROM sys_user WHERE phonenumber=:inUserPhone AND del_flag = '0' LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserPhone", phonenumber);

        SysUser queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysUser.class));
        return queryObj;
    }

    @Override
    public SysUser checkEmailUnique(String email) {
        String sql = "SELECT user_id, email FROM sys_user WHERE email=:inUserEmail AND del_flag = '0' LIMIT 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inUserEmail", email);

        SysUser queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysUser.class));
        return queryObj;
    }

}
