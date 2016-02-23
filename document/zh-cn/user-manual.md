<style type="text/css">
table.tbl {
	font-family: verdana,arial,sans-serif;
	font-size:12px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
	width: 100%;
}
table.tbl th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table.tbl td {
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

各节点的含义如下：

- **/configuration**：配置文件根节点；
- **/configuration/properties**：外部资源文件，可以在该文件中定义键值，并在XML的其余配置中以“${*键*}”的格式引用值；
- **/configuration/settings**：Rexdb的设置选项，可以设置异常语言；
- **/configuration/dataSource**：数据源，支持自定义数据源和JNDI；
- **/configuration/listener**：自定义的监听程序，用于跟踪框架的初始化、SQL执行、事物等事件。

### 加载配置文件 ###

**rexdb.xml**文件的默认的路径为运行环境的*classpath*根目录。Rexdb会在类加载时自动读取该文件，并完成配置项、数据源、监听程序的初始化。如果您启用了日志，将会在日志的输出中看到类似如下内容（输出格式取决于您的日志配置）：

    [INFO][2016-02-23 21:26:55] configuration.Configuration[main] - loading default configuration rexdb.xml.
	... # detailed log messages.
	[INFO][2016-02-23 21:26:59] configuration.Configuration[main] - default configuration rexdb.xml loaded.

在默认路径中无法找到**rexdb.xml**文件时，会输出如下日志：

	[INFO][2016-02-23 22:18:36] configuration.Configuration[main] - loading default configuration rexdb.xml.
	[WARN][2016-02-23 22:18:36] configuration.Configuration[main] - could not load default configuration rexdb.xml from classpath, rexdb is not initialized, cause (DB-URS01) resource rexdb.xml not found.

在配置未被加载时调用Rexdb的接口，Rexdb会再次尝试从默认路径中加载配置，如果仍然无法加载，将会抛出异常信息。

如果Rexdb的全局配置文件放置在其它位置，或者使用了其它的命名，可以使用类[org.rex.db.configuration.Configuration](#class-Configuration)类加载指定目录下的配置文件，例如：

    Configuration.loadConfigurationFromClasspath("rexdb-config.xml");//加载classpath中的rexdb-config.xml文件

需要注意到是，Rexdb在加载配置文件时具备容错机制，当某节点不符合配置要求，或无法根据配置完成初始化时，该节点将会被忽略，并继续加载下一个节点。所以，您通常需要留意日志的输出，检查是否有未被成功加载的配置。

### 外部资源文件 ###

全局配置文件的*/configuration/properties*节点用于引用一个外部资源文件，在该文件中定义的配置可以被其它节点以“${*键*}”引用。例如，放置在classpath根目录的资源文件**rexdb-database-sample.properties**内容如下：

	driver=com.mysql.jdbc.Driver
	url=jdbc:mysql://localhost:3306/rexdb
	username=root
	password=12345678

**rexdb.xml**的配置如下：

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

Rexdb在初始化时会首先读取**rexdb-database-sample.properties**文件的内容。在解析其余节点时，如果发现其内容符合"${...}"的格式，则会替换为资源文件中配置的值。例如，*dataSource*节点的属性*driverClassName*的值是“${driver}”，符合替换条件，则会被替换为资源文件中*driver*对应的值“com.mysql.jdbc.Driver”。

*/configuration/properties*节点有如下可选属性：

- path：本地classpath中资源文件的相对路径，不能与*url*属性同时存在；
- url：网络中资源文件的URL路径，不能与*path*属性同时存在；

### 全局设置 ###
全局配置文件的*/configuration/settings*节点用于设置Rexdb的运行参数。例如，该节点配置了如下内容：

	<settings>
		<property name="lang" value="en"/>
	</settings>

上述配置中的*lang*参数用于设置Rexdb抛出异常时的语言，设置为“en”时，Rexdb产生的异常信息均为英语；而设置为“zh-cn”时，异常消息均为中文。

要注意的是，如果设置项不被Rexdb支持，或者值的格式、值域不正确，则会被忽略并使用默认值。

Rexdb支持的所有配置项有：

<table class="tbl">
	<tr>
		<th width="60">配置项</th>
		<th width="60">类型</th>
		<th width="120">可选值</th>
		<th width="60">默认值</th>
		<th width="">说明</th>
	</tr>
	<tr>
		<td>lang</td>
		<td>String</td>
		<td>en, zh-cn</td>
		<td>en</td>
		<td>设置Rexdb异常信息的语言。要注意的是，中文异常在某些linux系统中可能输出为乱码。</td>
	</tr>
	<tr>
		<td>nolog</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否禁用所有日志输出，当设置为true时，Rexdb将不再输出任何日志。</td>
	</tr>
	<tr>
		<td>validateSql</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>是否对SQL语句进行简单的校验。通常Rexdb只校验SQL中带有“?”标记的个数是否与的预编译参数个数相同。</td>
	</tr>
	<tr>
		<td>checkWarnings</td>
		<td>boolean</td>
		<td>true, false</td>
		<td>false</td>
		<td>在执行SQL后，是否检查状态中的警告。当设置为true时，将执行检查，如果发现有警告信息，则抛出异常。开启该选项可能会大幅降低Rexdb的性能。</td>
	</tr>
	<tr>
		<td>queryTimeout</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>transactionTimeout</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>transactionIsolation</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>autoRollback</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>reflectCache</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>dynamicClass</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td>dateAdjust</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
</table>

### 数据源 ###
### 监听 ###

## Java接口 ##
### 插入/更新/删除 ###
### 批量处理 ###
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

- **loadDefaultConfiguration() throws DBException**<br/>
	从classpath中加载名称为rexdb.xml配置文件<br/>
	<b>抛出:</b><br/>
	DBException - 无法加载配置文件

- **loadConfigurationFromClasspath(String path) throws DBException**<br/> 
	从classpath中加载配置文件<br/>
	<b>参数:</b><br/>
	path - classpath中的文件路径，包含文件名称<br/>
	<b>抛出:</b><br/>
	DBException - 无法加载配置文件

- **loadConfigurationFromFileSystem(String path) throws DBException**<br/>
	从文件系统中加载配置文件<br/>
	<b>参数:</b><br/>
	path - 文件系统中的配置路径，包含文件名称<br/>
	<b>抛出:</b><br/>
	DBException - 无法加载配置文件