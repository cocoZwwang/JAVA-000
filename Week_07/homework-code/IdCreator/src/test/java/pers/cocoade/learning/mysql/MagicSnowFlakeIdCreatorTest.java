package pers.cocoade.learning.mysql;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class MagicSnowFlakeIdCreatorTest {

    @Autowired
    @Qualifier("MagicSnowFlake")
    private IdCreator<Long> idCreator;

    @Test
    void nextId() {
        for(int i = 0; i < 100; i++){
            System.out.printf("第%s个SnowFlake id：%s\n", i, idCreator.nextId());
        }
    }
}