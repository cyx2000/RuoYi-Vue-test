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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.db.parameter.NamedSqlParameterSource;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
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
     * 拼接排序和分页的sql语句，添加分页参数
     *
     * @param queryBuilder 字符串构建
     * @param inParamSource 参数
     */
    public void buildPagedSqlAndSetParameters(StringBuilder inQueryBuilder, MapSqlParameterSource inParamSource) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());

        if(StringUtils.isEmpty(orderBy)){
            if(inParamSource instanceof NamedSqlParameterSource namedSource) {
                orderBy = (String) namedSource.getDefaultOrderByStr();
            }
        }

        if(StringUtils.isNotEmpty(orderBy)) {
            inQueryBuilder.append(" ORDER BY " + orderBy);
        }

        if(pageNum < 1) {
            pageNum = 1;
        }

        if(pageSize < 10) {
            pageSize = 10;
        }

        inQueryBuilder.append(" LIMIT :inStartIdx,:inPageSize");

        Integer startIndex = (pageNum-1) * pageSize;
        inParamSource.addValue("inStartIdx", startIndex);
        inParamSource.addValue("inPageSize", pageSize);

    }

    /**
     * 获取查询的总数，设置返回体总数和错误码，列表数据需要在外部设置
     *
     * @param queryTotalSql 查询数据库总数的sql语句
     * @param paramSource 占位符的参数，只有where语句的参数
     * @return TableDataInfo分页返回体
     */
    public TableDataInfo getPagedRespInfo(final String queryTotalSql, final SqlParameterSource paramSource) {
		long total = this.getTotalRows(queryTotalSql, paramSource);

        TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(HttpStatus.SUCCESS);
        rspData.setTotal(total);
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
     * 返回对象数据集
	 * @param inSql 传入的sql语句
	 * @param paramSource 查询参数
	 * @param targetClass 例如 String，Long ...
	 * @return 对象list集合
     */
    public <K> List<K> queryForList(String inSql, SqlParameterSource paramSource, Class<K> targetClass) {
    	List<K> list = null;
    	try {
			list = namedJdbc.queryForList(inSql, paramSource, targetClass);
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
	 * 执行操作，没有参数
	 * @param sql 传入的语句 sql="insert into tables values(?,?)";
	 * @return 0:失败 1:成功
	 */
	public int execute(String sql) {
		int exc = 1;
		try {
			namedJdbc.getJdbcTemplate().execute(sql);
		} catch (Exception e) {
            throw new ServiceException(e.getMessage());
		}
		return exc;
	}

    /**
	 * 执行插入操作并返回主键
	 * @param sql 传入的语句
	 * @param paramSources  参数数组
	 * @return 返回自动增加的id号，失败则返回 -1
	 */
	public long insertAndReturnPk(String sql, SqlParameterSource paramSources) {
        try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

            namedJdbc.update(sql, paramSources, keyHolder);

            return keyHolder.getKey().longValue();
		} catch (Exception e) {
            throw new ServiceException(e.getMessage());
		}
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
            throw new ServiceException(e.getMessage());
		}
	}

    public NamedParameterJdbcTemplate getNamedJdbc() {
        return namedJdbc;
    }

    public void setNamedJdbc(NamedParameterJdbcTemplate namedJdbc) {
        this.namedJdbc = namedJdbc;
    }
}
