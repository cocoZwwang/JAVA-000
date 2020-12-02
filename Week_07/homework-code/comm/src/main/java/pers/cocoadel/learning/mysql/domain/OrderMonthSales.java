package pers.cocoadel.learning.mysql.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 订单的每月销售总额
 */
@Data
@ToString
public class OrderMonthSales {
    private Date date;

    private Long sales;
}
