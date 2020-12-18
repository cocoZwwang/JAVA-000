package pers.cocoade.learning.dubbo.api.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class DollarAccount implements Serializable {
    private static final long serialVersionUID = -1824901221372498984L;
    private Long id;

    private String name;

    private Long balance;

    private Integer state;

    private Date createTime;

    private Date updateTime;
}
