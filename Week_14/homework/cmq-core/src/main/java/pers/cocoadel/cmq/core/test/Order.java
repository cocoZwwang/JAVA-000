package pers.cocoadel.cmq.core.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Order {

    private Long id;

    private Long ts;

    private String symbol;

    private Double price;
}
