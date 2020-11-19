#  **Week05 作业题目（周四）** 

## 1.（选做）使 Java 里的动态代理，实现一个简单的 AOP。

项目路径：HomeWorks\AOP

- JDK动态代理：

  pers.cocoadel.aop.proxy.TestDemo

- CGLIB

  pers.cocoadel.aop.cglib.TestDemo

## 2.（必做）写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 Github。

​	代码在项目路径：HomeWorks\spring-bean

- 通过BeanDefinition注册，这种方式Bean会完全经历Spring Bean的生命周期管理

  - 通过XML注册：

    pers.cocoadel.homework.spring.bean.register.XMLBeanDefinitionDemo

  - 通过注解（@Bean、@Component等模式注解）:

    pers.cocoadel.homework.spring.bean.register.AnnotationBeanDefinitionDemo

  - 通过Spring API手动注册BeanDefinition：

    pers.cocoadel.homework.spring.bean.register.ApiBeanDefinitionDemo

  - 通过@Import导入：

    pers.cocoadel.homework.spring.bean.register.ImportUserRegister

- 外部实例化后，添加到Spring IOC容器，这种方式Bean不会受Spring Bean生命周期管理。

  - 通过FactoryBean：

    pers.cocoadel.homework.spring.bean.instance.FactoryBeanCreationDemo

  - 通过ServiceFactoryBean：

    pers.cocoadel.homework.spring.bean.instance.ServiceFactoryBeanCreationDemo

  - 通过SingletonBeanRegistry注册外部对象：

    pers.cocoadel.homework.spring.bean.instance.SingletonBeanRegistrationDemo

## 3.（选做）实现一个 Spring XML 自定义配置，配置一组 Bean，例如：Student/Klass/School。 

项目路径：HomeWorks\extensible-xml

#  **Week05 作业题目（周六）：** 

## 1.（选做）总结一下，单例的各种写法，比较它们的优劣。  

- 饿汉式单例

  ```java
  public class HungrySingleton
  {
      private final static HungrySingleton instance = new HungrySingleton();
      
      private HungrySingleton()
      {
      }
      
      public static HungrySingleton getInstance()
      {
          return instance;
      }
  }
  ```

  - 优点：没有加任何锁，执行效率比较高，用户体验比懒汉式单例更好
  - 缺点：类加载的时候初始化，不管用还是不用都暂着空间，浪费内存；会被反射和序列化破坏。
  - 适用于单例对象较少的情况，或者对象占用资源比较少的情况

- 简单懒汉式单例

  ```java
  public class LazySimpleSingleton implements Serializable
  {
      private static final long serialVersionUID = -2975437539369581827L;
      private static volatile LazySimpleSingleton instance = null;
      
      private LazySimpleSingleton()
      {
      }
  
      public static LazySimpleSingleton getInstance()
      {
          if (instance == null)
          {
              instance = new LazySimpleSingleton();
          }
          return instance;
      }
  }
  ```

  - 优点：实现简单；同时懒加载不会浪费内存
  - getInstance() 方法存在线程安全问题；会被反射和序列化破坏。

- 单重加锁懒汉式单例

  其他代码和上面一样，不同的是getInstance() 方法

  ```java
  public synchronized static LazySimpleSingleton getInstance()
  {
      if (instance == null)
      {
          instance = new LazySimpleSingleton();
      }
      return instance;
  }
  ```

  - 优点：实现简单；同时懒加载不会浪费内存；同时也是线程安全的。

  - 缺点：每次访问都是只有一个线程，性能不够好；会被反射和序列化破坏。

- 双重加锁懒汉式单例

  ```java
  public static LazySimpleSingleton getInstance()
  {
      if (instance == null)
      {
          synchronized (LazySimpleSingleton.class)
          {
              if (instance == null)
              {
                  instance = new LazySimpleSingleton();
              }
          }
      }
      return instance;
  }
  ```

  - 优点：懒加载不会浪费内存；同时也是线程安全的；只有初始化时候会触发锁，不存在性能问题。

  - 缺点：会被反射和序列化破坏。

- 内部类单例

  ```java
  public class LazyInnerClassSingleton implements Serializable
  {
      private static final long serialVersionUID = 3362649483947306509L;
  
      private LazyInnerClassSingleton()
      {
          //添加单例检查防止通过java反射创建多个实例
          if (LazyHolder.instance != null)
          {
              throw new RuntimeException("不允许创建多个实例");
          }
          System.out.println("new instance " + this.getClass().getName());
      }
  
      public static final LazyInnerClassSingleton getInstance()
      {
          return LazyHolder.instance;
      }
  	//内部类只有被使用才会被加载
      private static class LazyHolder
      {
          //final语义吧保证线程安全
          private final static LazyInnerClassSingleton instance = new LazyInnerClassSingleton();
      }
  }
  ```

  - 优点：懒加载不会浪费内存；同时也是线程安全的；线程安全；不会被反射破坏。

  - 缺点：会被序列化破坏。

- 枚举单例

  ```java
  public enum EnumSingleton
  {
      INSTANCE;
      private Object data;
      public Object getData()
      {
          return data;
      }
      public void setData(Object data)
      {
          this.data = data;
      }
  
      public static EnumSingleton getInstance()
      {
          return INSTANCE;
      }
  }
  ```

  - 优点：饿汉式加载，效率高；线程安全；不会被反射破坏；不会被序列化破坏。

  - 缺点：饿汉式加载，浪费内存。

##  2.（选做）maven/spring 的 profile 机制，都有什么用法？ 

- spring profile：条件装配

  项目例子：HomeWorks\profile

- maven profile：根据条件打包

  项目例子：HomeWorks\profile里的pom文件

  - 打包命令：mvn clean package -Pdev会把resources\dev\application.properties 打包到进去
  - 打包命令：mvn clean package -Pprod会把resources\prod\application.properties 打包到进去

##  3.（选做）总结 Hibernate 与 MyBatis 的各方面异同点。

##  

##  4.（必做）给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。 

- HomeWorks\spring01：是拷贝老师的项目，没有做修改。
- HomeWorks\spring01-stater：是对spring01封装的自动配置和stater模块。
- HomeWorks\spring01-stater-test：是对上面自动装配的测试。

##  5.（选做）学习 MyBatis-generator 的用法和原理，学会自定义 TypeHandler 处理复杂类型。

 项目：HomeWorks/MyBatis-Learning

##  6.（必做）研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：

使用 JDBC 原生接口，实现数据库的增删改查操作。

使用事务，PrepareStatement 方式，批处理方式，改进上述操作。

配置 Hikari 连接池，改进上述操作。提交代码到 Github。 

项目例子：HomeWorks\jdbc