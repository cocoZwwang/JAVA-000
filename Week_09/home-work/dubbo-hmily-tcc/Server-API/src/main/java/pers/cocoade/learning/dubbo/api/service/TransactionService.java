package pers.cocoade.learning.dubbo.api.service;


import pers.cocoade.learning.dubbo.api.domain.TransactionBill;

public interface TransactionService {
    void transaction(TransactionBill transactionBill);
}
