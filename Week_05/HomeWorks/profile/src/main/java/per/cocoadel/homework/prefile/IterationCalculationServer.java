package per.cocoadel.homework.prefile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("java7")
@Component
public class IterationCalculationServer implements CalculationServer {
    @Override
    public Integer sum(Integer... values) {
        System.out.println("开始使用迭代方式进行计算...");
        int sum = 0;
        for(Integer n : values){
            sum += n;
        }
        return sum;
    }
}
