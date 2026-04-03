package com.ruoyi.i18n.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.i18n.repository.LangTransRepository;
import com.ruoyi.i18n.domain.LangTrans;
import com.ruoyi.i18n.service.ILangTransService;

/**
 * 翻译文本Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-02
 */
@Service
public class LangTransServiceImpl implements ILangTransService
{
    @Resource
    private LangTransRepository langTransRepository;


    /**
     * 查询翻译文本
     *
     * @param langTrans 翻译文本
     * @return 翻译文本
     */
    @Override
    public LangTrans selectLangTransById(LangTrans langTrans)
    {
        return langTransRepository.selectLangTransById(langTrans);
    }

    /**
     * 根据条件分页查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 分页完成的翻译文本集合
     */
    @Override
    public TableDataInfo getPagedListResp(LangTrans langTrans)
    {
        return langTransRepository.getPagedListResp(langTrans);
    }

    /**
     * 查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 翻译文本
     */
    @Override
    public List<LangTrans> selectLangTransList(LangTrans langTrans)
    {
        return langTransRepository.selectLangTransList(langTrans);
    }

    /**
     * 新增翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    @Override
    public int insertLangTrans(LangTrans langTrans)
    {
        return langTransRepository.insertLangTrans(langTrans);
    }

    /**
     * 修改翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    @Override
    public int updateLangTrans(LangTrans langTrans)
    {
        return langTransRepository.updateLangTrans(langTrans);
    }

    /**
     * 批量删除翻译文本
     *
     * @param langIds 需要删除的翻译文本主键
     * @return 结果
     */
    @Override
    public int deleteLangTransByLangIds(Integer[] langIds)
    {
        return langTransRepository.deleteLangTransByLangIds(langIds);
    }

    /**
     * 删除翻译文本信息
     *
     * @param langId 翻译文本主键
     * @return 结果
     */
    @Override
    public int deleteLangTransByLangId(Integer langId)
    {
        return langTransRepository.deleteLangTransByLangId(langId);
    }
}
