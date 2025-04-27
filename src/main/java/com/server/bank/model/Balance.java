package com.server.bank.model;

import lombok.Data;

import java.util.Map;

@Data
public class Balance {

    private Double amount;
    private Map<Integer, Integer> denominations;

    public Balance(Double amount, Map<Integer, Integer> denominations) {
        this.amount = amount;
        this.denominations = denominations;
    }

}
