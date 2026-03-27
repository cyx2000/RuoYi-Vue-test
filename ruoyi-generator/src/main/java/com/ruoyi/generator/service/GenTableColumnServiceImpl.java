package com.ruoyi.generator.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.repository.GenTableColumnRepository;

import jakarta.annotation.Resource;

/**
 * 业务字段 服务层实现
 *
 * @author ruoyi
 */
@Service
public class GenTableColumnServiceImpl implements IGenTableColumnService
{
	@Resource
	private GenTableColumnRepository genTableColumnRepository;

	/**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
	@Override
	public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId)
	{
	    return genTableColumnRepository.selectGenTableColumnListByTableId(tableId);
	}

    @Override
    public int[] batchInsertGenTableColumn(List<GenTableColumn> genTableColumns) {
        return genTableColumnRepository.batchInsertGenTableColumn(genTableColumns);
        }

    /**
     * 新增业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
	@Override
	public int insertGenTableColumn(GenTableColumn genTableColumn)
	{
	    return genTableColumnRepository.insertGenTableColumn(genTableColumn);
	}

	/**
     * 修改业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
	@Override
	public int updateGenTableColumn(GenTableColumn genTableColumn)
	{
	    return genTableColumnRepository.updateGenTableColumn(genTableColumn);
	}

	/**
     * 删除业务字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	public int deleteGenTableColumnByIds(String ids)
	{
		return genTableColumnRepository.deleteGenTableColumnByIds(Convert.toLongArray(ids));
	}
}
