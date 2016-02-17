# 快速入门 #

本文档可以帮助您在最短的时间内了解Rexdb的使用方法。

## 目录 ##

- [准备运行环境](#c1)
- [全局配置文件](#c2)
- [创建一个测试表](#c3)
- [执行插入/更新/删除SQL](#c4)
- [执行批量更新](#c5)
- [查询多行记录](#c6)
- [查询单行记录](#c7)
- [启用事物](#c8)
- [调用函数和存储过程](#c9)
- [定义多个数据源](#c10)
- [使用其它数据源或JNDI](#c11)
- [更多功能](#c12)
- [附录：Rexdb可选配的第三方包](#c13)

## <div id="c1">准备运行环境</div> ##

Rexdb的运行环境需要满足以下要求：

1. JDK1.5及以上版本
2. 支持JDBC驱动

首先，您需要安装好数据库，并获取相应的jdbc驱动包。准备就绪后，将rexdb-1.0.0.jar及数据库jdbc驱动包拷贝至环境变量classpath路径中。

以Mysql为例，这时您的classpath中应有以下2个jar包：
> rexdb-1.0.0.jar（或更高版本）<br/>
> mysql-connector-java-5.1.26-bin.jar（或其它版本的驱动）

除以上Jar包外，您还可以在classpath中增加其它jar包，以启用Rexdb的更多功能。例如，增加Apache Log4j包后，Rexdb可以自动调用logger接口记录日志；增加jboss javassist包后，可以启用Rexdb的动态字节码功能，以获取更高的查询性能。详情请参见[附录：Rexdb可选配的第三方包](#f1)。

准备就绪后，就可以使用Rexdb操作数据库了。

## <div id="c2">全局配置文件</div> ##
在调用Rexdb的接口之前，首先需要创建配置文件。Rexdb在初始化时会加载该配置，并创建连接池、方言、日志、监听等模块。

在classpath根目录中新建一个文件，名称为**rexdb.xml**，内容为：

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

请确保该文件名称为**rexdb.xml**，并放置在环境变量classpath的根目录中，Rexdb会自动查找该文件。如果您希望将该文件放置在其它目录，则需要手动调用Rexdb的接口，以完成初始化工作。详情请参考[Rexdb用户手册](http://#)。

后续章节的内容如无特殊说明，均使用Mysql数据库。

## <div id="c3">创建一个测试表</div> ##

为了便于后续程序的演示，我们首先使用Rexdb创建表*REX_TEST*，该表包含3个字段：

> ID int(11) NOT NULL<br/>
> NAME varchar(30) NOT NULL<br/>
> CREATE_TIME time NOT NULL

编写一个Java类，名称为**TestCreate.java**，内容如下：
    
    import org.rex.DB;
    import org.rex.db.exception.DBException;
    
    public class TestCreate {
    	public static void main(String[] args) throws DBException {
    		String sql = "CREATE TABLE REX_TEST (ID int(11) NOT NULL, NAME varchar(30) NOT NULL, CREATE_TIME time NOT NULL)";
    		DB.update(sql); //执行SQL
    		System.out.println("table created.");
    	}
    }

*org.rex.db.DB*类是Rexdb的对外接口类，它提供了查询、更新、调用、事物等操作接口。*DB.update(String sql)*是该类的一个方法，用于在数据库中执行一条插入/更新/删除SQL。

接下来使用命令行编译，并执行该类：

    javac TestCreate.java
	java TestCreate

如果一切顺利，控制台将输出以下语句。

    table created.

此时，使用查询工具连接数据库，可以确认表*REX_TABLE*已被创建。

请注意，如果数据库配置错误，如地址无法连接、密码错误等。在执行该类时，将会有若干秒的等待，之后才会输出错误信息。这是由于Rexdb内置连接池具有重试机制，会在一定间隔内，反复尝试几次连接，全部失败后才会抛出异常。这是连接池的容错策略，是正常现象。

## <div id="c4">执行插入/更新/删除SQL</div> ##

在Rexdb中，数据库的插入/更新/删除操作使用的是同一个接口。我们接下来以插入为例，演示接口的使用方法。

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

DB.update(String sql, Object[] parameterArray)方法用于执行一个带有预编译参数的插入/更新/删除SQL。其中，parameterArray参数是一个数组，数组中的元素按照顺序对应SQL语句中的“?”标记。Rexdb将按照顺序从数组中取值，并调用JDBC相关接口赋值，然后执行SQL。

编译并执行该类后，控制台将输出：

    1 row inserted.

除Object[]数组可以作为执行SQL的参数外，Rexdb还内置了一个类*org.rex.db.Ps*，它拥有丰富的操作接口，可以用于封装预编译参数。它可以指定字段类型、按照下标赋值，还可以为存储过程调用声明输出、输入输出参数，您可以根据实际情况选用。除此之外，Rexdb还支持Map和自定义的Java实体类作为执行SQL的参数。

除数组外，各种类型的参数调用示例如下：

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

3）使用实体类作为预编译参数。首先需要编写一个数据表对应的实体类：

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

然后使用该类作为执行SQL的参数：

	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
	RexTest rexTest = new RexTest(1, "test", new Date());
	int i = DB.update(sql, rexTest);

请注意，在使用Map类、Java实体类作为预编译参数时，SQL语句中的预编译参数标记不再是JDBC标准的“?”，而是被“#{*参数名称*}”取代，Rexdb在执行时，会根据标记中的*参数名称*查找Map、实体类中对应的属性值。其中，数据库字段名称和Java对象参数名称对应规则如下所示：

    数据库字段名称		Map.Entry.key、实体类属性名称
	ID					id
	NAME				name
	CREATE_TIME			createTime

还需要额外注意的是，在使用实体类作为预编译参数时，实体类**必须**满足如下条件，才能被Rexdb正常调用：

- 类是可以访问的
- 可以使用无参的构造函数创建类实例（启用动态字节码选项时需要调用）
- 参数需要有标准的getter方法

为便于理解，我们总结了DB.update接口SQL和参数的组合方式，如下图所示：

![](resource/quick-start-update.png)

## <div id="c5">执行批量更新</div> ##

当插入多行记录时，使用批量接口可以获得更高的执行效率。

编写类TestUpdateBatch，内容如下：

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

在上面的类中，以*org.rex.db.Ps*数组作为批量插入的参数，数组中的每个元素都代表一条记录。执行后*DB.batchUpdate(String sql, Ps[] pss)*，数据库将写入10条记录。

除Ps数组外，Rexdb还支持对象二维数组、Map数组、实体类数组、List作为参数，或者不带参数的多条SQL语句批量执行。使用不同类型的参数时，对应的SQL的写法与单条记录的插入/更新/删除相同，组合关系如图所示：

![](resource/quick-start-batchupdate.png)

## <div id="c6">查询多行记录</div> ##

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

接口*DB.getList(String sql, Class resultClass, int offset, int rows)*根据预设的offset和rows参数查询相应的数据库记录，offset参数表示行偏移，rows参数表示查询行数。Rexdb在执行查询时，会根据数据库类型，自动选择相应的方言，并对SQL进行相应的封装，例如，Mysql中，实际执行的SQL语句为：

    SELECT * FROM REX_TEST limit ?, ?

Rexdb内置了如下数据库方言：

- DB2
- Derby
- DM
- H2
- HSQL
- MySQL
- Oracle
- PostgreSQL
- SQLServer

如果您使用的的数据库不在列表中，可以自行实现一个方言类，并在配置数据源时指定该类。详情请查看Rexdb用户手册。

除示例中调用的接口外，Rexdb对每一类查询都提供了丰富的接口。接口设置如下所示。

1）如果您没有编写结果集对应的实体类，可以使用图示中的参数组合查询出包含RMap的List对象：

![](resource/quick-start-getmaplist.png)

2）如果您已经编写了结果集对应的实体类，则只需要在上述接口中增加一个*实体类.class*参数，即可查询出包含实体类的List，如图所示：

![](resource/quick-start-getlist.png)

## <div id="c7">查询单行记录</div> ##

Rexdb提供了一系列查询单行记录的接口，例如，如果您希望只查询一行记录，并获取实体类时，可以使用如下接口：

	String sql = "SELECT * FROM REX_TEST limit 1";
	RexTest rexTest = DB.get(sql, RexTest.class);

当没有编写与结果集对应的实体类时，则可以直接查询出一个RMap对象：

	String sql = "SELECT * FROM REX_TEST limit 1";
	RMap rexTest = DB.get(sql);

如果希望直接获取某一行中的某个字段值时，可以直接从RMap中取值。例如查询某张表的总记录数，可以直接调用RMap中的getInt(String key)接口，获取int类型的值，例如：

	String sql = "SELECT count(*) as COUNT FROM REX_TEST";
	int count = DB.get(sql).getInt("count");

在调用单行记录查询接口时，请确保您的SQL只能查询出0条或者1条。当查询出的记录数超过1行时，Rexdb无法确定您需要哪一行，将会抛出异常信息。

单行记录查询接口的SQL和参数组合设置如下：

1）如果您没有编写结果集对应的实体类，可以直接查询RMap对象：

![](resource/quick-start-getmap.png)

2）如果您已经编写了结果集对应的实体类，则只需要在上述接口中增加一个*实体类.class*参数，即可实体类的实例，如图所示：

![](resource/quick-start-get.png)

## <div id="c8">启用事物</div> ##

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

接口*DB.beginTransaction()*用于开启一个基于数据库连接的普通事物，*DB.commit()*和*DB.rollback()*分别用于提交和回滚事物。Rexdb的事物是线程级别的，事物一旦开启，将在整个用户线程中有效。

如果要使用Jta事物，请使用如下接口：

	DB.beginJtaTransaction();
	DB.rollbackJta();
	DB.commitJta();

## <div id="c9">调用函数和存储过程</div> ##

Rexdb支持函数和存储过程调用，可以处理输入、输出、输入输出参数和返回结果。

例如，Mysql中有如下存储过程：
	
	CREATE PROCEDURE `proc` ()  BEGIN
		--do something
	END$$

可以使用Rexdb的DB.call(String sql)接口调用该存储过程：

	DB.call("{call proc_in()}");

当存储过程有输入参数时，例如：

	CREATE PROCEDURE `proc_in` (IN `id` INT)  BEGIN
		--do something
	END$$

则可以使用数组、Map、Ps对象、实体类等作为输入参数调用存储过程。以Ps对象为例：

	RMap result = DB.call("{call proc_in(?)}", new Ps(1));

当存在输出参数、输入输出参数时，需要使用*org.rex.db.Ps*对象封装参数。例如，Mysql中有如下存储过程：

    CREATE PROCEDURE `proc_in_out` (IN `i` INT, OUT `s` INT)  
	BEGIN 
    	--do something
    END$$

可以使用如下代码调用该存储过程，并获取输出：

	Ps ps = new Ps();
	ps.add(0);
	ps.addOutInt();//声明为输出参数

	RMap result = DB.call(sql, ps);

输出、输入输出参数将封装在RMap对象中，键为“*out_参数序号*”。为方便调用后取值，也可以在声明输出参数时设置一个别名，例如：

    ps2.addInOut("name", 1);//将第1个参数声明为输入输出参数，且别名为name

有返回值的存储过程，在调用后也会被解析处理，封装在返回的RMap对象中，且键为“*result_返回值序号*”。

![](resource/quick-start-call.png)

## <div id="c10">定义多个数据源</div> ##

如果您的应用程序需要使用多个数据库，可以在Rexdb全局配置文件**rexdb.xml**中配置多个数据源，例如：

    <?xml version="1.0" encoding="UTF-8"?> 
    <!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
    <configuration>
    	<dataSource>
    		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
    		<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
    		<property name="username" value="root" />
    		<property name="password" value="12345678" />
    	</dataSource>
		<dataSource id="oracleDs">
			<property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
			<property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:orcl" />
			<property name="username" value="rexdb" />
			<property name="password" value="12345678" />
		</dataSource>
    </configuration>

上面的配置文件定义了2个数据源，分别是Mysql和Oracle数据库。其中，为Oracle数据源定义了*id="oracleDs"*的属性，在调用*org.rex.db.DB*的接口时可以通过参数指定使用该数据源；未定义“*id*”属性的是Rexdb的默认数据源，一个应用中只能定义1个默认数据源。

在调用接口时，可以使用如下方法使用数据源：

	String sql = "SELECT 1 FROM DUAL";
	RMap map = DB.getMap("oracleDs", sql);

*org.rex.db.DB*类的更新、查询、调用、事物等接口均可以指定数据源，只需要将接口的第一个参数设置为配置文件中声明的“*id*”即可。

## <div id="c11">使用其它数据源或JNDI</div> ##

如果您希望使用其它数据源，例如DBCP，再配置好数据源的运行环境后，可以在配置文件中进行如下定义：

    <?xml version="1.0" encoding="UTF-8"?> 
    <!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
    <configuration>
    	<dataSource class="org.apache.commons.dbcp.BasicDataSource">
    		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
    		<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
    		<property name="username" value="root" />
    		<property name="password" value="12345678" />
    	</dataSource>
    </configuration>

全局配置文件中/configuration/dataSource节点的*class="org.apache.commons.dbcp.BasicDataSource"*属性定义了数据源的实现类，其子节点*property*则是该数据源要求配置的属性。Rexdb在初始化数据源时，会创建调用实现类的setter方法对其赋值。

如果您希望使用JNDI，则可以进行如下配置：

    <?xml version="1.0" encoding="UTF-8"?> 
    <!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
    <configuration>
    	<dataSource jndi="java:comp/env/mysqlJNDI"/>
    </configuration>

其中，JNDI名称可能会根据您的应用运行容器而有所不同，如果出现找不到JNDI的错误时，请检查容器的名称定义规则。

## <div id="c12">更多功能</div> ##

除本文档中提及的功能外，Rexdb还有更多用法，请参见Rexb用户手册。

## <div id="f1">附录：Rexdb可选配的第三方包</div> ##

Rexdb没有必须依赖的第三方包，但在运行环境中导入如下第三方包后，可以开启更多功能：

- 日志包：可以选用Apache log4j、slf4j、Apache log4j2。当Rexdb在初始化时，检测到运行环境中存在以上jar包时，将自动开启日志功能。当运行环境中存在多种日志包时，Rexdb会按照顺序优先选择第1个日志服务。
- 连接池：Rexdb内置了一个连接池，同时也支持DBCP、C3P0等连接池，以及JNDI数据源。在配置数据库连接时，可以为不同的数据库指定相应的连接方式。
- 动态字节码：Rexdb支持javassist的动态字节码功能，当运行环境中具有该jar包，并且启用了相关配置时，Rexdb将使用动态字节码方式读写Java对象。