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

Rexdb需要一个全局配置文件**rexdb.xml**。例如，一个典型的配置内容为：

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

- /configuration：配置文件根节点；
- /configuration/properties：外部资源文件，可以在该文件中定义常量，并在其余节点中以${*常量键*}引用常量的值；
- /configuration/settings：Rexdb的设置选项；


### 加载配置 ###



### 外部资源文件 ###
### 全局设置 ###
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
