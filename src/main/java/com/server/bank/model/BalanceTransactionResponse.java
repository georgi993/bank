package com.server.bank.model;

import lombok.Data;

import java.util.List;

@Data
public class BalanceTransactionResponse {

    private List<Transaction> transaction;

    public BalanceTransactionResponse(List<Transaction> transaction){
        this.transaction = transaction;
    }
}
