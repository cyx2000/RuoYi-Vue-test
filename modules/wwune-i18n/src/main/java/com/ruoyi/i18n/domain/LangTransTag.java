package com.ruoyi.i18n.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.StringUtils;

/**
 * 翻译标签对象 lang_trans_tag
 *
 * @author winter123
 * @date 2026-04-02
 */
public class LangTransTag extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 标签序号 */
    @Excel(name = "标签序号")
    private Integer tagId;

    /** 类型（比如java，file） */
    @Excel(name = "类型", readConverterExp = "比=如java，file")
    private String tagType;

    /** 模块（比如exception，write） */
    @Excel(name = "模块", readConverterExp = "比=如exception，write")
    private String module;

    /** 标签（比如serli，最后拼成java.excep.serli） */
    @Excel(name = "标签", readConverterExp = "比=如serli，最后拼成java.excep.serli")
    private String label;

    /** 发给客户端（0不是，1是） */
    @Excel(name = "发给客户端", readConverterExp = "0=不是，1是")
    private String toApp;

    /** 语言状态（0正常 1停用 2删除 3是删除和停用） */
    @Excel(name = "语言状态", readConverterExp = "0=正常,1=停用,2=删除,3=是删除和停用")
    private Integer status;

    public void setTagId(Integer tagId)
    {
        this.tagId = tagId;
    }

    public Integer getTagId()
    {
        return tagId;
    }

    public void setTagType(String tagType)
    {
        this.tagType = tagType;
    }

    public String getTagType()
    {
        return tagType;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public String getModule()
    {
        return module;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setToApp(String toApp)
    {
        this.toApp = toApp;
    }

    public String getToApp()
    {
        return toApp;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public String getTransTag() {
        StringBuilder strBuilder = new StringBuilder(this.getTagType());

        strBuilder.append(".");
        if (StringUtils.isNotEmpty(this.getModule())) {
            strBuilder.append(this.getModule())
                .append(".");
        }
        strBuilder.append(this.getLabel());

        return strBuilder.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("tagId", getTagId())
            .append("tagType", getTagType())
            .append("module", getModule())
            .append("label", getLabel())
            .append("toApp", getToApp())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
