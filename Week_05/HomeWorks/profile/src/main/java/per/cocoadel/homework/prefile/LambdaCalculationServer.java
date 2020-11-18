package per.cocoadel.homework.prefile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * 使用Lambda表达式进行计算
 */
@Profile("java8")
@Component
public class LambdaCalculationServer implements CalculationServer {
    @Override
    public Integer sum(Integer... values) {
        System.out.println("开始使用Lambda表达式方式进行计算...");
        return Stream
                .of(values)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
