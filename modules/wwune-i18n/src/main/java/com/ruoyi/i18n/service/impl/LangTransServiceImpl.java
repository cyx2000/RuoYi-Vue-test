package com.ruoyi.i18n.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.i18n.LocaleContextHolder;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.i18n.repository.LangLanguageRepository;
import com.ruoyi.i18n.repository.LangTransRepository;
import com.ruoyi.i18n.repository.LangTransTagRepository;
import com.ruoyi.i18n.domain.LangLanguage;
import com.ruoyi.i18n.domain.LangTrans;
import com.ruoyi.i18n.domain.LangTransTag;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(LangTransServiceImpl.class);

    @Resource
    private LangTransRepository langTransRepository;

    @Resource
    private LangTransTagRepository langTransTagRepository;

    @Resource
    private LangLanguageRepository langLanguageRepository;

    @Resource
    protected Validator validator;

    @Resource
    RedisCache redisCache;

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
     * 根据语言和模块从数据库查询翻译文本列表
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    @Override
    public List<LangTrans> selectLangTransListByModuleKey(String moduleKey)
    {
        String lang = LocaleContextHolder.getLocale().getLanguage();

        LangLanguage targetLanguage = langLanguageRepository.selectLangLanguageByLangTag(lang);

        LangTransTag tanstag = new LangTransTag();
        tanstag.setModuleTag(moduleKey);

        List<Integer> moduleTransTagIds = langTransTagRepository.selectModuleLangTransTagIds(tanstag);

        if (StringUtils.isEmpty(moduleTransTagIds)) {
            throw new ServiceException("错误的标签");
        }

        List<LangTrans> moduleTransTexts = langTransRepository.selectLangTransListByIds(targetLanguage.getLangId(), moduleTransTagIds);

        return moduleTransTexts;
    }

    /**
     * 根据语言和模块从redis查询翻译文本列表
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    @Override
    public List<LangTrans> tryGetModuleTransTextFromRedis(String moduleKey)
    {
        String lang = LocaleContextHolder.getLocale().getLanguage();

        String langMuduleKey = lang + ":" + moduleKey;

        Map<String, LangTrans> moduleMap = redisCache.getCacheMap(langMuduleKey);

        if (StringUtils.isNotEmpty(moduleMap))
        {
            return (List<LangTrans>) moduleMap.values();
        }

        return tryLoadModuleTransTextToRedis(moduleKey);
    }

    /**
     * 根据语言和模块从数据库查询翻译文本后加载到redis
     *
     * @param moduleKey 如："java.exp"
     * @return 翻译文本列表
     */
    @Override
    public List<LangTrans> tryLoadModuleTransTextToRedis(String moduleKey)
    {
        List<LangTrans> moduleTransTexts = this.selectLangTransListByModuleKey(moduleKey);

        String lang = LocaleContextHolder.getLocale().getLanguage();

        String langMuduleKey = lang + ":" + moduleKey;

        Map<String, LangTrans> moduleMap = new HashMap<String, LangTrans>();

        for (LangTrans langTrans : moduleTransTexts) {
            String label = langTrans.getTranstag().getLabel();

            moduleMap.put(label, langTrans);
        }

        // eg: {"zh:user.login": "notMatch": transObj1, "locked": transObj2 }
        redisCache.setCacheMap(langMuduleKey, moduleMap);

        // TODO 缓存时间配置化，当前为5分钟
        redisCache.expire(langMuduleKey, 5L, TimeUnit.MINUTES);

        return moduleTransTexts;
    }

    /**
     * 查询数据内容是否正确，并设置对应的标签id
     *
     * @param transtext 翻译文本
     */
    public void checkTransTextAndSetId(LangTrans transtext) throws Exception
    {
        BeanValidators.validateWithException(validator, transtext);

        LangTransTag targetTag = transtext.getTranstag();

        BeanValidators.validateWithException(validator, targetTag);

        LangTransTag existTag = langTransTagRepository.selectLangTransTag(targetTag);

        if (StringUtils.isNull(existTag))
        {
            throw new ServiceException("不存在该翻译标签" );
        }

        transtext.setTagId(existTag.getTagId());

        LangTrans existText = langTransRepository.selectLangTransById(transtext);

        if (StringUtils.isNotNull(existText))
        {
            throw new ServiceException("已经在数据库中的翻译文本，无法导入" );
        }
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

    /**
     * 导入翻译文本数据
     *
     * @param transtextList 翻译文本列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 信息
     */
    @Override
    public void importTransTexts(List<LangTrans> transtextList, String operName, Integer langId)
    {
        if (StringUtils.isEmpty(transtextList))
        {
            throw new ServiceException("导入的数据不能为空！");
        }

        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();

        for (int i = 0; i < transtextList.size(); i++)
        {
            LangTrans transtext = transtextList.get(i);

            transtext.setLangId(langId);

            try
            {
                this.checkTransTextAndSetId(transtext);

                transtext.setCreateBy(operName);

                langTransRepository.insertLangTrans(transtext);

            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>第 " + (i+1) + " 条数据：";
                failureMsg.append(msg + e.getMessage());
                LOGGER.error(msg, e);
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "一共有 " + failureNum + " 条错误数据，如下：");
            throw new ServiceException(failureMsg.toString());
        }
    }
}
