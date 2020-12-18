package pers.cocoade.learning.dubbo.a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoade.learning.dubbo.a.service.TransactionServiceAImpl;
import pers.cocoade.learning.dubbo.api.domain.TransactionBill;

import java.util.Date;

@SpringBootApplication
public class App implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private TransactionServiceAImpl transactionServiceA;

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

    //本地
    private static TransactionBill createBillA(){
        TransactionBill transactionBill = new TransactionBill();
        transactionBill.setId(1L);
        //买入7元人民币
        transactionBill.setBuyAccount(1L);
        transactionBill.setBuyAmount(700L);
        transactionBill.setBuyCurrency("RMB");
        //卖出1美元
        transactionBill.setSellerAccount(1L);
        transactionBill.setSellerAmount(100L);
        transactionBill.setSellerCurrency("DOLLAR");

        transactionBill.setCreateTime(new Date());
        transactionBill.setUpdateTime(new Date());
        return transactionBill;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        transactionServiceA.testTCCTransaction(createBillA());
    }
}
