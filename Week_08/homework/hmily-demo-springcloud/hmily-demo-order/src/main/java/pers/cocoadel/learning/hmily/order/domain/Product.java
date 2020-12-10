package pers.cocoadel.learning.hmily.order.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Product {
    private Long id;

    private Long classId;

    private String name;

    private Long price;

    private Integer stock;

    private Date createTime;

    private Date updateTime;

    private Long createBy;

    private Long updateBy;
}

