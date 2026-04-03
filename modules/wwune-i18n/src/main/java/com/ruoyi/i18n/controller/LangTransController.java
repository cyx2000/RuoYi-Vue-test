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
import com.ruoyi.i18n.domain.LangTrans;
import com.ruoyi.i18n.service.ILangTransService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 翻译文本Controller
 *
 * @author winter123
 * @date 2026-04-02
 */
@RestController
@RequestMapping("/i18n/transtext")
public class LangTransController extends BaseController
{
    @Resource
    private ILangTransService langTransService;

    /**
     * 根据条件分页查询翻译文本列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:list')")
    @GetMapping("/list")
    public TableDataInfo list(LangTrans langTrans)
    {
        TableDataInfo pagedResp = langTransService.getPagedListResp(langTrans);
        return pagedResp;
    }

    /**
     * 导出翻译文本列表
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:export')")
    @Log(title = "翻译文本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LangTrans langTrans)
    {
        List<LangTrans> list = langTransService.selectLangTransList(langTrans);
        ExcelUtil<LangTrans> util = new ExcelUtil<LangTrans>(LangTrans.class);
        util.exportExcel(response, list, "翻译文本数据");
    }

    /**
     * 获取翻译文本详细信息
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:query')")
    @GetMapping(value = "/{langId}")
    public AjaxResult getInfo(@PathVariable("langId") Integer langId)
    {
        return success(langTransService.selectLangTransByLangId(langId));
    }

    /**
     * 新增翻译文本
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:add')")
    @Log(title = "翻译文本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LangTrans langTrans)
    {
        langTrans.setCreateBy(getUsername());
        return toAjax(langTransService.insertLangTrans(langTrans));
    }

    /**
     * 修改翻译文本
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:edit')")
    @Log(title = "翻译文本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LangTrans langTrans)
    {
        langTrans.setUpdateBy(getUsername());
        return toAjax(langTransService.updateLangTrans(langTrans));
    }

    /**
     * 删除翻译文本
     */
    @PreAuthorize("@ss.hasPermi('i18n:translang:remove')")
    @Log(title = "翻译文本", businessType = BusinessType.DELETE)
	@DeleteMapping("/{langIds}")
    public AjaxResult remove(@PathVariable Integer[] langIds)
    {
        return toAjax(langTransService.deleteLangTransByLangIds(langIds));
    }
}
