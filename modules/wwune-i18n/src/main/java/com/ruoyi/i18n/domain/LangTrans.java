package com.ruoyi.i18n.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 翻译文本对象 lang_trans
 *
 * @author winter123
 * @date 2026-04-02
 */
public class LangTrans extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 语言序号 */
    @Excel(name = "语言序号")
    private Integer langId;

    /** 标签序号 */
    @Excel(name = "标签序号")
    private Integer tagId;

    /** 翻译文本 */
    @Excel(name = "翻译文本")
    private String transText;

    public void setLangId(Integer langId)
    {
        this.langId = langId;
    }

    public Integer getLangId()
    {
        return langId;
    }

    public void setTagId(Integer tagId)
    {
        this.tagId = tagId;
    }

    public Integer getTagId()
    {
        return tagId;
    }

    public void setTransText(String transText)
    {
        this.transText = transText;
    }

    public String getTransText()
    {
        return transText;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("langId", getLangId())
            .append("tagId", getTagId())
            .append("transText", getTransText())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
