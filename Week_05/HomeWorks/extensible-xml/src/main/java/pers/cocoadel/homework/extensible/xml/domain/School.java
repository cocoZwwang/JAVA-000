package pers.cocoadel.homework.extensible.xml.domain;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class School {
    private long id;

    private String name;

    private String address;
}
