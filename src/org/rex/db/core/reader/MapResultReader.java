package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.rex.WMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;

/**
 * 读取单条结果集，进行OR映射
 */
public class MapResultReader implements ResultReader {

	private ORUtil orUtil = new ORUtil();

	private Ps ps;
	boolean originalKey;

	private List<WMap> results;

	private int rowNum = 0;

	public MapResultReader(boolean originalKey) {
		this.results = new LinkedList<WMap>();
		this.originalKey = originalKey;
	}

	/**
	 * 创建结果集读取类，适用于普通查询
	 * 
	 * @param ps 查询参数
	 * @param originalKey 是否按照结果集原始键处理
	 * @param resultPojo
	 */
	public MapResultReader(Ps ps, boolean originalKey) {
		this(originalKey);
		this.ps = ps;
	}

	public void setPs(Ps ps) {
		this.ps = ps;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		results.add(row2Map(rs, rowNum++, ps, originalKey));
	}

	public List<WMap> getResults() {
		return results;
	}

	// -----------
	/**
	 * 将结果集转为WMap
	 * 
	 * @param rs
	 * @param rowNum
	 * @param ps
	 * @param originalKey
	 * @return
	 * @throws DBException
	 */
	private WMap row2Map(ResultSet rs, int rowNum, Ps ps, boolean originalKey) throws DBException {
		try {
			return orUtil.rs2Map(rs, originalKey);
		} catch (SQLException e) {
			throw new DBException("DB-Q10005", e);
		}
	}

}
