package db;

import org.rex.DB;
import org.rex.db.exception.DBException;

public class TestTransaction extends Base {

	// --------------
	public static void main(String[] args) throws Exception {
		TestTransaction transaction = new TestTransaction();
//		transaction.execute();
		transaction.executeJta();
	}

	public void execute() throws Exception {

		TestInsert insert = new TestInsert();
		
		DB.beginTransaction();
		try {
			insert.executeELSqlWithBean();
			insert.executePreparedSqlWithPs();
			
			DB.commit();
		} catch (Exception e) {
			DB.rollback();
			throw e;
		}
	}
	
	public void executeJta() throws DBException{
		DB.beginJtaTransaction();
		
		
	}
}
