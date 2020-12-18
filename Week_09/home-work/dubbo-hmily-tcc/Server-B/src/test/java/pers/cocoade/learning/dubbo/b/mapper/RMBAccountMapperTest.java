package pers.cocoade.learning.dubbo.b.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.cocoade.learning.dubbo.api.domain.RMBAccount;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RMBAccountMapperTest {

    @Autowired
    RMBAccountMapper accountMapper;

    @Test
    public void updateBalance() {
        accountMapper.updateBalance(1L,20000L);
    }

    @Test
    public void updateState() {
        accountMapper.updateState(1L,2);
    }

    @Test
    public void add() {
        RMBAccount rmbAccount = new RMBAccount();
        rmbAccount.setId(3L);
        rmbAccount.setName("yang");
        rmbAccount.setBalance(2000L);
        rmbAccount.setState(0);
        Date date = new Date();
        rmbAccount.setCreateTime(date);
        rmbAccount.setUpdateTime(date);
        accountMapper.add(rmbAccount);
    }

    @Test
    public void delete() {
        accountMapper.delete(3L);
    }

    @Test
    public void selectOne() {
        RMBAccount rmbAccount = accountMapper.selectOne(1L);
        System.out.println(rmbAccount);
    }
}