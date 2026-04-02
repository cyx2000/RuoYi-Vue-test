package com.ruoyi.i18n.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.i18n.repository.LangTransTagRepository;
import com.ruoyi.i18n.domain.LangTransTag;
import com.ruoyi.i18n.service.ILangTransTagService;

/**
 * 翻译标签Service业务层处理
 *
 * @author winter123
 * @date 2026-04-02
 */
@Service
public class LangTransTagServiceImpl implements ILangTransTagService
{
    @Resource
    private LangTransTagRepository langTransTagRepository;


    /**
     * 查询翻译标签
     *
     * @param tagId 翻译标签主键
     * @return 翻译标签
     */
    @Override
    public LangTransTag selectLangTransTagByTagId(Integer tagId)
    {
        return langTransTagRepository.selectLangTransTagByTagId(tagId);
    }

    /**
     * 根据条件分页查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 分页完成的翻译标签集合
     */
    @Override
    public TableDataInfo getPagedListResp(LangTransTag langTransTag)
    {
        return langTransTagRepository.getPagedListResp(langTransTag);
    }

    /**
     * 查询翻译标签列表
     *
     * @param langTransTag 翻译标签
     * @return 翻译标签
     */
    @Override
    public List<LangTransTag> selectLangTransTagList(LangTransTag langTransTag)
    {
        return langTransTagRepository.selectLangTransTagList(langTransTag);
    }

    /**
     * 新增翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    @Override
    public int insertLangTransTag(LangTransTag langTransTag)
    {
        return langTransTagRepository.insertLangTransTag(langTransTag);
    }

    /**
     * 修改翻译标签
     *
     * @param langTransTag 翻译标签
     * @return 结果
     */
    @Override
    public int updateLangTransTag(LangTransTag langTransTag)
    {
        return langTransTagRepository.updateLangTransTag(langTransTag);
    }

    /**
     * 批量删除翻译标签
     *
     * @param tagIds 需要删除的翻译标签主键
     * @return 结果
     */
    @Override
    public int deleteLangTransTagByTagIds(Integer[] tagIds)
    {
        return langTransTagRepository.deleteLangTransTagByTagIds(tagIds);
    }

    /**
     * 删除翻译标签信息
     *
     * @param tagId 翻译标签主键
     * @return 结果
     */
    @Override
    public int deleteLangTransTagByTagId(Integer tagId)
    {
        return langTransTagRepository.deleteLangTransTagByTagId(tagId);
    }
}
