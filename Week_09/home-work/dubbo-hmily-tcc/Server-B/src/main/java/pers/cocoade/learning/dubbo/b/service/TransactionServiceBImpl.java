package pers.cocoade.learning.dubbo.b.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pers.cocoade.learning.dubbo.api.domain.DollarAccount;
import pers.cocoade.learning.dubbo.api.domain.RMBAccount;
import pers.cocoade.learning.dubbo.api.domain.TransactionBill;
import pers.cocoade.learning.dubbo.api.enums.AccountState;
import pers.cocoade.learning.dubbo.api.enums.TransactionState;
import pers.cocoade.learning.dubbo.api.service.TransactionService;
import pers.cocoade.learning.dubbo.b.mapper.DollarAccountMapper;
import pers.cocoade.learning.dubbo.b.mapper.RMBAccountMapper;
import pers.cocoade.learning.dubbo.b.mapper.TransactionBillMapper;

@DubboService(version = "1.0.0")
public class TransactionServiceBImpl implements TransactionService {

    @Autowired
    private DollarAccountMapper dollarAccountMapper;

    @Autowired
    private RMBAccountMapper rmbAccountMapper;

    @Autowired
    private TransactionBillMapper transactionBillMapper;

    private static final String RMB = "RMB";
    private static final String DOLLAR = "DOLLAR";

    @HmilyTCC(confirmMethod = "comfirm", cancelMethod = "cancel")
    @Transactional
    @Override
    public void transaction(TransactionBill transactionBill) {
        System.out.println("transactionBill: " + transactionBill.toString());
        //
        transactionBill.setState(TransactionState.TRADING.getCode());
        transactionBillMapper.add(transactionBill);
        //卖出
        if (RMB.equalsIgnoreCase(transactionBill.getSellerCurrency())) {
            RMBAccount rmbAccount = rmbAccountMapper.selectOne(transactionBill.getSellerAccount());
            if (rmbAccount.getBalance() < transactionBill.getSellerAmount()) {
                throw new RuntimeException("余额不足");
            }
            Long balance = rmbAccount.getBalance() - transactionBill.getSellerAmount();
            rmbAccountMapper.updateBalance(transactionBill.getSellerAccount(), balance);
        } else {
            DollarAccount dollarAccount = dollarAccountMapper.selectOne(transactionBill.getSellerAccount());
            if (dollarAccount.getBalance() < transactionBill.getSellerAmount()) {
                throw new RuntimeException("余额不足");
            }
            Long balance = dollarAccount.getBalance() - transactionBill.getSellerAmount();
            dollarAccountMapper.updateBalance(transactionBill.getSellerAccount(), balance);
        }

        //买入
        if (RMB.equalsIgnoreCase(transactionBill.getBuyCurrency())) {
            RMBAccount rmbAccount = rmbAccountMapper.selectOne(transactionBill.getBuyAccount());
            Long balance = rmbAccount.getBalance() + transactionBill.getBuyAmount();
            rmbAccountMapper.updateBalance(transactionBill.getBuyAccount(), balance);
        } else {
            DollarAccount dollarAccount = dollarAccountMapper.selectOne(transactionBill.getBuyAccount());
            Long balance = dollarAccount.getBalance() + transactionBill.getBuyAmount();
            dollarAccountMapper.updateBalance(transactionBill.getBuyAccount(), balance);
        }
    }

    public void comfirm(TransactionBill transactionBill) {
        //交易结束
        transactionBillMapper.updateSate(transactionBill.getId(), TransactionState.COMPLETED.getCode());
        System.out.println("B comfirm");
    }

    public void cancel(TransactionBill transactionBill) {
        //交易失败
        transactionBillMapper.updateSate(transactionBill.getId(), TransactionState.FAIL.getCode());
        //冻结账户
        if (RMB.equalsIgnoreCase(transactionBill.getSellerCurrency())) {
            rmbAccountMapper.updateState(transactionBill.getSellerAccount(), AccountState.FROZEN.getCode());
        } else {
            dollarAccountMapper.updateState(transactionBill.getSellerAccount(), AccountState.FROZEN.getCode());
        }

        if (RMB.equalsIgnoreCase(transactionBill.getBuyCurrency())) {
            rmbAccountMapper.updateState(transactionBill.getBuyAccount(), AccountState.FROZEN.getCode());
        } else {
            dollarAccountMapper.updateState(transactionBill.getBuyAccount(), AccountState.FROZEN.getCode());
        }
        System.out.println("B cancel");
    }
}
