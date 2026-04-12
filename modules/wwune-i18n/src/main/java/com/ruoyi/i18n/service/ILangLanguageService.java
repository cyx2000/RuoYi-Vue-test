package com.ruoyi.i18n.service;

import java.util.List;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.i18n.domain.LangLanguage;

/**
 * 语言Service接口
 *
 * @author winter123
 * @date 2026-04-02
 */
public interface ILangLanguageService
{
    /**
     * 查询语言
     *
     * @param langId 语言主键
     * @return 语言
     */
    public LangLanguage selectLangLanguageByLangId(Integer langId);

    /**
     * 查询语言及翻译标签列表
     *
     * @param langId 语言主键
     * @return 语言
     */
    public LangLanguage selectLangLanguageAndTransTagsByLangId(Integer langId);

    /**
     * 分页查询语言列表
     *
     * @param langLanguage 语言
     * @return 分页完成的语言集合
     */
    public TableDataInfo getPagedListResp(LangLanguage langLanguage);

    /**
     * 查询语言列表
     *
     * @param langLanguage 语言
     * @return 语言集合
     */
    public List<LangLanguage> selectLangLanguageList(LangLanguage langLanguage);

    /**
     * 查询所有可用的语言列表
     *
     * @return 语言集合
     */
    public List<LangLanguage> getAllNormalLanguages();

    /**
     * 新增语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    public int insertLangLanguage(LangLanguage langLanguage);

    /**
     * 修改语言
     *
     * @param langLanguage 语言
     * @return 结果
     */
    public int updateLangLanguage(LangLanguage langLanguage);

    /**
     * 批量删除语言
     *
     * @param langIds 需要删除的语言主键集合
     * @return 结果
     */
    public int deleteLangLanguageByLangIds(Integer[] langIds);

    /**
     * 删除语言信息
     *
     * @param langId 语言主键
     * @return 结果
     */
    public int deleteLangLanguageByLangId(Integer langId);
}
