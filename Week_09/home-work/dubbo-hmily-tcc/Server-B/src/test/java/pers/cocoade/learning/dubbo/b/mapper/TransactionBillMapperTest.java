package pers.cocoade.learning.dubbo.b.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.cocoade.learning.dubbo.api.domain.TransactionBill;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionBillMapperTest {

    @Autowired
    private TransactionBillMapper mapper;

    @Test
    public void add() {
        TransactionBill bill = new TransactionBill();
        bill.setId(1L);
        bill.setBuyAccount(1L);
        bill.setBuyAmount(700L);
        bill.setBuyCurrency("RMB");
        bill.setSellerAccount(2L);
        bill.setSellerAmount(100L);
        bill.setSellerCurrency("DOLLAR");
        bill.setState(1);
        bill.setCreateTime(new Date());
        bill.setUpdateTime(new Date());
        mapper.add(bill);
    }

    @Test
    public void updateSate() {
        mapper.updateSate(1L,0);
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void selectOne() {
        TransactionBill bill = mapper.selectOne(1L);
        System.out.println(bill);
    }
}