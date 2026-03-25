package com.ruoyi.system.repository;

import java.util.List;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.domain.SysNotice;

/**
 * 公告已读记录 数据层
 *
 * @author winter123
 */
public interface SysNoticeReadRepository
{
    /**
     * 新增已读记录（忽略重复）
     *
     * @param noticeRead 已读记录
     * @return 结果
     */
    public int insertNoticeRead(SysNoticeRead noticeRead);

    /**
     * 查询某用户未读公告数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    public int selectUnreadCount(Long userId);

    /**
     * 查询某用户是否已读某公告
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     * @return 已读记录数（0未读 1已读）
     */
    public int selectIsRead(Long noticeId, Long userId);

    /**
     * 批量标记已读
     *
     * @param userId    用户ID
     * @param noticeIds 公告ID数组
     * @return 结果
     */
    public int insertNoticeReadBatch(Long userId, Long[] noticeIds);

    /**
     * 查询带已读状态的公告列表（SQL层限制条数，一次查询完成）
     *
     * @param userId 用户ID
     * @param limit  最多返回条数
     * @return 带 isRead 标记的公告列表
     */
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 公告删除时清理对应已读记录
     *
     * @param noticeIds 公告ID数组
     * @return 结果
     */
    public int deleteByNoticeIds(Long[] noticeIds);
}
