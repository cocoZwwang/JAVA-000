package pers.cocoadel.homework.extensible.xml.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Student {
    private long id;

    private String name;

    private int age;

    private int gender;
}
