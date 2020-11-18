package pers.cocoadel.homework.spring.bean.domain;

import lombok.Data;
import lombok.ToString;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@ToString
@Data
public class User {
    private String name;

    private Integer age;

    private String description;

    @PostConstruct
    public void init(){
        System.out.println("User Bean init on PostConstruct...");
    }

    @PreDestroy
    public void onDestroy(){
        System.out.println("User Bean destroy...");
    }
}
