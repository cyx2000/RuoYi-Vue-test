package com.ruoyi.i18n.domain;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 语言对象 lang_language
 *
 * @author winter123
 * @date 2026-04-02
 */
public class LangLanguage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 语言序号 */
    private Integer langId;

    /** 语言标签 */
    private String langTag;

    /** 展示顺序 */
    private Integer sort;

    /** 语言状态（0正常 1停用 2删除 3是删除和停用） */
    private Integer status;

    /** 默认语言（0不是默认，1是默认） */
    private String isDefault;

    /** 翻译标签列表 */
    private List<LangTransTag> transtags;

    public void setLangId(Integer langId)
    {
        this.langId = langId;
    }

    public Integer getLangId()
    {
        return langId;
    }

    public void setLangTag(String langTag)
    {
        this.langTag = langTag;
    }

    public String getLangTag()
    {
        return langTag;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setIsDefault(String isDefault)
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault()
    {
        return isDefault;
    }

    public List<LangTransTag> getTranstags() {
        return transtags;
    }

    public void setTranstags(List<LangTransTag> transtags) {
        this.transtags = transtags;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("langId", getLangId())
            .append("langTag", getLangTag())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("isDefault", getIsDefault())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
