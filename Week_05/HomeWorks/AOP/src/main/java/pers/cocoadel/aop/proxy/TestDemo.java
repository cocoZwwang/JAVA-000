package pers.cocoadel.aop.proxy;

import pers.cocoadel.aop.Calculation;
import pers.cocoadel.aop.CalculationImpl;
import pers.cocoadel.aop.DefaultCalculation;

public class TestDemo {
    public static void main(String[] args) {
        //代理DefaultCalculation实现
        test(new DefaultCalculation());

        //代理CalculationImpl实现
        test(new CalculationImpl());
    }

    private static void test(Calculation calculation) {
        //代理Calculation实现，打印方法运行耗时
        calculation = (Calculation) new PrintTimeProxy().getInstance(calculation);
        int res = calculation.fibonacci(40);
        System.out.println("result: " + res);
    }
}
