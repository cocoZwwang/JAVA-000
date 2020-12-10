package pers.cocoadel.learning.hmily.order.domain;


import lombok.Data;
import lombok.ToString;

import java.util.Date;


@Data
@ToString
public class Order {

    private Long id;

    private Long userId;

    private Long productId;

    private Integer productAmount;

    private Integer state;

    private Date createTime;

    private Date updateTime;
}
