# mybatis-penguin
A enhance framework of mybatis 
# JDK version
jdk >= 1.8
# preface
mybatis-penguin 是mybatis的扩展。
在mybatis的基础上扩展了部分常用的方法，只需要继承BaseMapper既可以直接调用常用的方法
并通过注解的方式实现Bean和database field的映射。

# method
* loadOne 通过传入主键查询单条记录
* findByIds 传入多个主键Id查询多条记录
* findAll 查询所有记录
* findByPage 进行分页查询,返回的结果中包含了总数
* findLimit 进行mysql的limit 查询
* insert 插入记录
* insertAll 同时插入多条记录
* updateSelective 根据主键更新数据，只更新不为null的属性
* update 根据主键更新所有字段
* updateSelectiveAll 根据每条记录的主键更新数据，只更新不为null的属性
* updateAll 根据每一条的主键更新所有字段
* deleteOne 根据主键删除所有字段
* deleteAll 同时根据多条主键，删除记录

# notice
* 因为方法中使用mybatis同时执行多条语句的开关，所以在配置数据库连接的时候需要将执行多条语句的开关打开allowMultiQueries=true
* java bean 中的主键应当使用@Id标注
* 具体的方法使用可以参照测试用例

# plan

* 通过修改Bean自动关联table字段的增加修改




