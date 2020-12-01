package pers.cocoadel.learning.mysql.domain;


import lombok.Data;

import java.util.Date;


@Data
public class Order {

    private Long id;

    private Long userId;

    private Long productId;

    private Integer productAmount;

    private Integer state;

    private Date createTime;

    private Date updateTime;
}
