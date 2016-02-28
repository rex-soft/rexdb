<style type="text/css">
	table {
		font-family: verdana,arial,sans-serif;
		font-size:12px;
		color:#333333;
		border-width: 1px;
		border-color: #666666;
		border-collapse: collapse;
		width: 100%;
	}
	table th {
		border-width: 1px;
		padding: 8px;
		border-style: solid;
		border-color: #666666;
		background-color: #dedede;
	}
	table td {
		border-width: 1px;
		padding: 8px;
		border-style: solid;
		border-color: #666666;
		background-color: #ffffff;
	}
</style>

# 用户手册 #

## 概述 ##
### 简介 ###

Rexdb是一款使用Java语言编写的，开放源代码的持久层框架。它具有管理数据源、执行SQL、调用函数和存储过程、处理事务等功能。

使用Rexdb时，不需要像JDBC一样编写繁琐的代码，也不需要编写数据表映射文件，只要将SQL和Java对象等参数传递至框架接口，即可获取需要的结果。

Rexdb的官方网站地址为：[http://db.rex-soft.org](http://db.rex-soft.org)

### 功能 ###

Rexdb具有如下功能：

- 数据库查询、更新、批量处理、函数和存储过程调用、事物和JTA事物等；
- ORM映射，可以使用数组、Map、Java对象作为预编译参数，也可以自动将结果集转换为Map、Java对象；
- 数据源管理，拥有内置的连接池和数据源，支持第三方数据源和JNDI；
- 数据库方言，自动封装分页查询和常用函数，支持Oracle、DB2、SQL Server、Mysql、达梦等数据库；
- 支持对框架初始化、SQL执行、事物等事件的监听；
- 统一的异常管理、异常信息的国际化支持等；

### 一般使用过程 ###

Rexdb的使用较为简单，一般遵循如下过程即可：

1. 下载Rexdb的jar包，并拷贝至开发/运行环境；
2. 在classpath中增加全局配置文件**rexdb.xml**，并在文件中配置数据库连接；
3. 在Java程序中直接调用Rexdb的接口操作数据库；


## 下载和安装 ##

Rexdb的官方网站提供了下载衔接，下载并解压后，可以得到编译好的jar包和全局配置文件的示例：

- rexdb-1.0.0.jar（或其它版本）
- rexdb.xml

**rexdb-1.0.0.jar**（或其它版本）是运行Rexdb必须的包，请确保它在开发/运行环境的classpath中。例如，当您在JavaEE Web应用中使用Rexdb时，需要将该文件拷贝至应用根目录下的“*/WEB-INF/lib*”中。

由于Rexdb直接调用JDBC的接口，所以您还需要在classpath中设置好数据库的驱动。如果要使用Rexdb的扩展功能，还需在运行环境中增加其它jar包。具体请参考[扩展](http://#)。

**rexdb.xml**是Rexdb的全局配置文件，默认放置在开发/运行环境的classpath中。例如，在JavaEE Web应用中，该文件应当放置在应用根目录下的“*/WEB-INF/classes*”中。

如果需要将该配置文件放置在其它位置，需要编写程序加载指定位置的文件。具体请参考[加载配置](http://#)。

## 全局配置文件 ##

Rexdb需要一个全局配置文件**rexdb.xml**，用于设置运行选项、配置数据源、监听程序等。例如，一个典型的配置为：

```xml
	<?xml version="1.0" encoding="UTF-8"?> 
	<!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
	<configuration>
		<properties path="rexdb-settings.properties" />
		<settings>
			<property name="lang" value="${setting.lang}"/>
			<property name="nolog" value="true" />
			<property name="reflectCache" value="true" />
			<property name="dynamicClass" value="true" />
		</settings>
		<dataSource>
			<property name="driverClassName" value="com.mysql.jdbc.Driver" />
			<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
			<property name="username" value="root" />
			<property name="password" value="12345678" />
		</dataSource>
		<dataSource id="oracleDs" jndi=""/>
	 	<listener class="org.rex.db.listener.impl.SqlDebugListener"/> 
	</configuration>
```

各节点的含义如下：

- **/configuration**：配置文件根节点；
- **/configuration/properties**：外部资源文件，可以在该文件中定义键值，并在XML的其余配置中以“${*键*}”的格式引用值；
- **/configuration/settings**：Rexdb的设置选项，可以设置异常语言；
- **/configuration/dataSource**：数据源，支持自定义数据源和JNDI；
- **/configuration/listener**：自定义的监听程序，用于跟踪框架的初始化、SQL执行、事物等事件。

### 加载配置文件 ###

**rexdb.xml**文件的默认的路径为运行环境的*classpath*根目录。Rexdb会在类加载时自动读取该文件，并完成配置项、数据源、监听程序的初始化。如果您启用了日志，将会在日志的输出中看到类似如下内容（输出格式取决于您的日志配置）：

```bash
    [INFO][2016-02-23 21:26:55] configuration.Configuration[main] - loading default configuration rexdb.xml.
	... # detailed log messages.
	[INFO][2016-02-23 21:26:59] configuration.Configuration[main] - default configuration rexdb.xml loaded.
```

在默认路径中无法找到**rexdb.xml**文件时，会输出如下日志：

```bash
	[INFO][2016-02-23 22:18:36] configuration.Configuration[main] - loading default configuration rexdb.xml.
	[WARN][2016-02-23 22:18:36] configuration.Configuration[main] - could not load default configuration rexdb.xml from classpath, rexdb is not initialized, cause (DB-URS01) resource rexdb.xml not found.
```

在配置未被加载时调用Rexdb的接口，Rexdb会再次尝试从默认路径中加载配置，如果仍然无法加载，将会抛出异常信息。如果Rexdb的全局配置文件放置在其它位置，或者使用了其它的命名，可以使用类[org.rex.db.configuration.Configuration](#class-Configuration)类加载指定目录下的配置文件。该类有如下加载配置文件的接口：

<table class="tbl">
	<tr>
		<th width="60">返回值</th>
		<th width="300">接口</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>void</td>
		<td>loadDefaultConfiguration()</td>
		<td>从classpath中加载名称为rexdb.xml的配置文件</td>
	</tr>
	<tr>
		<td>void</td>
		<td>loadConfigurationFromClasspath(String path)</td>
		<td>从classpath中加载配置文件</td>
	</tr>
	<tr>
		<td>void</td>
		<td>loadConfigurationFromFileSystem(String path)</td>
		<td>从文件系统中加载配置文件</td>
	</tr>
</table>

例如，下面的代码加载了位于classpath中的rexdb-config.xml文件：

```java
    Configuration.loadConfigurationFromClasspath("rexdb-config.xml");
```

需要注意到是，Rexdb在加载配置文件时具备容错机制，当某节点不符合配置要求，或无法根据配置完成初始化时，该节点将会被忽略，并继续加载下一个节点。所以，您通常需要留意日志的输出，检查是否有未被成功加载的配置。

### 外部资源文件 ###

全局配置文件的*/configuration/properties*节点用于引用一个外部资源文件，在该文件中定义的配置可以被其它节点以“${*key*}”引用。例如，放置在classpath根目录的资源文件**rexdb-database-sample.properties**内容如下：

```bash
	driver=com.mysql.jdbc.Driver
	url=jdbc:mysql://localhost:3306/rexdb
	username=root
	password=12345678
```

**rexdb.xml**的配置如下：

```xml
	<?xml version="1.0" encoding="UTF-8"?> 
	<!DOCTYPE configuration PUBLIC "-//rex-soft.org//REXDB DTD 1.0//EN" "http://www.rex-soft.org/dtd/rexdb-1-config.dtd">
	<configuration>
		<properties path="rexdb-database-sample.propertie" />
		<dataSource>
			<property name="driverClassName" value="${driver}" />
			<property name="url" value="${url}" />
			<property name="username" value="${username}" />
			<property name="password" value="${password}" />
		</dataSource>
	</configuration>
```

Rexdb在初始化时会首先读取**rexdb-database-sample.properties**文件的内容。在解析其余节点时，如果发现其内容符合"${...}"的格式，则会替换为资源文件中配置的值。例如，*dataSource*节点的属性*driverClassName*的值是“${driver}”，符合替换条件，则会被替换为资源文件中*driver*对应的值“com.mysql.jdbc.Driver”。

*/configuration/properties*节点有如下可选属性：

<table>
	<tr>
		<th width="60">属性</th>
		<th width="40">必填</th>
		<th width="40">类型</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>path</td>
		<td>否</td>
		<td>String</td>
		<td>本地classpath中资源文件的相对路径，不能与*url*属性同时存在。</td>
	</tr>
	<tr>
		<td>url</td>
		<td>否</td>
		<td>String</td>
		<td>网络中资源文件的URL路径，不能与*path*属性同时存在。</td>
	</tr>
</table>

### 全局设置 ###

全局配置文件的*/configuration/settings*节点用于设置Rexdb的运行参数。例如，如果要设置Rexdb抛出异常时的语言为中文，可以使用如下配置：

```xml
	<settings>
		<property name="lang" value="en"/>
	</settings>
```

Rexdb支持的所有配置项有：

<table>
	<tr>
		<th width="60">配置项</th>
		<th width="40">必填</th>
		<th width="40">类型</th>
		<th width="100">可选值</th>
		<th width="60">默认值</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>lang</td>
		<td>否</td>
		<td>String</td>
		<td>en, zh-cn</td>
		<td>en</td>
		<td>设置Rexdb异常信息的语言。要注意的是，中文异常在某些linux系统中可能输出为乱码。</td>
	</tr>
	<tr>
		<td>nolog</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否禁用所有日志输出，当设置为true时，Rexdb将不再输出任何日志。</td>
	</tr>
	<tr>
		<td>validateSql</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否对SQL语句进行简单的校验。通常Rexdb只校验SQL中带有“?”标记的个数是否与的预编译参数个数相同。</td>
	</tr>
	<tr>
		<td>checkWarnings</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>在执行SQL后，是否检查状态中的警告。当设置为true时，将执行检查，如果发现有警告信息，则抛出异常。请注意，开启该选项可能会大幅降低Rexdb的性能。</td>
	</tr>
	<tr>
		<td>queryTimeout</td>
		<td>否</td>
		<td>int</td>
		<td>任意整数</td>
		<td>-1</td>
		<td>执行SQL的超时秒数，当小于或等于0时，不设置超时时间。当同时设置了事物超时时间时，Rexdb会自动选择一个较短的时间作为执行SQL的超时秒数。</td>
	</tr>
	<tr>
		<td>transactionTimeout</td>
		<td>否</td>
		<td>int</td>
		<td>任意整数</td>
		<td>-1</td>
		<td>执行事务的超时秒数，当小于或等于0时，不设置超时时间。要注意的是，Rexdb通过设置事物中每个SQL的执行时间来控制整体事物的时间，如果事物中有与SQL执行无关的操作，且在执行该操作时超时，事物超时时间将不起作用。</td>
	</tr>
	<tr>
		<td>transactionIsolation</td>
		<td>否</td>
		<td>String</td>
		<td>
			DEFAULT<br/>
			READ_UNCOMMITTED<br/>
			READ_COMMITTED<br/>
			REPEATABLE_READ<br/>
			SERIALIZABLE<br/>
		</td>
		<td>DEFAULT</td>
		<td>
			定义事物的隔离级别，仅在非JTA事物中时有效。各参数含义如下：<br/>
			- DEFAULT：使用数据库默认的事务隔离级别；<br/>
			- READ_UNCOMMITTED：一个事务可以看到其它事务未提交的数据<br/>
			- READ_COMMITTED：一个事务修改的数据提交后才能被另外一个事务读取；<br/>
			- REPEATABLE_READ：同一事务的多个实例在并发读取数据时，会看到同样的数据行；<br/>
			- SERIALIZABLE：通过强制事务排序，使事物之间不可能相互冲突。
		</td>
	</tr>
	<tr>
		<td>autoRollback</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>
			事务提交失败时是否自动回滚。
		</td>
	</tr>
	<tr>
		<td>reflectCache</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>true</td>
		<td>是否启用反射缓存。当启用时，Rexdb将会缓存类的参数、函数等信息。开启该选项可以大幅提升Rexdb的性能。</td>
	</tr>
	<tr>
		<td>dynamicClass</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>true</td>
		<td>是否启用动态字节码功能。当开启该选项时，Rexdb将使用javassist的生成中间类。启用该选项可以大幅提高Rexdb在查询Java对象时的性能。要注意的是，该选项需要配合jboss javassist包使用，Rexdb会在加载全局配置时检测javassist环境，当环境不可用时，该配置项会被自动切换为false。</td>
	</tr>
	<tr>
		<td>dateAdjust</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>true</td>
		<td>写入数据时，是否自动将日期类型的参数转换为Timestamp类型。开启此选项可以有效避免日期、时间数据的丢失，以及因类型、格式不匹配而产生的异常。</td>
	</tr>
</table>

要注意的是，如果设置项不被Rexdb支持，或者值的格式、值域不正确，则会被忽略并使用默认值。

### 数据源 ###

*/configuration/dataSource*节点用于配置数据源。该节点支持如下属性：

<table>
	<tr>
		<th width="60">属性</th>
		<th width="40">必填</th>
		<th width="40">类型</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>id</td>
		<td>否</td>
		<td>String</td>
		<td>数据源编号，不设置时为Rexdb的默认数据源，配置文件中只允许出现一个默认数据源。</td>
	</tr>
	<tr>
		<td>class</td>
		<td>否</td>
		<td>String</td>
		<td>数据源实现类，不设置时使用Rexdb的内置数据源，不能与jndi属性一同出现。</td>
	</tr>
	<tr>
		<td>jndi</td>
		<td>否</td>
		<td>String</td>
		<td>上下文中的JNDI数据源，不能与class属性一同出现。</td>
	</tr>
	<tr>
		<td>dialect</td>
		<td>否</td>
		<td>String</td>
		<td>为该数据源指定的数据库方言，不设置时将由Rexdb根据元数据信息自动选择内置的方言，请参见[方言接口](#class-dialect)。</td>
	</tr>
</table>

还可以为dataSource节点设置一个或多个*property*子节点，每个子节点可以设置一个数据源支持的的初始化参数。例如，以下代码配置了3个数据源：

```xml
	<dataSource>
		<property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
		<property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:orcl" />
		<property name="username" value="test" />
		<property name="password" value="123456" />
	</dataSource>
	<dataSource id="mysqlDs" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
		<property name="username" value="root" />
		<property name="password" value="12345678" /
	</dataSource>
	<dataSource id="oracleDs" jndi="java:/comp/env/oracleDb"/>
```

按照顺序分别是：

- 连接Oracle数据库的默认数据源，使用Rexdb自带的数据源和连接池；
- 连接Mysql数据库的数据源，编号为“mysqlDs”，使用了Apache DBCP数据源；
- 连接Oracle的数据源，编号为“oracleDs”，使用JNDI方式查找容器自带的数据源；

在Java程序中，使用org.rex.DB类进行SQL执行、事务处理等操作时，都可以指定数据源。例如，在执行查询时：

```java
	DB.getMap("SELECT * FROM REX_TEST");			//使用默认数据源执行查询
	DB.getMap("mysqlDs", "SELECT * FROM REX_TEST");	//使用mysqlDs数据源执行查询
	DB.getMap("oracleDs", "SELECT * FROM REX_TEST");//使用oracleDs数据源执行查询
```

不为dataSource节点设置class属性时，默认使用内置数据源。内置的数据源支持如下初始化参数：

<table class="tbl">
	<tr>
		<th width="60">选项</th>
		<th width="40">必填</th>
		<th width="60">类型</th>
		<th width="80">可选值</th>
		<th width="60">默认值</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>driverClassName</td>
		<td>是</td>
		<td>String</td>
		<td>-</td>
		<td>-</td>
		<td>JDBC驱动类。</td>
	</tr>
	<tr>
		<td>url</td>
		<td>是</td>
		<td>String</td>
		<td>-</td>
		<td>-</td>
		<td>数据库连接URL。</td>
	</tr>
	<tr>
		<td>username</td>
		<td>是</td>
		<td>String</td>
		<td>-</td>
		<td>-</td>
		<td>数据库用户。</td>
	</tr>
	<tr>
		<td>password</td>
		<td>是</td>
		<td>String</td>
		<td>-</td>
		<td>-</td>
		<td>数据库密码。</td>
	</tr>
	<tr>
		<td>initSize</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>1</td>
		<td>初始化连接池时创建的连接数。</td>
	</tr>
	<tr>
		<td>minSize</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>3</td>
		<td>连接池保持的最小连接数。连接池将定期检查持有的连接数，达不到该数量时将开启新的空闲连接。</td>
	</tr>
	<tr>
		<td>maxSize</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>10</td>
		<td>连接池的最大连接数。当程序所需连接超出此数量时，将置于等待状态，直到有新的空闲连接。</td>
	</tr>
	<tr>
		<td>increment</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>1</td>
		<td>每次增长的连接数。当连接池的连接数量不足，需要开启新的连接时，将一次性增长该参数指定的连接数。</td>
	</tr>
	<tr>
		<td>retries</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>2</td>
		<td>获取新的数据库连接失败后的重试次数。Rexdb不会判定失败原因，只要无法创建新的连接，即重试指定的次数。</td>
	</tr>
	<tr>
		<td>retryInterval</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>750</td>
		<td>创建新的数据库连接失败后的重试间隔，单位为毫秒。即当获取一个新的数据库连接失败，直到下一次重试的等待时间.</td>
	</tr>
	<tr>
		<td>getConnectionTimeout</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>5000</td>
		<td>获取连接的超时时间，单位为毫秒。当程序从Rexdb数据源中申请一个新的连接，且当前无空闲连接时，程序的等待时间。当超过改时间后，Rexdb会抛出一个超时的异常信息。</td>
	</tr>
	<tr>
		<td>inactiveTimeout</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>600000</td>
		<td>数据库连接的最长空闲时间，单位为毫秒。当数据库连接的空闲时间超过该参数的值时，连接会被关闭。</td>
	</tr>
	<tr>
		<td>maxLifetime</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>1800000</td>
		<td>数据库连接的最长时间，单位为毫秒。当数据库连接开启时间超过该参数的值时，连接会被关闭。</td>
	</tr>
	<tr>
		<td>testConnection</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>true</td>
		<td>开启新的数据库连接后，是否测试连接可用。当运行环境为JDK1.6及以上版本时，Rexdb将使用JDBC的测试接口执行测试；当JDK低于1.5时，如果未指定测试SQL，将调用方言接口获取测试SQL，如果能成功执行，则测试通过。</td>
	</tr>
	<tr>
		<td>testSql</td>
		<td>否</td>
		<td>String</td>
		<td>SQL语句</td>
		<td>-</td>
		<td>指定测试连接是否活跃的SQL语句。</td>
	</tr>
	<tr>
		<td>testTimeout</td>
		<td>否</td>
		<td>int</td>
		<td>大于0的整数</td>
		<td>500</td>
		<td>测试连接的超时时间。</td>
	</tr>
</table>

例如，当您希望修改数据源的初始化连接数为3、每次增长3个连接、重试次数设置为3、不再测试连接活跃性时，可以采用如下配置：

```xml
	<dataSource>
		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
		<property name="username" value="root" />
		<property name="password" value="12345678" />
		
		<property name="initSize" value="3"/>
		<property name="increment" value="3"/>
		<property name="retries" value="3"/>
		<property name="testConnection" value="false"/>
	</dataSource>
```

类似于Rexdb内置的数据源，其它开源数据源（例如Apache DBCP、C3P0等），通常也支持设置多个初始化参数，具体请参考各自的用户手册。

### 监听 ###

*/configuration/listener*节点用于设置监听程序。监听程序可以跟踪Rexdb的SQL执行、事物等事件，该节点支持如下属性：

<table>
	<tr>
		<th width="60">属性</th>
		<th width="40">必填</th>
		<th width="40">类型</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>class</td>
		<td>否</td>
		<td>String</td>
		<td>监听程序实现类。</td>
	</tr>
</table>

如果监听类定义了可以设置的属性，还可以通过设置*property*子节点为属性赋值。Rexdb内置了用于输出SQL和事物的监听类，分别是：

- org.rex.db.listener.impl.SqlDebugListener：使用日志包输出SQL和事物信息。该监听类支持如下配置选项：

<table class="tbl">
	<tr>
		<th width="60">选项</th>
		<th width="40">必填</th>
		<th width="60">类型</th>
		<th width="80">可选值</th>
		<th width="60">默认值</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>level</td>
		<td>否</td>
		<td>String</td>
		<td>debug, info</td>
		<td>debug</td>
		<td>设置日志的输出级别。</td>
	</tr>
	<tr>
		<td>simple</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否启用简易的日志输出，当设置为true时，仅在SQL或事物完成后输出日志；设置为false时，在SQL和事物执行前后均会输出日志。</td>
	</tr>
</table>

- org.rex.db.listener.impl.SqlConsolePrinterListener：将SQL和事物信息输出到终端。该监听类支持如下配置选项：

<table class="tbl">
	<tr>
		<th width="60">选项</th>
		<th width="40">必填</th>
		<th width="60">类型</th>
		<th width="80">可选值</th>
		<th width="60">默认值</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>simple</td>
		<td>否</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否启用简易的日志输出，当设置为true时，仅在SQL或事物完成后输出日志；设置为false时，在SQL和事物执行前后均会输出日志。</td>
	</tr>
</table>

例如，一个使用内置的监听类的示例如下：

```xml
 	<listener class="org.rex.db.listener.impl.SqlDebugListener">
 		<property name="simple" value="true"/>
 	</listener>
```

以上配置使用了Rexdb内置的SqlDebugListener监听，并以DEBUG级别输出简要的日志信息。如果您需要自定义监听程序，例如记录每个SQL的执行时间，可以自行编写监听类，详情请查看[监听接口](#class-listener)。

需要注意的是，监听程序并非线程安全，且不运行于独立线程，在编程时需要注意线程安全和性能问题。

## 执行数据库操作 ##

定义好全局配置文件后，就可以使用Rexdb的接口执行数据库操作了。

### 插入/更新/删除 ###

在Rexdb中，数据库的插入/更新/删除操作，以及执行创建表、删除表等DDL SQL时，均使用org.rex.DB的如下接口：

<table class="tbl">
	<tr>
		<th width="60">返回值</th>
		<th width="300">接口</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String sql)</td>
		<td>在默认数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String sql, Object[] parameterArray)</td>
		<td>在默认数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“?”标记预编译参数，Object数组中的元素按照顺序与其对应。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String sql, Ps ps)</td>
		<td>在默认数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“?”标记预编译参数，Ps对象内置的元素按照顺序与其对应。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String sql, Map<?, ?> parameterMap)</td>
		<td>在默认数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“${*key*}”的格式标记预编译参数，Map对象中键为*key*的值与其对应。当Map对象中没有键*key*时，将赋值为null。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String sql, Object parameterBean)</td>
		<td>在默认数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“${*key*}”的格式标记预编译参数，Rexdb将在Object对象中查找*key*对应的getter方法，通过该方法取值后作为相应的预编译参数。当Object对象中没有相应的getter方法时，将赋值为null。</td>
	</tr>

	<tr>
		<td>int</td>
		<td>update(String dataSourceId, String sql)</td>
		<td>在指定的数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String dataSourceId, String sql, Object[] parameterArray)</td>
		<td>在指定的数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“?”标记预编译参数，Object数组中的元素按照顺序与其对应。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String dataSourceId, String sql, Ps ps)</td>
		<td>在指定的数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“?”标记预编译参数，Ps对象内置的元素按照顺序与其对应。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String dataSourceId, String sql, Map<?, ?> parameterMap)</td>
		<td>在指定的数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“${*key*}”的格式标记预编译参数，Map对象中键为*key*的值与其对应。当Map对象中没有键*key*时，将赋值为null。</td>
	</tr>
	<tr>
		<td>int</td>
		<td>update(String dataSourceId, String sql, Object parameterBean)</td>
		<td>在指定的数据源中执行一个SQL语句，例如INSERT、UPDATE、DELETE或DDL语句。SQL语句需要以“${*key*}”的格式标记预编译参数，Rexdb将在Object对象中查找*key*对应的getter方法，通过该方法取值后作为相应的预编译参数。当Object对象中没有相应的getter方法时，将赋值为null。</td>
	</tr>
</table>

例如，需要向数据库中插入一行记录时，可以编写如下代码：

	DB.update("INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (1, 'Jim', now())"); //Mysql

当希望以预编译方式执行SQL时，可以将参数按照顺序放置在Object数组中，并调用update接口：

```Java
	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
	int i = DB.update(sql, new Object[]{1, "test", new Date()});
```

Rexdb内置了一个类*org.rex.db.Ps*，它可以取代Object数组作为执行SQL的参数。它可以指定字段类型、按照下标赋值，还可以声明输出参数，在使用中更加灵活，详情请参见[类org.rex.db.Ps](#class-ps)。例如，以下代码直接调用了该对象的构造函数，设置了预编译参数：

```Java
String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
int i = DB.update(sql, new Ps(1, "test", new Date()));
```

Rexdb支持*java.util.Map*作为执行SQL的参数。此时，SQL语句中的预编译参数需要声明为"${*key*}"的格式，Map中键为*key*的值将作为预编译参数，当没有*key*时，预编译参数将设置为null。例如：

```Java
String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
Map prameters = new HashMap();
prameters.put("id", 1);
prameters.put("name", "test");
prameters.put("createTime", new Date());

int i = DB.update(sql, prameters);
```

Rexdb还支持Java实体类作为预编译参数时，同Map类似，SQL语句中的预编译参数需要声明为"${*key*}"的格式，Rexdb将调用ava实体类中key对应的getter方法，成功取值后作为相应的预编译参数，找不到对应的getter方法时，预编译参数将设置为null。例如，Java实体类如下：

```Java
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
```

该类的实例可以作为执行SQL的参数：

```Java
String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";
RexTest rexTest = new RexTest(1, "test", new Date());

int i = DB.update(sql, rexTest);
```

在使用实体类作为预编译参数时，需要注意的是，实体类需要满足如下条件，才能被Rexdb正常调用：

- 类是可以访问的；
- 参数需要有标准的getter方法；
- 类具备无参的构造函数（启用动态字节码选项时需要调用）


下图展示了*DB.update*接口中SQL语句和参数的组合方式：

![](resource/quick-start-update.png)

### 批量更新 ###

Rexdb支持批量更新操作，以下接口

编写类TestUpdateBatch，内容如下：
```Java
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
```
在上面的类中，以*org.rex.db.Ps*数组作为批量插入的参数，数组中的每个元素都代表一条记录。执行后*DB.batchUpdate(String sql, Ps[] pss)*，数据库将写入10条记录。

除*Ps*数组外，Rexdb还支持*Object*二维数组、*Map*数组、实体类数组以及*List*作为参数，或者直接执行多条SQL语句。使用不同类型的参数时，对应的SQL的写法与单条记录的插入/更新/删除相同，SQL语句和参数的组合关系如图所示：

![](resource/quick-start-batchupdate.png)

### 查询 ###
### 调用 ###

## SQL语句和预编译参数 ##
普通SQL
带有预编译标记的SQL
带有Rexdb标记的SQL

## 扩展 ##
### 监听 ###

### 日志 ###

## 接口列表 ##

### <div id="class-Configuration">类org.rex.db.configuration.Configuration</div> ###

用于加载Rexdb全局配置文件。

> 接口摘要

<table class="tbl">
	<tr>
		<th width="60">返回值</th>
		<th width="300">接口</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>void</td>
		<td>loadDefaultConfiguration()</td>
		<td>从classpath中加载名称为rexdb.xml的配置文件</td>
	</tr>
	<tr>
		<td>void</td>
		<td>loadConfigurationFromClasspath(String path)</td>
		<td>从classpath中加载配置文件</td>
	</tr>
	<tr>
		<td>void</td>
		<td>loadConfigurationFromFileSystem(String path)</td>
		<td>从文件系统中加载配置文件</td>
	</tr>
</table>

> 详细信息

- **loadDefaultConfiguration() throws DBException**
	从classpath中加载名称为rexdb.xml配置文件
	<b>抛出:</b>
	DBException - 无法加载配置文件

- **loadConfigurationFromClasspath(String path) throws DBException**
	从classpath中加载配置文件
	<b>参数:</b>
	path - classpath中的文件路径，包含文件名称
	<b>抛出:</b>
	DBException - 无法加载配置文件

- **loadConfigurationFromFileSystem(String path) throws DBException**
	从文件系统中加载配置文件
	<b>参数:</b>
	path - 文件系统中的配置路径，包含文件名称
	<b>抛出:</b>
	DBException - 无法加载配置文件

### <div id="class-dialect">接口org.rex.db.dialect.Dialect</div> ###


### <div id="class-listener">接口org.rex.db.listener.DBListener</div> ###

### <div id="class-ps">接口org.rex.db.Ps</div> ###