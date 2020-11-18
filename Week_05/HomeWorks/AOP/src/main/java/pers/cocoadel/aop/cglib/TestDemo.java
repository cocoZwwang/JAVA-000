package pers.cocoadel.aop.cglib;

import pers.cocoadel.aop.Calculation;
import pers.cocoadel.aop.CalculationImpl;
import pers.cocoadel.aop.DefaultCalculation;

public class TestDemo {
    public static void main(String[] args) {
        //代理DefaultCalculation
//        test(DefaultCalculation.class);
        //代理CalculationImpl
        test(CalculationImpl.class);
    }

    private static void test(Class<? extends Calculation> clazz) {
        Calculation calculation = (Calculation) new PrintTimeInterceptor().getInstance(clazz);
        int res = calculation.fibonacci(40);
        System.out.println("result: " + res);
    }
}
