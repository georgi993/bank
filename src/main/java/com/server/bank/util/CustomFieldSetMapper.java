package com.server.bank.util;

import com.server.bank.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class CustomFieldSetMapper implements FieldSetMapper<Transaction> {

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        Transaction transaction = new Transaction();
        transaction.setAmount(fieldSet.readDouble("amount"));
        transaction.setCurrency(BankUtil.convertToCurrency(fieldSet.readString("currency")));
        transaction.setProcessedDate(BankUtil.convertToTimestamp(fieldSet.readString("processedDate")));
        transaction.setDenominations(BankUtil.parseDenominations(fieldSet.readString("denominations")));
        transaction.setCashier(fieldSet.readString("cashier"));
        transaction.setTransactionType(BankUtil.convertToTransactionType(fieldSet.readString("transactionType")));
        return transaction;
    }
}