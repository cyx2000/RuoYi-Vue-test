package com.ruoyi.i18n.service.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.validation.Validator;

import org.springframework.stereotype.Service;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.json.JsonUtils;
import com.ruoyi.i18n.repository.LangLanguageRepository;
import com.ruoyi.i18n.repository.LangTransTagRepository;
import com.ruoyi.i18n.domain.LangLanguage;
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

    @Resource
    private LangLanguageRepository langLanguageRepository;

    @Resource
    protected Validator validator;

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
        LangTransTag queryTransTag = langTransTagRepository.selectLangTransTag(langTransTag);
        if (StringUtils.isNotNull(queryTransTag))
        {
            throw new ServiceException("已经在数据库中");
        }
        return langTransTagRepository.insertLangTransTag(langTransTag);
    }

    /**
     * 导入翻译标签
     *
     * @param langTransTags 翻译标签列表
     * @param operName 用户名
     * @return 结果
     */
    @Override
    public void importTransTags(List<LangTransTag> langTransTags, String operName)
    {
        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();

        if (StringUtils.isEmpty(langTransTags))
        {
            throw new ServiceException("没有数据，无法导入");
        }

        List<Long> transtagIds = new ArrayList<>();

        for (int i = 0; i < langTransTags.size(); ++i)
        {
            LangTransTag langTransTag = langTransTags.get(i);

            try
            {
                BeanValidators.validateWithException(validator, langTransTag);

                LangTransTag queryTransTag = langTransTagRepository.selectLangTransTag(langTransTag);
                if (StringUtils.isNotNull(queryTransTag))
                {
                    throw new ServiceException("已经在数据库中，无法导入");
                }

                langTransTag.setCreateBy(operName);

                long pk = langTransTagRepository.insertLangTransTagAndReturnId(langTransTag);

                transtagIds.add(pk);

            } catch (Exception e) {
                langTransTags.remove(i);
                failureNum++;
                String msg = "<br/>第 " + (i+1) + " 条数据：";
                failureMsg.append(msg + e.getMessage());
            }
        }

        List<LangLanguage> currentLanguages = langLanguageRepository.selectLangLanguageList(null);

        for (LangLanguage langLanguage : currentLanguages) {
            List<Long> current = JsonUtils.jsonArrayToList(langLanguage.getTransTags(), Long.class);

            current.addAll(transtagIds);

            langLanguage.setTransTags(JsonUtils.toJsonStr(current));

            langLanguageRepository.updateLangLanguageTransTags(langLanguage);
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "一共有 " + failureNum + " 条错误数据，如下：");
            throw new ServiceException(failureMsg.toString());
        }
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
