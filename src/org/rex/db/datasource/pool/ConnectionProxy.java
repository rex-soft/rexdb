package org.rex.db.datasource.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TimerTask;

public class ConnectionProxy implements InvocationHandler {
	
	private ArrayList<Statement> _openStatements;
	private volatile boolean _isClosed;
	private SimpleConnectionPool _parentPool;

	protected Connection delegate;

	private volatile boolean _forceClose;
	private long _creationTime;
	private long _lastAccess;

	private TimerTask _leakTask;

	public IConnectionProxy bind(Connection conn) {
		this.delegate = conn;
		__init();
		
		IConnectionProxy proxyConnection = (IConnectionProxy)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { IConnectionProxy.class }, this);
		return proxyConnection;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method[] overrideMethods = ConnectionProxy.class.getMethods();
		for (int i = 0; i < overrideMethods.length; i++) {
			if (methodEqueals(overrideMethods[i], method)) {
				return overrideMethods[i].invoke(this, args);
			}
		}
		return method.invoke(delegate, args);
	}
	
	private boolean methodEqueals(Method m1, Method m2){
        if (m1.getName() == m2.getName()) {
            if (!m1.getReturnType().equals(m2.getReturnType()))
                return false;
            Class<?>[] params1 = m1.getParameterTypes();
            Class<?>[] params2 = m2.getParameterTypes();
            if (params1.length == params2.length) {
                for (int i = 0; i < params1.length; i++) {
                    if (params1[i] != params2[i])
                        return false;
                }
                return true;
            }
        }
        return false;
	}

	public void unregisterStatement(Object statement) {
		if (!_isClosed) {
			_openStatements.remove(statement);
		}
	}

	public long getCreationTime() {
		return _creationTime;
	}

	public long getLastAccess() {
		return _lastAccess;
	}

	public void markLastAccess() {
		this._lastAccess = System.currentTimeMillis();
	}

	public void unclose() {
		_isClosed = false;
	}

	public boolean isBrokenConnection() {
		return _forceClose;
	}

	public void setParentPool(SimpleConnectionPool parentPool) {
		this._parentPool = parentPool;
	}

	public SQLException checkException(SQLException sqle) {
		// String sqlState = sqle.getSQLState();
		// _forceClose |= (sqlState != null && (sqlState.startsWith("08") ||
		// SQL_ERRORS.contains(sqlState)));

		return sqle;
	}

	private void __init() {
		_openStatements = new ArrayList<Statement>(64);
		_creationTime = _lastAccess = System.currentTimeMillis();
	}

	protected void checkClosed() throws SQLException {
		if (_isClosed) {
			throw new SQLException("Connection is closed");
		}
	}

	public final Connection getDelegate() {
		return delegate;
	}

	// ----------------implements
	public void close() throws SQLException {
		if (!_isClosed) {
			_isClosed = true;
			if (_leakTask != null) {
				_leakTask.cancel();
				_leakTask = null;
			}

			try {
				for (int i = _openStatements.size() - 1; i >= 0; i--) {
					_openStatements.get(i).close();
				}
			} catch (SQLException e) {
				throw checkException(e);
			} finally {
				_openStatements.clear();
				_parentPool.releaseConnection(this);
			}
		}
	}

	public final void __close() throws SQLException {
		delegate.close();
	}

	public boolean isClosed() throws SQLException {
		return _isClosed;
	}

	public Statement createStatement() throws SQLException {
		checkClosed();
		try {
			Statement statement = delegate.createStatement();
			_openStatements.add(statement);

			return statement;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		checkClosed();
		try {
			Statement statement = delegate.createStatement(resultSetType, resultSetConcurrency);
			_openStatements.add(statement);

			return statement;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkClosed();
		try {
			Statement statement = delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
			_openStatements.add(statement);

			return statement;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		checkClosed();
		try {
			CallableStatement statement = delegate.prepareCall(sql);
			_openStatements.add(statement);

			return statement;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		checkClosed();
		try {
			CallableStatement statement = delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
			_openStatements.add(statement);

			return statement;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkClosed();
		try {
			CallableStatement statementProxy = delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql, autoGeneratedKeys);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql, columnIndexes);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		checkClosed();
		try {
			PreparedStatement statementProxy = delegate.prepareStatement(sql, columnNames);
			_openStatements.add(statementProxy);

			return statementProxy;
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

	public boolean isValid(int timeout) throws SQLException {
		if (_isClosed) {
			return false;
		}

		try {
			return delegate.isValid(timeout);
		} catch (SQLException e) {
			throw checkException(e);
		}
	}

}
