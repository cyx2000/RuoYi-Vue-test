package com.ruoyi.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.repository.SysLogininforRepository;
import com.ruoyi.system.service.ISysLogininforService;

import jakarta.annotation.Resource;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService
{

    @Resource
    private SysLogininforRepository logininforRepository;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor)
    {
        logininforRepository.insertLogininfor(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        return logininforRepository.selectLogininforList(logininfor);
    }

    /**
     * 根据条件分页查询登录日志
     *
     * @param logininfor 访问日志对象
     * @return 分页完成的登录日志集合
     */
    @Override
    public TableDataInfo getPagedListResp(SysLogininfor logininfor) {
        return logininforRepository.getPagedListResp(logininfor);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return logininforRepository.deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        logininforRepository.cleanLogininfor();
    }
}
