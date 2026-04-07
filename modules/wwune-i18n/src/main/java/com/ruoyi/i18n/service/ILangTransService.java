package com.ruoyi.i18n.service;

import java.util.List;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.i18n.domain.LangTrans;

/**
 * 翻译文本Service接口
 *
 * @author winter123
 * @date 2026-04-02
 */
public interface ILangTransService
{
    /**
     * 查询翻译文本
     *
     * @param langTrans 翻译文本
     * @return 翻译文本结果
     */
    public LangTrans selectLangTransById(LangTrans langTrans);

    /**
     * 根据语言和模块从数据库查询翻译文本列表
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    public List<LangTrans> selectLangTransListByModuleKey(String moduleKey);

    /**
     * 根据语言和模块从redis查询翻译文本列表
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    public List<LangTrans> tryGetModuleTransTextFromRedis(String moduleKey);

    /**
     * 根据语言和模块从数据库查询翻译文本后加载到redis
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    public List<LangTrans> tryLoadModuleTransTextToRedis(String moduleKey);

    /**
     * 查询数据内容是否正确，并设置对应的标签id
     *
     * @param transtext 翻译文本
     */
    public void checkTransTextAndSetId(LangTrans transtext) throws Exception;

    /**
     * 根据条件分页查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 分页完成的翻译文本集合
     */
    public TableDataInfo getPagedListResp(LangTrans langTrans);

    /**
     * 查询翻译文本列表
     *
     * @param langTrans 翻译文本
     * @return 翻译文本集合
     */
    public List<LangTrans> selectLangTransList(LangTrans langTrans);

    /**
     * 新增翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    public int insertLangTrans(LangTrans langTrans);

    /**
     * 修改翻译文本
     *
     * @param langTrans 翻译文本
     * @return 结果
     */
    public int updateLangTrans(LangTrans langTrans);

    /**
     * 批量删除翻译文本
     *
     * @param langIds 需要删除的翻译文本主键集合
     * @return 结果
     */
    public int deleteLangTransByLangIds(Integer[] langIds);

    /**
     * 删除翻译文本信息
     *
     * @param langId 翻译文本主键
     * @return 结果
     */
    public int deleteLangTransByLangId(Integer langId);

    /**
     * 导入翻译文本数据
     *
     * @param transtextList 翻译文本列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 信息
     */
    public void importTransTexts(List<LangTrans> transtextList, String operName, Integer langId);
}
