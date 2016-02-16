# 快速入门 #

如果您使用Eclipse，请查看[快速入门（Eclipse开发环境）](http://#)。

## 准备运行环境 ##

Rexdb的运行环境需要满足以下要求：

1. JDK1.5及以上版本；

同时，您需要安装好相关数据库，并获取该数据库对应的jdbc驱动包，并了解该数据库的驱动类和URL格式。如果您不了解，可以参考[常见数据库的JDBC驱动、驱动类及URL格式](http://#)。

准备就绪后，将rexdb-1.0.0.jar及数据库jdbc驱动包拷贝至环境变量classpath路径中。以Mysql为例，这时您的classpath中应有以下2个jar包：
> rexdb-1.0.0.jar<br/>
> mysql-connector-java-5.1.26-bin.jar（或其它的Mysql驱动包）

您还可以在classpath中增加其它第三方包，以启用更多功能，请参见[附录3：Rexdb可选配的第三方包](#f3)。

## 编写全局配置文件 ##
Rexdb依赖一个全局配置文件，在初始化时会自动加载该配置，并初始化连接池、日志、监听等模块。

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

## 创建一个测试表 ##

配置完成后，就可以执行SQL了。为了后续方便演示，我们首先调用接口创建一张测试用表。

编写一个Java类，名称为**TestCreate.java**，内容如下（以Mysql为例）：
    
    import org.rex.DB;
    import org.rex.db.exception.DBException;
    
    public class TestCreate {
    	public static void main(String[] args) throws DBException {
    		String sql = "CREATE TABLE REX_TEST (ID int(11) NOT NULL, NAME varchar(30) NOT NULL, CREATE_TIME time NOT NULL)";
    		DB.update(sql);//执行SQL
    		System.out.println("table created.");
    	}
    }

org.rex.DB类是Rexdb的接口类，提供数据库查询、更新、调用、事物等功能。update(String sql)是该类的一个方法，用于在数据库中执行一条更新SQL。

接下来编译并执行该类：

    javac TestCreate.java
	java TestCreate

如果一切顺利，控制台将输出以下语句。

    table created.

此时，使用查询工具连接数据库，可以确认表REX_TABLE已经被创建。

请注意，如果数据库配置错误，如地址无法连接、密码错误等。在执行该类时，将会有若干秒的等待，之后才会输出错误信息。这是由于Rexdb内置连接池具有重试机制，会在一定间隔内，反复尝试几次连接，全部失败后才会抛出异常。这是连接池的容错策略，是正常现象。

## 执行插入/更新/删除SQL ##

接下来调用Rexdb的接口，在数据库中执行插入操作。编写类TestUpdate，内容如下：

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

DB类的update(String sql, Object[] parameterArray)方法用于执行一个带有预编译参数的更新SQL。其中，parameterArray参数是一个数组，数组中的元素按顺序对应SQL语句中的“?”标记。Rexdb将按照顺序对预编译参数进行赋值，并执行SQL。

编译并执行该类后，控制台将输出：

    1 row inserted.

除Object[]可以作为预编译参数外，Rexdb还内置了一个预编译参数的封装类*org.rex.db.Ps*，它具有丰富的操作接口，可以用于封装、传递预编译参数，它可以指定字段类型、按照下标赋值，还可以为存储过程调用设置输出、输入输出参数。该类的接口设计较为灵活，您可以根据实际情况选用。除此之外，Rexdb还支持Map和自定义的Java实体类作为参数。

各种类型的参数示例如下：

1）使用内置的*org.rex.db.Ps*类作为预编译参数：

    String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
    int i = DB.update(sql, new Ps(1, "test", new Date()));


2）使用Map作为预编译参数：

	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
	
	Map prameters = new HashMap();
	prameters.put("id", 1);
	prameters.put("name", "test");
	prameters.put("createTime", new Date());
	
	int i = DB.update(sql, prameters);

3）使用实体类作为预编译参数。首先我们编写一个数据表对应的实体类：

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

然后使用该类作为参数，在数据库中执行插入操作：

	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
	RexTest rexTest = new RexTest(1, "test", new Date());
	int i = DB.update(sql, rexTest);

请注意，在使用Map类、Java实体类作为预编译参数时，SQL语句中的预编译参数标记不再是“?”，而是被“#{*参数名称*}”取代，Rexdb在执行时，会根据“#{ }”标记中的*参数名称*查找Map、实体类中对应的属性值。数据库字段名称和Java对象参数名称对应规则如下所示：

    数据库字段名称		Map.Entry.key、实体类属性名称
	ID					id
	NAME				name
	CREATE_TIME			createTime

在使用实体类作为预编译参数时，还需要额外注意的是，实体类**必须**满足如下条件，才能被Rexdb正常调用：

- 类必须是可以访问的
- 必须有一个无参的构造函数（启用动态字节码选项时需要调用）
- 参数必须有标准的getter方法

DB.update接口总体设置如下图所示：

![](resource/quick-start-update.png)

## 查询多行记录 ##

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

如果您希望查询出符合条件的实体类，可以使用如下代码（以数组做参数为例）：

	String sql = "SELECT * FROM REX_TEST limit ?";
	RexTest rexTest = DB.get(sql, new Object[] { 1 }, RexTest.class);


如果您希望执行分页查询，并查询出实体类，可以使用如下代码：

	String sql = "SELECT * FROM REX_TEST";
	List<RexTest> list = DB.getList(sql, RexTest.class, 1, 1);

接口*getList(String sql, Class resultClass, int offset, int rows)*根据预设的offset和rows参数查询相应的数据库记录。Rexdb在执行查询时，会根据数据库类型，对SQL进行相应的封装，例如，Mysql中，实际执行的SQL语句为：

    SELECT * FROM REX_TEST limit ?, ?



除示例中的接口外，Rexdb对每一类查询都提供了丰富的接口。接口总体设计如下所示。

1）如果您没有编写结果集对应的实体类，可以使用图示中的参数组合查询出包含RMap的List对象：

![](resource/quick-start-getmaplist.png)

2）如果您已经编写了结果集对应的实体类，则只需要在上述接口中增加一个*实体类.class*参数，即可查询出包含实体类的List，如图所示：

![](resource/quick-start-getlist.png)

## 查询单行记录 ##

如果您只希望查询一个实体类，可以使用如下接口：

	String sql = "SELECT * FROM REX_TEST limit 1";
	RexTest rexTest = DB.get(sql, RexTest.class);

此时，请确保您的SQL只能查询出1条，或者0条记录。当查询出的记录数超过1调试，Rexdb将会抛出异常信息。

## 事物 ##

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

## 调用函数和存储过程 ##

Rexdb支持函数和存储过程调用，可以处理输入、输出、输入输出参数和返回结果。

例如，Mysql中有如下存储过程：
	
	CREATE PROCEDURE `proc` ()  BEGIN
		--do something
	END$$

可以使用Rexdb的调用接口执行该存储过程：

	DB.call("{call proc_in()}");

当存储过程有输入参数时，例如：

	CREATE PROCEDURE `proc_in` (IN `id` INT)  BEGIN
		--do something
	END$$

则可以使用数组、Map、Ps对象、实体类等作为输入参数。Ps对象为例：

	RMap result = DB.call("{call proc_in(?)}", new Ps(1));

当存在输出参数、输入输出参数时，需要使用Ps对象作为参数，并明确指定参数类型。例如，Mysql中有如下存储过程：

    CREATE PROCEDURE `proc_in_out` (IN `i` INT, OUT `s` INT)  
	BEGIN 
    	SELECT COUNT(*) INTO s FROM REX_TEST WHERE ID > i;  
    END$$

可以使用如下代码调用该存储过程，并获取输出：

	Ps ps = new Ps();
	ps.add(0);
	ps.addOutInt();

	RMap result = DB.call(sql, ps);

更多用法请参见用户手册。



## <div id="f3">附录3：可选的第三方包</div> ##

Rexdb没有必须依赖的第三方包，但在运行环境中导入如下第三方包后，可以开启更多功能：

- 日志包：可以选用[Apache log4j](http://#)、[slf4j](http://#)、[Apache log4j2](http://#)。当Rexdb在初始化时，检测到运行环境中存在以上jar包时，将自动开启日志功能。当运行环境中存在多种日志包时，Rexdb会按照顺序优先选择第1个日志服务。
- 连接池：Rexdb内置了一个连接池，同时也支持DBCP、C3P0等连接池，以及JNDI数据源。在配置数据库连接时，可以为不同的数据库指定相应的连接方式。
- 动态字节码：Rexdb支持[javassist](http://jboss-javassist.github.io/javassist/)的动态字节码功能，当运行环境中具有该jar包，并且启用了相关配置时，Rexdb将使用动态字节码方式读写Java对象。