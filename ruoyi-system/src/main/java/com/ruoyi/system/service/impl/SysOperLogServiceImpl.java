package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.repository.SysOperLogRepository;
import com.ruoyi.system.service.ISysOperLogService;

import jakarta.annotation.Resource;

/**
 * 操作日志 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService
{
    @Resource
    private SysOperLogRepository operLogRepository;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog)
    {
        operLogRepository.insertOperlog(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog)
    {
        return operLogRepository.selectOperLogList(operLog);
    }

    /**
     * 根据条件分页查询系统操作日志
     *
     * @param operLog 操作日志对象
     * @return 分页完成的操作日志集合
     */
    @Override
    public TableDataInfo getPagedListResp(SysOperLog operLog) {
        return operLogRepository.getPagedListResp(operLog);
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return operLogRepository.deleteOperLogByIds(operIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return operLogRepository.selectOperLogById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        operLogRepository.cleanOperLog();
    }
}
