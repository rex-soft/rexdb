import org.rex.DB;
import org.rex.db.exception.DBException;

public class TestCreate {
	public static void main(String[] args) throws DBException {
		String sql = "CREATE1 TABLE REX_TEST (ID int(11) NOT NULL, NAME varchar(30) NOT NULL, CREATE_TIME time NOT NULL)";
		DB.update(sql);
		System.out.println("table created.");
	}
}
