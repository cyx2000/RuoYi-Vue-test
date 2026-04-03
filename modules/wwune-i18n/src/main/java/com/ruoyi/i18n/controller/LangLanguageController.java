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
import com.ruoyi.i18n.domain.LangLanguage;
import com.ruoyi.i18n.service.ILangLanguageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 语言Controller
 *
 * @author winter123
 * @date 2026-04-02
 */
@RestController
@RequestMapping("/i18n/language")
public class LangLanguageController extends BaseController
{
    @Resource
    private ILangLanguageService langLanguageService;

    /**
     * 分页查询语言列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:list')")
    @GetMapping("/list")
    public TableDataInfo list(LangLanguage langLanguage)
    {
        TableDataInfo pagedResp = langLanguageService.getPagedListResp(langLanguage);
        return pagedResp;
    }

    /**
     * 导出语言列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:export')")
    @Log(title = "语言", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LangLanguage langLanguage)
    {
        List<LangLanguage> list = langLanguageService.selectLangLanguageList(langLanguage);
        ExcelUtil<LangLanguage> util = new ExcelUtil<LangLanguage>(LangLanguage.class);
        util.exportExcel(response, list, "语言数据");
    }

    /**
     * 获取语言详细信息
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:query')")
    @GetMapping(value = "/{langId}")
    public AjaxResult getInfo(@PathVariable("langId") Integer langId)
    {
        return success(langLanguageService.selectLangLanguageByLangId(langId));
    }

    /**
     * 新增语言
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:add')")
    @Log(title = "语言", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LangLanguage langLanguage)
    {
        langLanguage.setCreateBy(getUsername());
        return toAjax(langLanguageService.insertLangLanguage(langLanguage));
    }

    /**
     * 修改语言
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:edit')")
    @Log(title = "语言", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LangLanguage langLanguage)
    {
        return toAjax(langLanguageService.updateLangLanguage(langLanguage));
    }

    /**
     * 删除语言
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:remove')")
    @Log(title = "语言", businessType = BusinessType.DELETE)
	@DeleteMapping("/{langIds}")
    public AjaxResult remove(@PathVariable Integer[] langIds)
    {
        return toAjax(langLanguageService.deleteLangLanguageByLangIds(langIds));
    }
}
