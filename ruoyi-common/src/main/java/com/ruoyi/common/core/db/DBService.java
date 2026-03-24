package com.ruoyi.common.core.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.sql.SqlUtil;

/**
 * jdbc服务，封装数据库操作
 *
 * @author winter123
 */
@Component
public class DBService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DBService.class);

    private NamedParameterJdbcTemplate namedJdbc;

    public DBService(DataSource inDataSource) {
        this.setNamedJdbc(new NamedParameterJdbcTemplate(inDataSource));
    }

    /**
     * 获取分页和排序后的数据列表
     *
     * @param <K> 泛型返回实体类型，例如 User、Student ...
     * @param queryListSql 查询列表的sql语句
     * @param paramSource 查询参数
     * @param rowMapper 字段映射，针对实体类，例如 User、Student ...
     * @return 分页和排序后的实体类数组
     */
    public <K> List<K> getPagedList(String queryListSql, MapSqlParameterSource paramSource, RowMapper<K> rowMapper) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());

        StringBuilder pageSql = new StringBuilder(queryListSql);

        pageSql.append(" ORDER BY :inOrderBy :inSort");
        if(!orderBy.isEmpty()){
            paramSource.addValue("inOrderBy", orderBy);
        }
        paramSource.addValue("inSort", pageDomain.getIsAsc());

        if(pageNum < 1) {
            pageNum = 1;
        }

        if(pageSize < 10) {
            pageSize = 10;
        }

        pageSql.append(" LIMIT :inStartIdx,:inPageSize");

        Integer startIndex = (pageNum-1) * pageSize;
        paramSource.addValue("inStartIdx", startIndex);
        paramSource.addValue("inPageSize", pageSize);

        return this.queryForList(pageSql.toString(), paramSource, rowMapper);
    }

    /**
     * 获取查询的总数，将分页的数据封装到列表返回体中
     *
     * @param pagedList 已分页后的列表数据
     * @param queryTotalSql 查询总数的sql语句，例如：SELECT COUNT(1) FROM aaa WHERE x=x
     * @param paramSource 查询参数
     * @return TableDataInfo分页返回体
     */
    public TableDataInfo getPagedResult(List<?> pagedList, String queryTotalSql, SqlParameterSource paramSource) {

		long total = this.getTotalRows(queryTotalSql, paramSource);

        TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(HttpStatus.SUCCESS);
        rspData.setTotal(total);
        rspData.setRows(pagedList);
        return rspData;
    }

    /**
     * 查询记录总条数
     * @param inSql sql语句，如：SELECT COUNT(1) FROM xxx WHERE xx=1;
     * @param paramSource 查询参数
     * @return 总数
     */
    public long getTotalRows(String inSql, SqlParameterSource paramSource) {
        return namedJdbc.queryForObject(inSql, paramSource, Long.class).longValue();
    }

    /**
     * 返回指定对象
	 * @param inSql 传入的sql语句
	 * @param paramSource 查询参数
	 * @param rowMapper 字段映射，针对实体类，例如 User、Student ...
	 * @return 实体类对象
     */
    public <K> K queryForObject(String inSql, SqlParameterSource paramSource, RowMapper<K> rowMapper) {
        try {
            return namedJdbc.queryForObject(inSql, paramSource, rowMapper);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * 返回指定对象
	 * @param inSql 传入的sql语句
	 * @param paramSource 查询参数
	 * @param targetClass 如Long，Integer...
	 * @return 实体类对象
     */
    public <K> K queryForObject(String inSql, SqlParameterSource paramSource, Class<K> targetClass) {
        try {
            return namedJdbc.queryForObject(inSql, paramSource, targetClass);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * 返回对象数据集
	 * @param inSql 传入的sql语句
	 * @param paramSource 查询参数
	 * @param rowMapper 针对实体类，例如 User、Student ...
	 * @return 实体类对象list集合
     */
    public <K> List<K> queryForList(String inSql, SqlParameterSource paramSource, RowMapper<K> rowMapper) {
    	List<K> list = null;
    	try {
			list = namedJdbc.query(inSql, paramSource, rowMapper);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		if (list == null) {
			list = new ArrayList<K>();
		}
        return list;
    }

    /**
     * 返回数据集
	 * @param inSql 传入的sql语句
	 * @param paramSource 查询参数
	 * @return Map<String, Object>列表
     */
    public List<Map<String, Object>> queryForList(String inSql, SqlParameterSource paramSource) {
    	List<Map<String, Object>> list = null;
    	try {
			list = namedJdbc.queryForList(inSql, paramSource);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		if (list == null) {
			list = new ArrayList<Map<String, Object>>();
		}
        return list;
    }

    /**
	 * 批量insert,update,delete操作
	 * @param sql sql语句必须固定
	 * @param paramSourceList 参数数组
	 * @return int列表，1：成功 0：失败
	 */
	public int[] batchUpdate(String sql, SqlParameterSource[] paramSourceList) {
		try {
			return namedJdbc.batchUpdate(sql, paramSourceList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return new int[]{0};
	}

    public NamedParameterJdbcTemplate getNamedJdbc() {
        return namedJdbc;
    }

    public void setNamedJdbc(NamedParameterJdbcTemplate namedJdbc) {
        this.namedJdbc = namedJdbc;
    }
}
