# 简介 #
## 概述 ##
Rexdb是一款使用Java语言编写的、开放源代码的持久层框架。具有功能全面、使用简便、性能良好等特点。尤其适合于需求不明确、需要快速迭代，或是功能复杂、表结构复杂的研发场景；又由于框架使用难度较低，不需要进行长期的专题培训，所以也适合于临时组建的、或是外包成员较多的研发团队。

## 功能 ##

1. JDBC的主要功能，包括数据库查询、更新、批量处理、函数和存储过程调用、事物和JTA事物等；
2. ORM映射，Java对象与JDBC对象之间的自动映射；
3. 数据源管理，拥有内置的连接池和数据源，支持第三方数据源和JNDI；
4. 数据库方言，支持Oracle、DB2、SQL Server、Mysql、达梦等数据库；
5. SQL的跟踪监控；
6. 统一的异常管理、错误信息的国际化支持；
7. SQL校验、事物超时设置等功能。


## 特点和优势 ##

1. 编码量少，不需要编写映射配置，且只需要少量代码就可以完成与数据库的交互；
1. 使用简单，学习难度极低，开发人员不需要学习繁琐的配置规则，详情请参见[Rexdb快速上手指南](quick-start.html)；
4. 性能良好，框架具有极低的性能损耗，性能测试结果请查看[Rexdb性能测试报告](http://#)；
5. 兼容性好，没有必须依赖的第三方包，可以与其它框架组合使用。

## 官方网站 ##

Rexdb的网站地址是：[http://db.rex-soft.org](http://db.rex-soft.org)。

## 帮助和支持 ##

Rexdb是免费的开源软件，除源代码、文档和示例外，不提供技术支持。但开发团队致力于改进框架的使用体验，欢迎使用者[提出需求和BUG反馈](http://#)。

## 使用协议 ##

Rexdb基于Apache 2.0协议，可以免费用于个人或商业用途。

协议详情请见：[Apache Lisence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)