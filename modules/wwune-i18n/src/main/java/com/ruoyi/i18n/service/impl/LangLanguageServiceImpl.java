package com.ruoyi.i18n.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.i18n.repository.LangLanguageRepository;
import com.ruoyi.i18n.repository.LangTransTagRepository;
import com.ruoyi.i18n.domain.LangLanguage;
import com.ruoyi.i18n.domain.LangTransTag;
import com.ruoyi.i18n.service.ILangLanguageService;

/**
 * 语言Service业务层处理
 *
 * @author winter123
 * @date 2026-04-02
 */
@Service
public class LangLanguageServiceImpl implements ILangLanguageService
{
    @Resource
    private LangLanguageRepository langLanguageRepository;

    @Resource
    private LangTransTagRepository langTransTagRepository;

    /**
     * 查询语言
     *
     * @param langId 语言主键
     * @return 语言
     */
    @Override
    public LangLanguage selectLangLanguageByLangId(Integer langId)
    {
        return langLanguageRepository.selectLangLanguageByLangId(langId);
    }

    /**
     * 查询语言及翻译标签列表
     *
     * @param langId 语言主键
     * @return 语言
     */
    @Override
    public LangLanguage selectLangLanguageAndTransTagsByLangId(Integer langId) {
        LangLanguage lang = selectLangLanguageByLangId(langId);
        if (StringUtils.isNotNull(lang))
        {
            List<LangTransTag> transtags = langTransTagRepository.selectLangTransTagList(new LangTransTag());

            lang.setTranstags(transtags);
        }
        return lang;
    }

    /**
     * 分页查询语言列表
     *
     * @param langLanguage 语言
     * @return 分页完成的语言集合
     */
    @Override
    public TableDataInfo getPagedListResp(LangLanguage langLanguage)
    {
        return langLanguageRepository.getPagedListResp(langLanguage);
    }

    /**
     * 查询语言列表
     *
     * @param langLanguage 语言
     * @return 语言
     */
    @Override
    public List<LangLanguage> selectLangLanguageList(LangLanguage langLanguage)
    {
        return langLanguageRepository.selectLangLanguageList(langLanguage);
    }

    /**
     * 新增语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    @Override
    public int insertLangLanguage(LangLanguage langLanguage)
    {
        LangLanguage lang = langLanguageRepository.selectLangLanguageByLangTag(langLanguage.getLangTag());
        if(StringUtils.isNotNull(lang)) {
            // TODO 国际化，面向客户端
            throw new ServiceException("语言标签已存在，无法创建");
        }
        langLanguage.setStatus(1);
        return langLanguageRepository.insertLangLanguage(langLanguage);
    }

    /**
     * 修改语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    @Override
    public int updateLangLanguage(LangLanguage langLanguage)
    {
        return langLanguageRepository.updateLangLanguage(langLanguage);
    }

    /**
     * 批量删除语言
     *
     * @param langIds 需要删除的语言主键
     * @return 结果
     */
    @Override
    public int deleteLangLanguageByLangIds(Integer[] langIds)
    {
        return langLanguageRepository.deleteLangLanguageByLangIds(langIds);
    }

    /**
     * 删除语言信息
     *
     * @param langId 语言主键
     * @return 结果
     */
    @Override
    public int deleteLangLanguageByLangId(Integer langId)
    {
        return langLanguageRepository.deleteLangLanguageByLangId(langId);
    }
}
