package pers.cocoadel.aop;

public class CalculationImpl implements Calculation{


    @Override
    public int fibonacci(int n) {
        if(n <= 2){
            return n;
        }
        int f1 = 1;
        int f2 = 2;
        for(int i = 3; i <= n; i++){
            int res = f1 + f2;
            f1 = f2;
            f2 = res;
        }
        return f2;
    }
}
