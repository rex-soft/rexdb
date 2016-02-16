# 快速入门 #

如果您使用Eclipse，请查看[快速入门（Eclipse开发环境）](http://#)。

## 准备运行环境 ##

Rexdb的运行环境需要满足以下要求：

1. JDK1.5及以上版本；

同时，您需要准备好相关数据库，以及该数据库的jdbc驱动包，并了解该数据库的驱动类和URL格式。如果您不了解，可以参考[常见数据库的JDBC驱动、驱动类及URL格式](http://#)。

准备就绪后，将rexdb-1.0.0.jar及数据库jdbc驱动包拷贝至环境变量classpath路径中。以Mysql为例，这时您的classpath中应有以下2个jar包：
> rexdb-1.0.0.jar<br/>
> mysql-connector-java-5.1.26-bin.jar（或其它的Mysql驱动包）

同时，Rexdb还可以选配其它第三方包，以实现更多功能，请参见[附录3：可选的第三方包](#f3)

## 编写全局配置文件 ##
Rexdb依赖一个全局配置文件，在初始化时会自动加载该配置，并初始化连接池、日志、监听等模块。<br/>
接下来在classpath根目录中新建一个文件，名称为**rexdb.xml**，内容为：

    <?xml version="1.0" encoding="UTF-8"?> 
    <!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
    <configuration>
    	<dataSource>
    		<property name="driverClassName" value="[驱动类]" />
    		<property name="url" value="[JDBC连接URL]" />
    		<property name="username" value="[数据库账户]" />
    		<property name="password" value="[数据库密码]" />
    	</dataSource>
    </configuration>

以Mysql为例，该配置文件的内容可能会是：

    <?xml version="1.0" encoding="UTF-8"?> 
    <!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
    <configuration>
    	<dataSource>
    		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
    		<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
    		<property name="username" value="root" />
    		<property name="password" value="12345678" />
    	</dataSource>
    </configuration>

请确保该文件名称为**rexdb.xml**并在classpath根目录中，Rexdb会自动查找该文件。更多的配置选项请参考[Rexdb配置](http://#)。

## 执行建表SQL ##

配置完成后，就可以执行SQL了，编写一个Java类，名称为**TestCreate.java**，内容如下（以Mysql为例）：
    
    import org.rex.DB;
    import org.rex.db.exception.DBException;
    
    public class TestCreate {
    	public static void main(String[] args) throws DBException {
    		String sql = "CREATE TABLE REX_TEST (ID int(11) NOT NULL, NAME varchar(30) NOT NULL, CREATE_TIME time NOT NULL)";
    		DB.update(sql);//执行SQL
    		System.out.println("table created.");
    	}
    }

org.rex.DB类是Rexdb的接口提供类，数据库查询、更新、调用等接口均直接调用该类即可。update(String sql)是该类的一个方法，用于在数据库中执行一条更新SQL。

接下来编译并执行该类：

    javac TestCreate.java
	java TestCreate

如果一切顺利，控制台将输出以下语句。

    table created.

使用查询工具连接数据库，可以确认表REX_TABLE已经被创建。<br/>
如果数据库配置错误，如密码错误，在执行该类时，将会在若干秒的延迟后才会输出结果。这是由于Rexdb内置的连接池具有重试机制，会反复尝试几次连接，全部失败后才会输出异常。

## 执行插入/更新/删除SQL ##

编写类TestUpdate，内容如下：

    import java.util.Date;
    
    import org.rex.DB;
    import org.rex.db.exception.DBException;
    
    public class TestUpdate {
    	public static void main(String[] args) throws DBException {
    		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
    		int i = DB.update(sql, new Object[]{1, "test", new Date()});
    		System.out.println( i + " row inserted.");
    	}
    }

DB类的update(String sql, Object[] parameterArray)方法用于执行一个带有预编译参数的更新SQL。其中parameterArray参数是一个数组，数组中的元素按顺序对应SQL语句中的“?”标记。Rexdb将按照顺序对预编译参数赋值并执行SQL。

编译并执行该类后，控制台输出：

    1 row inserted.

除Object[]可以作为预编译参数外，Rexdb还内置了一个预编译参数的封装类*org.rex.db.Ps*，并支持常见的Java对象类型，其它类型的示例如下：

1）使用内置的*org.rex.db.Ps*类作为预编译参数：

    String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
    int i = DB.update(sql, new Ps(1, "test", new Date()));

org.rex.db.Ps具有丰富的操作接口，可以指定字段类型、按照下标赋值，还可以为存储过程调用设置输出参数，或是输入输出参数。它也是Rexdb推荐使用的预编译参数封装类。

2）使用Map作为预编译参数：

	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
	
	Map prameters = new HashMap();
	prameters.put("id", 1);
	prameters.put("name", "test");
	prameters.put("createTime", new Date());
	
	int i = DB.update(sql, prameters);

使用Map类封装预编译参数时，SQL语句中的预编译参数标记不再是“?”，而是被“#{*参数名称*}”取代，Rexdb在执行时，会根据“#{ }”标记中的*参数名称*查找Map对应的属性值。

3）使用实体类作为预编译参数：

更多时候，编程人员可能更加习惯于使用实体类传递参数。首先编写一个实体类：

    import java.util.Date;
    
    public class RexTest {
    	
    	private int id;
    	private String name;
    	private Date createTime;

		public RexTest() {
		}    

    	public RexTest(int id, String name, Date createTime) {
    		this.id = id;
    		this.name = name;
    		this.createTime = createTime;
    	}
    
    	public int getId() {
    		return id;
    	}
    
    	public void setId(int id) {
    		this.id = id;
    	}
    
    	public String getName() {
    		return name;
    	}
    
    	public void setName(String name) {
    		this.name = name;
    	}
    
    	public Date getCreateTime() {
    		return createTime;
    	}
    
    	public void setCreateTime(Date createTime) {
    		this.createTime = createTime;
    	}
    }

接下来可以使用该类作为预编译参数：

	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";

	RexTest rexTest = new RexTest(1, "test", new Date());
	int i = DB.update(sql, rexTest);

使用实体类封装预编译参数时，SQL语句中的预编译参数使用“#{*参数名称*}”标记，Rexdb在执行时，会根据“#{ }”标记中的*参数名称*查找实体类对应的属性值。

请注意，在使用实体类作为预编译参数时，实体类**必须**满足如下条件，才能被Rexdb正常调用：

- 类必须是可以访问的
- 必须有一个无参的构造函数（启用动态字节码时需要使用）
- 参数必须有标准的getter方法

## 执行查询SQL ##

编写类TestQuery，该类用于查询出表REX_TEST中的所有记录：

    import java.util.List;
    
    import org.rex.DB;
    import org.rex.RMap;
    import org.rex.db.exception.DBException;
    
    public class TestQuery {
    	public static void main(String[] args) throws DBException {
    		String sql = "SELECT * FROM REX_TEST";
    		List<RMap> list = DB.getMapList(sql);
    		System.out.println(list);
    	}
    }

编译并执行后，输出结果如下：

    [{id=1, createTime=Tue Feb 16 15:05:54 CST 2016, name=test}, {id=1, createTime=Tue Feb 16 15:06:15 CST 2016, name=test}, {id=1, createTime=Tue Feb 16 15:13:41 CST 2016, name=test}]

DB类的getMapList(String sql)方法用于执行一条查询SQL，并返回一个包含有查询结果的List，其中数据库列名将被转换为Java命名风格。

其中List中的元素*org.rex.RMap*是Rexdb框架提供的封装类，它继承自*java.util.HashMap*，并额外提供了Java类型的自动转换功能，您可以方便的从该类中直接获取各种Java类型的值，而不需要自行编写类型转换代码。

如果您希望查询出实体类列表，可以使用如下代码：

	String sql = "SELECT * FROM REX_TEST";
	List<RexTest> list = DB.getList(sql, RexTest.class);

如果您希望根据条件查询列表，可以使用如下代码（以数组做参数为例）：

	String sql = "SELECT * FROM REX_TEST limit ?";
	RexTest rexTest = DB.get(sql, new Object[] { 1 }, RexTest.class);


如果您希望执行分页查询，并查询出实体类，可以使用如下代码：

	String sql = "SELECT * FROM REX_TEST";
	List<RexTest> list = DB.getList(sql, RexTest.class, 1, 1);

接口*getList(String sql, Class resultClass, int offset, int rows)*根据预设的offset和rows参数查询相应的数据库记录。Rexdb在执行查询时，会根据数据库类型，对SQL进行相应的封装，例如，Mysql中，实际执行的SQL语句为：

    SELECT * FROM REX_TEST limit ?, ?

如果您只希望查询一个实体类，可以使用如下接口：

	String sql = "SELECT * FROM REX_TEST limit 1";
	RexTest rexTest = DB.get(sql, RexTest.class);

此时，请确保您的SQL只能查询出1条，或者0条记录。当查询出的记录数超过1调试，Rexdb将会抛出异常信息。

除示例中的接口外，Rexdb对每一类查询都提供了丰富的接口，请参见开发手册。

## 启用事物 ##

编写类TestTransaction，内容如下：

    import java.util.Date;
    
    import org.rex.DB;
    import org.rex.db.Ps;
    import org.rex.db.exception.DBException;
    
    public class TestTransaction {
    	public static void main(String[] args) throws DBException {
    		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
    		DB.beginTransaction();
    		try{
    			DB.update(sql, new Ps(1, "test", new Date()));
    			DB.update(sql, new Ps(2, "test", new Date()));
    			DB.commit();
    		}catch(Exception e){
    			DB.rollback();
    		}
    	}
    }

接口beginTransaction()用于开启一个事物，commit()和rollback()分别用于提交和回滚事物。Rexdb的事物是线程级别的，事物一旦开启，将在整个用户线程中有效。

Rexdb支持Jta事物，详情请参见用户手册。

## 函数和存储过程调用 ##

Rexdb支持函数和存储过程调用，可以处理输入、输出、输入输出参数和返回结果。

例如，Mysql中已经创建了如下存储过程：

    CREATE PROCEDURE `proc_in_out` (IN `i` INT, OUT `s` INT)  
	BEGIN 
    	SELECT COUNT(*) INTO s FROM REX_TEST WHERE ID > i;  
    END$$

可以使用如下代码调用该存储过程并返回输出参数：

	Ps ps = new Ps();
	ps.add(0);
	ps.addOutInt();

	RMap result = DB.call(sql, ps);

更多用法请参见用户手册

## <div id="f3">附录3：可选的第三方包</div> ##

Rexdb没有必须依赖的第三方包，但在运行环境中导入如下第三方包后，可以开启更多功能：

- 日志包：可以选用[Apache log4j](http://#)、[slf4j](http://#)、[Apache log4j2](http://#)。当Rexdb在初始化时，检测到运行环境中存在以上jar包时，将自动开启日志功能。当运行环境中存在多种日志包时，Rexdb会按照顺序优先选择第1个日志服务。
- 连接池：Rexdb内置了一个连接池，同时也支持DBCP、C3P0等连接池，以及JNDI数据源。在配置数据库连接时，可以为不同的数据库指定相应的连接方式。
- 动态字节码：Rexdb支持[javassist](http://jboss-javassist.github.io/javassist/)的动态字节码功能，当运行环境中具有该jar包，并且启用了相关配置时，Rexdb将使用动态字节码方式读写Java对象。