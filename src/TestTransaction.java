import java.util.Date;

import org.rex.DB;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.transaction.DefaultDefinition;

public class TestTransaction {
	public static void main2(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
		DefaultDefinition definition = new DefaultDefinition();
		definition.setTimeout(10);
		definition.setIsolationLevel(DefaultDefinition.ISOLATION_READ_COMMITTED);
		
		DB.beginTransaction(definition);
		try{
			DB.update(sql, new Ps(1, "test", new Date()));
			DB.update(sql, new Ps(2, "test", new Date()));
			DB.commit();
		}catch(Exception e){
			DB.rollback();
		}
	}
	
	public static void main1(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
	DB.beginJtaTransaction();
	DB.rollbackJta();
	DB.commitJta();
	}
}
