package com.ruoyi.system.repository.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.db.DBService;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.repository.SysDeptRepository;

@Service
public class SysDeptRepositoryImpl implements SysDeptRepository {

    private DBService dbService;

    private String baseSelectSql = "SELECT d.dept_id, d.parent_id, d.ancestors, d.dept_name, d.order_num, d.leader, d.phone, d.email, d.status, d.del_flag, d.create_by, d.create_time FROM sys_dept d WHERE d.del_flag='0'";

    public SysDeptRepositoryImpl(DBService inDbService) {
        this.dbService = inDbService;
    }

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        Long deptId = dept.getDeptId();
        Long parentId = dept.getParentId();
        String deptName = dept.getDeptName();
        String deptStatus = dept.getStatus();

        StringBuilder addWhereBuilder = new StringBuilder(baseSelectSql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if(StringUtils.isNotNull(deptId) && deptId != 0L) {
            addWhereBuilder.append(" AND d.dept_id=:inDeptId");
            parameters.addValue("inDeptId", deptId);
        }
        if(StringUtils.isNotNull(parentId) && parentId != 0L) {
            addWhereBuilder.append(" AND d.parent_id=:inParentId");
            parameters.addValue("inParentId", deptId);
        }
        if(StringUtils.isNotEmpty(deptName)) {
            addWhereBuilder.append(" AND d.dept_name LIKE CONCAT('%', :inDeptName, '%')");
            parameters.addValue("inDeptName", deptName);
        }
        if(StringUtils.isNotEmpty(deptStatus)) {
            addWhereBuilder.append(" AND d.status=:inDeptStatus");
            parameters.addValue("inDeptStatus", deptStatus);
        }
        addWhereBuilder.append(dept.getDataScopeFilterParam());
        addWhereBuilder.append(" ORDER BY d.parent_id, d.order_num");

        List<SysDept> queryList = dbService.queryForList(addWhereBuilder.toString(), parameters, new SimplePropertyRowMapper<>(SysDept.class));
        return queryList;
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId, boolean deptCheckStrictly) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT d.dept_id FROM sys_dept d LEFT JOIN sys_role_dept rd ON d.dept_id = rd.dept_id WHERE rd.role_id=:inRoleId");

        MapSqlParameterSource parameters = new MapSqlParameterSource("inRoleId", roleId);

        if(deptCheckStrictly) {
            sqlBuilder.append(" AND d.dept_id NOT IN (SELECT d.parent_id FROM sys_dept d INNER JOIN sys_role_dept rd ON d.dept_id = rd.dept_id AND rd.role_id=:inRoleId)");
        }
        sqlBuilder.append(" ORDER BY d.parent_id, d.order_num");

        List<Long> queryList = dbService.queryForList(sqlBuilder.toString(), parameters, Long.class);
        return queryList;
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        String sql = "SELECT d.dept_id, d.parent_id, d.ancestors, d.dept_name, d.order_num, d.leader, d.phone, d.email, d.status,(SELECT dept_name FROM sys_dept where dept_id = d.parent_id) parent_name FROM sys_dept d WHERE d.dept_id=:inDeptId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        SysDept queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDept.class));
        return queryObj;
    }

    @Override
    public List<SysDept> selectChildrenDeptById(Long deptId) {
        String sql = "SELECT * FROM sys_dept WHERE FIND_IN_SET(:inDeptId, ancestors)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        List<SysDept> queryList = dbService.queryForList(sql, parameters, new SimplePropertyRowMapper<>(SysDept.class));
        return queryList;
    }

    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        String sql = "SELECT count(1) FROM sys_dept WHERE status = 0 AND del_flag = '0' AND FIND_IN_SET(:inDeptId, ancestors)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        Integer queryList = dbService.queryForObject(sql, parameters, Integer.class);
        return queryList.intValue();
    }

    @Override
    public int hasChildByDeptId(Long deptId) {
        String sql = "SELECT count(1) FROM sys_dept WHERE del_flag = '0' AND parent_id=:inDeptId limit 1";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        Integer queryList = dbService.queryForObject(sql, parameters, Integer.class);
        return queryList.intValue();
    }

    @Override
    public int checkDeptExistUser(Long deptId) {
        String sql = "SELECT count(1) FROM sys_user WHERE dept_id=:inDeptId AND del_flag = '0'";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        Integer queryList = dbService.queryForObject(sql, parameters, Integer.class);
        return queryList.intValue();
    }

    @Override
    public SysDept checkDeptNameUnique(String deptName, Long parentId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptName", deptName);
        parameters.addValue("inParentId", parentId);

        String sql = baseSelectSql + " AND dept_name=:inDeptName AND parent_id=:inParentId LIMIT 1";

        SysDept queryObj = dbService.queryForObject(sql, parameters, new SimplePropertyRowMapper<>(SysDept.class));
        return queryObj;
    }

    @Override
    public int insertDept(SysDept dept) {
        Long parentId = dept.getParentId();
        String deptName = dept.getDeptName();
        String deptAncestors = dept.getAncestors();
        Integer deptOrderNum = dept.getOrderNum();
        String deptLeader = dept.getLeader();
        String deptLeaderPhone = dept.getPhone();
        String deptEmail = dept.getEmail();
        String deptStatus = dept.getStatus();
        String createBy = dept.getCreateBy();

        String insterSql = "INSERT INTO sys_dept(parent_id, dept_name, ancestors, order_num, leader, phone, email, status, create_by, create_time) VALUES(:inParentId, :inDeptName, :inDeptAncestors, :inDeptOrderNum, :inDeptLeader, :inDeptLeaderPhone, :inDeptEmail, :inDeptStatus, :inCreateBy, :inCreateTime)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inParentId", parentId);
        parameters.addValue("inDeptName", deptName);
        parameters.addValue("inDeptAncestors", deptAncestors);
        parameters.addValue("inDeptOrderNum", deptOrderNum);
        parameters.addValue("inDeptLeader", deptLeader);
        parameters.addValue("inDeptLeaderPhone", deptLeaderPhone);
        parameters.addValue("inDeptEmail", deptEmail);
        parameters.addValue("inDeptStatus", deptStatus);
        parameters.addValue("inCreateBy", createBy);
        parameters.addValue("inCreateTime", LocalDateTime.now(ZoneId.of("UTC")));

        int[] insertResList = dbService.batchUpdate(insterSql, new MapSqlParameterSource[]{parameters});
        return insertResList[0];

    }

    @Override
    public int updateDept(SysDept dept) {
        Long deptId = dept.getDeptId();
        Long parentId = dept.getParentId();
        String deptName = dept.getDeptName();
        String deptAncestors = dept.getAncestors();
        Integer deptOrderNum = dept.getOrderNum();
        String deptLeader = dept.getLeader();
        String deptLeaderPhone = dept.getPhone();
        String deptEmail = dept.getEmail();
        String deptStatus = dept.getStatus();
        String updateBy = dept.getUpdateBy();

        StringBuffer updateSqlBuffer = new StringBuffer("UPDATE sys_dept SET");

        MapSqlParameterSource parameters = new MapSqlParameterSource("inParentId", parentId);

        if(StringUtils.isNotNull(parentId) && parentId != 0) {
            updateSqlBuffer.append(" parent_id=:inParentId,");
            parameters.addValue("inParentId", parentId);
        }
        if(StringUtils.isNotEmpty(deptName)) {
            updateSqlBuffer.append(" dept_name=:inDeptName,");
            parameters.addValue("inDeptName", deptName);
        }
        if(StringUtils.isNotEmpty(deptAncestors)) {
            updateSqlBuffer.append(" ancestors=:inDeptAncestors,");
            parameters.addValue("inDeptAncestors", deptAncestors);
        }
        if(StringUtils.isNotNull(deptOrderNum)) {
            updateSqlBuffer.append(" order_num=:inDeptOrderNum,");
            parameters.addValue("inDeptOrderNum", deptOrderNum);
        }
        if(StringUtils.isNotEmpty(deptLeader)) {
            updateSqlBuffer.append(" leader=:inDeptLeader,");
            parameters.addValue("inDeptLeader", deptLeader);
        }
        if(StringUtils.isNotEmpty(deptLeaderPhone)) {
            updateSqlBuffer.append(" phone=:inDeptLeaderPhone,");
            parameters.addValue("inDeptLeaderPhone", deptLeaderPhone);
        }
        if(StringUtils.isNotEmpty(deptEmail)) {
            updateSqlBuffer.append(" email=:inDeptEmail,");
            parameters.addValue("inDeptEmail", deptEmail);
        }
        if(StringUtils.isNotEmpty(deptStatus)) {
            updateSqlBuffer.append(" status=:inDeptStatus,");
            parameters.addValue("inDeptStatus", deptStatus);
        }
        updateSqlBuffer.append(" update_by=:inUpdateBy, update_time=:inUpdateTime WHERE dept_id=:inDeptId");
        parameters.addValue("inUpdateBy", updateBy);
        parameters.addValue("inUpdateTime", LocalDateTime.now(ZoneId.of("UTC")));
        parameters.addValue("inDeptId", deptId);

        int[] updateResList = dbService.batchUpdate(updateSqlBuffer.toString(), new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

    @Override
    public void updateDeptStatusNormal(Long[] deptIds) {
        String sql = "UPDATE sys_dept SET status = '0' WHERE dept_id=:inDeptId";

        MapSqlParameterSource[] paramsList = new MapSqlParameterSource[deptIds.length];
        for (int i = 0; i < paramsList.length; i++) {
            Long deptId = deptIds[i];
            paramsList[i] = new MapSqlParameterSource("inDeptId", deptId);
        }

        dbService.batchUpdate(sql, paramsList);
    }

    @Override
    public int updateDeptChildren(List<SysDept> depts) {
        String sql = "UPDATE sys_dept SET ancestors = CASE dept_id WHEN :inDeptId THEN :inDeptAncestors END WHERE dept_id=:inDeptId";

        MapSqlParameterSource[] paramsList = new MapSqlParameterSource[depts.size()];
        for (int i = 0; i < paramsList.length; i++) {
            Long deptId = depts.get(i).getDeptId();
            String deptAncestors = depts.get(i).getAncestors();
            paramsList[i] = new MapSqlParameterSource("inDeptId", deptId);
            paramsList[i].addValue("inDeptAncestors", deptAncestors);
        }

        int[] updateResList = dbService.batchUpdate(sql, paramsList);
        return updateResList[0];
    }

    @Override
    public void updateDeptSort(SysDept dept) {
        String sql = "UPDATE sys_dept SET order_num=:inDeptOrderNum WHERE dept_id=:inDeptId";

        Long deptId = dept.getDeptId();
        Integer deptOrderNum = dept.getOrderNum();

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);
        parameters.addValue("inDeptOrderNum", deptOrderNum);

        dbService.batchUpdate(sql, new MapSqlParameterSource[]{parameters});
    }

    @Override
    public int deleteDeptById(Long deptId) {
        String deletSql = "UPDATE sys_dept SET del_flag = '2' WHERE dept_id=:inDeptId";

        MapSqlParameterSource parameters = new MapSqlParameterSource("inDeptId", deptId);

        int[] updateResList = dbService.batchUpdate(deletSql, new MapSqlParameterSource[]{parameters});
        return updateResList[0];
    }

}
