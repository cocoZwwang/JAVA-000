package per.cocoadel.homework.prefile;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.AbstractEnvironment;

/**
 * {@link org.springframework.context.annotation.Profile} 条件装配例子
 */
@ComponentScan(basePackageClasses = ProfileBootstrap.class)
public class ProfileBootstrap {
    static {
        //设置当前激活的profile属性是java8
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,"java8");
        //设置默认profile属性 是java7
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME,"java7");
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProfileBootstrap.class);
        CalculationServer calculationServer = context.getBean(CalculationServer.class);
        int sum = calculationServer.sum(1,2,3,4,5);
        System.out.println("计算结果是：" + sum);
        context.close();
    }

}
