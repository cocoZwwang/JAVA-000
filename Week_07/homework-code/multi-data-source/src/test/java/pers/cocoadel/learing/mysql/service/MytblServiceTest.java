package pers.cocoadel.learing.mysql.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MytblServiceTest {
    @Autowired
    private MytblService mytblService;

    @Test
    public void batchSave(){
        mytblService.batchSave();
    }

    @Test
    public void show() {
        mytblService.show();
    }
}