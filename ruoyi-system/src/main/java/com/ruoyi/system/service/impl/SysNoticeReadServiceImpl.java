package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.repository.SysNoticeReadRepository;
import com.ruoyi.system.service.ISysNoticeReadService;

import jakarta.annotation.Resource;

/**
 * 公告已读记录 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysNoticeReadServiceImpl implements ISysNoticeReadService
{
    @Resource
    private SysNoticeReadRepository noticeReadRepository;

    /**
     * 标记已读
     */
    @Override
    public void markRead(Long noticeId, Long userId)
    {
        SysNoticeRead record = new SysNoticeRead();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        noticeReadRepository.insertNoticeRead(record);
    }

    /**
     * 查询某用户未读公告数量
     */
    @Override
    public int selectUnreadCount(Long userId)
    {
        return noticeReadRepository.selectUnreadCount(userId);
    }

    /**
     * 查询公告列表并标记当前用户已读状态
     */
    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit)
    {
        return noticeReadRepository.selectNoticeListWithReadStatus(userId, limit);
    }

    /**
     * 批量标记已读
     */
    @Override
    public void markReadBatch(Long userId, Long[] noticeIds)
    {
        if (noticeIds == null || noticeIds.length == 0)
        {
            return;
        }
        noticeReadRepository.insertNoticeReadBatch(userId, noticeIds);
    }

    /**
     * 删除公告时清理对应已读记录
     */
    @Override
    public void deleteByNoticeIds(Long[] noticeIds)
    {
        noticeReadRepository.deleteByNoticeIds(noticeIds);
    }
}
