package pers.cocoadel.learing.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import pers.cocoadel.learing.mysql.context.MultiDataSourceRegister;
import pers.cocoadel.learing.mysql.service.MytblService;

/**
 * 最终测试代码：test目录下 pers.cocoadel.learing.mysql.service.MytblServiceTest
 */
@Import(MultiDataSourceRegister.class)
@SpringBootApplication
public class App implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private MytblService mytblService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
//        mytblService.batchSave();
//        mytblService.show();
    }
}
