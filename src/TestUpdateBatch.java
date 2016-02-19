import java.util.Date;

import org.rex.DB;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class TestUpdateBatch {
	public static void main(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
		Ps[] pss = new Ps[10];
		for (int i = 0; i < 10; i++)
			pss[i] = new Ps(i, "name", new Date());
		DB.batchUpdate(sql, pss);
	}
}
