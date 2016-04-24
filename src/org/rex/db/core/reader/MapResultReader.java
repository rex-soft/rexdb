/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.rex.RMap;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;

/**
 * Map ResultSet reader.
 * 
 * @version 1.0, 2016-02-12
 * @since Rexdb-1.0
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
