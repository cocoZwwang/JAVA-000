package pers.cocoadel.learning.mysql.service;

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

    /**
     * 插入数据
     */
    @Test
    public void insert() {
        mytblService.insert();
    }

    /**
     * 读取数据
     */
    @Test
    public void show() {
        mytblService.show();
    }

//    /**
//     * 同一事务中先写后读
//     */
//    @Test
//    public void insertAndShow() {
//        mytblService.insertAndShow();
//    }
}