package pers.cocoade.learning.dubbo.b.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.cocoade.learning.dubbo.api.domain.DollarAccount;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DollarAccountMapperTest {

    @Autowired
    DollarAccountMapper accountMapper;

    @Test
    public void updateBalance() {
        accountMapper.updateBalance(1L,2000L);
    }

    @Test
    public void updateState() {
        accountMapper.updateState(1L,2);
    }

    @Test
    public void add() {
        DollarAccount dollarAccount = new DollarAccount();
        dollarAccount.setId(3L);
        dollarAccount.setName("yang");
        dollarAccount.setBalance(2000L);
        dollarAccount.setState(0);
        Date date = new Date();
        dollarAccount.setCreateTime(date);
        dollarAccount.setUpdateTime(date);
        accountMapper.add(dollarAccount);
    }

    @Test
    public void delete() {
        accountMapper.delete(3L);
    }

    @Test
    public void selectOne() {
        DollarAccount dollarAccount = accountMapper.selectOne(1L);
        System.out.println(dollarAccount);
    }
}