package com.ruoyi.i18n.service;

import java.util.List;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.i18n.domain.LangTransTag;

/**
 * 翻译标签Service接口
 *
 * @author winter123
 * @date 2026-04-02
 */
public interface ILangTransTagService
{
    /**
     * 查询翻译标签
     *
     * @param tagId 翻译标签主键
     * @return 翻译标签
     */
    public LangTransTag selectLangTransTagByTagId(Integer tagId);

    /**
     * 根据条件分页查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 分页完成的翻译标签集合
     */
    public TableDataInfo getPagedListResp(LangTransTag langTransTag);

    /**
     * 查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签集合
     */
    public List<LangTransTag> selectLangTransTagList(LangTransTag langTransTag);

    /**
     * 新增翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    public int insertLangTransTag(LangTransTag langTransTag);

    /**
     * 修改翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    public int updateLangTransTag(LangTransTag langTransTag);

    /**
     * 批量删除翻译标签
     *
     * @param tagIds 需要删除的翻译标签主键集合
     * @return 结果
     */
    public int deleteLangTransTagByTagIds(Integer[] tagIds);

    /**
     * 删除翻译标签信息
     *
     * @param tagId 翻译标签主键
     * @return 结果
     */
    public int deleteLangTransTagByTagId(Integer tagId);
}
