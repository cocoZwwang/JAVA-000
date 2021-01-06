package pers.cocoade.learning.mysql;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SequenceIdCreatorTest {

    @Autowired
    @Qualifier("SequenceId")
    private IdCreator idCreator;

    @Before
    public void before(){
        System.out.println("before");
    }

    @Test
    public void nextId() {
        for(int i = 0; i < 100; i++){
            System.out.printf("第%s个id：%s\n", i, idCreator.nextId());
        }
    }
}