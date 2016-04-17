/**
 * Copyright 2016 the original author or authors.
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
package org.rex.db.dialect;

/**
 * 针对不同数据库的sql特征，进行不同的包装
 */
public interface Dialect {


	// ------------------------------------------------------------分页SQL
	public LimitHandler getLimitHandler(int rows);
	
	public LimitHandler getLimitHandler(int offset, int rows);
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql();

	// ------------------------------------------------------------版本信息
	/**
	 * 获取方言版本信息
	 * @return 方言版本
	 */
	public String getName();
}
