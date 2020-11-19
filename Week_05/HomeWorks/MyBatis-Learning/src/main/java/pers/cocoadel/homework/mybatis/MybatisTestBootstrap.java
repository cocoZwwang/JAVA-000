package pers.cocoadel.homework.mybatis;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.homework.mybatis.domain.Coffee;
import pers.cocoadel.homework.mybatis.domain.CoffeeEntry;
import pers.cocoadel.homework.mybatis.domain.CoffeeEntryExample;
import pers.cocoadel.homework.mybatis.mapper.CoffeeEntryMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class MybatisTestBootstrap implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private CoffeeEntryMapper coffeeEntryMapper;

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestBootstrap.class,args);
    }

    private void generateArtifacts() throws Exception {
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(
                this.getClass().getResourceAsStream("/generatorConfig.xml"));
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            generateArtifacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playWithArtifacts() {
        CoffeeEntry espresso = new Coffee()
                .withName("espresso")
                .withPrice(Money.of(CurrencyUnit.of("CNY"), 20.0))
                .withCreateTime(new Date())
                .withUpdateTime(new Date());
        coffeeEntryMapper.insert(espresso);


        CoffeeEntry s = coffeeEntryMapper.selectByPrimaryKey(1L);
        System.out.printf("Coffee %s\n", s);

        CoffeeEntryExample example = new CoffeeEntryExample();
        example.createCriteria().andNameEqualTo("latte");
        List<CoffeeEntry> list = coffeeEntryMapper.selectByExample(example);
        list.forEach(e -> System.out.printf("Coffee %s\n", e));
    }
}
