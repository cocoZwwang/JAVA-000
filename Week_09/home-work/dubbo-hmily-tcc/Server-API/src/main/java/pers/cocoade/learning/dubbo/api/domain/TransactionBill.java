package pers.cocoade.learning.dubbo.api.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class TransactionBill implements Serializable {
    private static final long serialVersionUID = -1548160964417640132L;
    private Long id;

    //买入的账户Id
    private Long buyAccount;

    //买入的金额
    private Long buyAmount;

    //买入的币种
    // "RMB":人民币 "DOLLAR"：美元
    private String buyCurrency;

    //出售的账户Id
    private Long sellerAccount;

    //出售的账户金额
    private Long sellerAmount;

    //出售的币种
    private String sellerCurrency;

    //0:关闭 1：进行中 2：失败
    private Integer state;

    private Date createTime;

    private Date updateTime;


    public TransactionBill reversal(Long buyAccount,Long sellerAccount){
        TransactionBill bill = new TransactionBill();
        bill.id = id;
        bill.buyAccount = buyAccount;
        bill.buyAmount = sellerAmount;
        bill.buyCurrency = sellerCurrency;
        bill.sellerAccount = sellerAccount;
        bill.sellerAmount = buyAmount;
        bill.sellerCurrency = buyCurrency;

        bill.createTime = createTime;
        bill.updateTime = updateTime;
        return  bill;
    }
}
