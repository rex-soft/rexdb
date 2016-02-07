package org.rex.db.sql;

import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.sql.analyzer.DynamicStatementSetter;
import org.rex.db.sql.analyzer.MapStatementSetter;
import org.rex.db.sql.analyzer.ReflectedStatementSetter;
import org.rex.db.sql.analyzer.StatementSetter;
import org.rex.db.sql.analyzer.TokenAnalyzer;

/**
 * 处理带有标记的参数 
 */
public class SqlParser {
	
	//----------setting
	/**
	 * 是否启用动态字节码编译
	 * @throws DBException 
	 */
	private static boolean isDynamicClass() throws DBException{
		return Configuration.getCurrentConfiguration().isDynamicClass();
	}
	
	//---------delegate
	/**
	 * 用于解析SQL语句
	 */
	private TokenAnalyzer tokenAnalyzer;
	
	public SqlParser(String sql, Object bean) throws DBException{
		StatementSetter statementSetter = null;
		if(bean instanceof Map){
			statementSetter = new MapStatementSetter(sql, (Map<?,?>)bean);
		}else{
			statementSetter = isDynamicClass() ? new DynamicStatementSetter(sql, bean) : new ReflectedStatementSetter(sql, bean);
		}
		
		this.tokenAnalyzer = new TokenAnalyzer(sql, statementSetter);
		tokenAnalyzer.parse();
	}
	
	/**
	 * 获取解析后的SQL
	 * @return
	 */
	public String getParsedSql(){
		return tokenAnalyzer.getParsedSql();
	}
	
	/**
	 * 获取解析后的Ps对象
	 * @return
	 */
	public Ps getParsedPs(){
		return tokenAnalyzer.getParsedPs();
	}
	
}
