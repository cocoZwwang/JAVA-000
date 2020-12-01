package pers.cocoadel.learning.mysql.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ToString
@Entity
@Table(name = "mytbl")
public class Mytbl {
    @Id
    private Integer id;

    private String name;
}
