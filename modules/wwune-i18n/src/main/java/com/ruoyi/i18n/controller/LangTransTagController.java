package com.ruoyi.i18n.controller;

import java.util.List;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.i18n.domain.LangTransTag;
import com.ruoyi.i18n.service.ILangTransTagService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 翻译标签Controller
 *
 * @author winter123
 * @date 2026-04-02
 */
@RestController
@RequestMapping("/i18n/transtag")
public class LangTransTagController extends BaseController
{
    @Resource
    private ILangTransTagService langTransTagService;

    /**
     * 查询翻译标签列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:list')")
    @GetMapping("/list")
    public TableDataInfo list(LangTransTag langTransTag)
    {
        TableDataInfo pagedResp = langTransTagService.getPagedListResp(langTransTag);
        return pagedResp;
    }

    /**
     * 导出翻译标签列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:export')")
    @Log(title = "翻译标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LangTransTag langTransTag)
    {
        List<LangTransTag> list = langTransTagService.selectLangTransTagList(langTransTag);
        ExcelUtil<LangTransTag> util = new ExcelUtil<LangTransTag>(LangTransTag.class);
        util.exportExcel(response, list, "翻译标签数据");
    }

    /**
     * 获取翻译标签详细信息
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:query')")
    @GetMapping(value = "/{tagId}")
    public AjaxResult getInfo(@PathVariable("tagId") Integer tagId)
    {
        return success(langTransTagService.selectLangTransTagByTagId(tagId));
    }

    /**
     * 新增翻译标签
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:add')")
    @Log(title = "翻译标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LangTransTag langTransTag)
    {
        langTransTag.setCreateBy(getUsername());
        return toAjax(langTransTagService.insertLangTransTag(langTransTag));
    }

    /**
     * 修改翻译标签
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:edit')")
    @Log(title = "翻译标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LangTransTag langTransTag)
    {
        langTransTag.setUpdateBy(getUsername());
        return toAjax(langTransTagService.updateLangTransTag(langTransTag));
    }

    /**
     * 删除翻译标签
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:remove')")
    @Log(title = "翻译标签", businessType = BusinessType.DELETE)
	@DeleteMapping("/{tagIds}")
    public AjaxResult remove(@PathVariable Integer[] tagIds)
    {
        return toAjax(langTransTagService.deleteLangTransTagByTagIds(tagIds));
    }
}
