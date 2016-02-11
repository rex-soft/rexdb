package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.rex.RMap;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;

/**
 * 读取单条结果集，进行OR映射
 */
public class MapResultReader implements ResultReader {

	private ORUtil orUtil = new ORUtil();

	private List<RMap> results;

	private int rowNum = 0;

	public MapResultReader() {
		this.results = new LinkedList<RMap>();
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		results.add(row2Map(rs, rowNum++));
	}

	public List<RMap> getResults() {
		return results;
	}

	// -----------private methods
	private RMap<String, ?> row2Map(ResultSet rs, int rowNum) throws DBException {
		return orUtil.rs2Map(rs);
	}

}
