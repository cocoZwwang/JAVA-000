package pers.cocoadel.homework.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import pers.cocoadel.homework.jdbc.dao.FooDao;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@SpringBootApplication
public class JdbcTestBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private FooDao fooDao;

    public static void main(String[] args) {
        SpringApplication.run(JdbcTestBootstrap.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //一条条插入
        fooDao.insertData();
        //查询并且打印
        fooDao.showData();
        //批量插入
        fooDao.batchInsetData();
        //查询并且打印
        fooDao.showData();
    }

    @Bean
    public SimpleJdbcInsert simpleJdbcInsert(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("foo")
                .usingGeneratedKeyColumns("id");
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
