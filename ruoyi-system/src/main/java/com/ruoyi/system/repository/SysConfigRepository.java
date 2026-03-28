package com.ruoyi.system.repository;

import java.util.List;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysConfig;

/**
 * 参数配置 仓库接口
 *
 * @author winter123
 */
public interface SysConfigRepository
{
    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    public SysConfig selectConfigByKey(String configKey);

    /**
     * 通过ID查询配置
     *
     * @param configId 参数ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 分页完成的参数配置集合
     */
    public TableDataInfo getPagedListResp(SysConfig config);

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果，1是成功，0是失败
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果，1是成功，0是失败
     */
    public int updateConfig(SysConfig config);

    /**
     * 删除参数配置
     *
     * @param configId 参数ID
     * @return 结果，1是成功，0是失败
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果集，1是成功，0是失败
     */
    public int[] deleteConfigByIds(Long[] configIds);
}
