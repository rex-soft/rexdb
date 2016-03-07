# <div id="top">简介</div> #

<!--
summary		概述
feature		功能
advantage	特点和优势
website		官方网站
help		帮助和支持
lisence		使用协议
-->

## <div id="summary">概述</div> ##

Rexdb是一款使用Java语言编写的，开放源代码的持久层框架。它具有管理数据源、执行SQL、调用函数和存储过程、处理事务等功能。使用Rexdb时，不需要像JDBC一样编写繁琐的代码，也不需要编写数据表映射文件，只要将SQL和Java对象等参数传递至框架接口，即可获取需要的结果。

Rexdb具有接口灵活、使用简单、性能良好等特点。更加适合于功能复杂、需要快速迭代或是对性能要求严苛的软件项目。由于框架使用难度较低，不需要进行长期的专题培训，所以也适合于临时组建、或是外包成员较多的研发团队。

## <div id="feature">功能</div> ##

- 数据库查询、更新、批量处理、函数和存储过程调用、事物和JTA事物等；
- ORM映射，可以使用数组、Map、Java对象作为预编译参数，也可以自动将结果集转换为Map、Java对象；
- 数据源管理，拥有内置的连接池和数据源，支持第三方数据源和JNDI；
- 数据库方言，自动封装分页查询和常用函数，支持Oracle、DB2、SQL Server、Mysql、达梦等数据库；
- 支持对框架初始化、SQL执行、事物等事件的监听；
- 统一的异常管理、异常信息的国际化支持等；


## <div id="advantage">特点和优势</div> ##

- 编码量少，不需要编写映射配置，且只需要少量代码就可以完成与数据库的交互；
- 使用简单，学习难度极低，开发人员不需要学习繁琐的配置规则；
- 性能良好，与直接调用JDBC接口相比，框架具有极低的性能损耗；
- 兼容性好，没有必须依赖的第三方包，可以与其它框架组合使用。

## <div id="website">官方网站</div> ##

Rexdb的网站地址是：[http://db.rex-soft.org](http://db.rex-soft.org)。

## <div id="help">帮助和支持</div> ##

Rexdb是免费的开源软件，除源代码、文档和示例外，不提供技术支持。但开发团队致力于改进框架的使用体验，欢迎使用者[提出需求和BUG反馈](http://#)。

## <div id="lisence">使用协议</div> ##

Rexdb基于Apache 2.0协议，可以免费用于个人或商业用途。

协议详情请见：[Apache Lisence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)