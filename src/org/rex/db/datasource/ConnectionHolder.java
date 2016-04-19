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
package org.rex.db.datasource;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

import org.rex.db.exception.DBRuntimeException;

/**
 * Holds connections in ThreadLocal.
 * 
 * @version 1.0, 2016-02-02
 * @since Rexdb-1.0
 */
public class ConnectionHolder{

	private final Connection connection;

	/**
	 * Seconds to live
	 */
	private int liveSeconds = -1;
	
	/**
	 * Transaction dead time
	 */
	private Date deadline;
	
	public ConnectionHolder(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * Sets timeout in seconds
	 */
	public void setTimeoutInSeconds(int seconds) {
		liveSeconds = seconds;
		setTimeoutInMillis(seconds * 1000);
	}

	private void setTimeoutInMillis(long millis) {
		this.deadline = new Date(System.currentTimeMillis() + millis);
	}

	/**
	 * Returns transaction dead time
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * Returns seconds to live
	 */
	public int getTimeToLiveInSeconds() {
		double diff = ((double) getTimeToLiveInMillis()) / 1000;
		return (int) Math.ceil(diff);
	}

	private long getTimeToLiveInMillis() {
		long liveInMillis = deadline.getTime() - System.currentTimeMillis();
		if(liveInMillis <= 0){
			throw new DBRuntimeException("T0015", liveSeconds, new DecimalFormat("0.00").format((liveSeconds - liveInMillis/1000)), connection.hashCode());
		}
		
		return liveInMillis;
	}

}
