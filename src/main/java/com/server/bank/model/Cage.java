package com.server.bank.model;

import lombok.Data;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Data
public class Cage {

    private Map<Currency, Balance> balances = new HashMap<>();
    public Cage() {

        Map<Integer, Integer> bgnDenomination = new HashMap<>();
        bgnDenomination.put(10, 50);
        bgnDenomination.put(50, 10);
        balances.put(Currency.getInstance("BGN"), new Balance(1000.00D, bgnDenomination));

        Map<Integer, Integer> euroDenomination = new HashMap<>();
        euroDenomination.put(10, 100);
        euroDenomination.put(50, 20);
        balances.put(Currency.getInstance("EUR"), new Balance(2000.00D, euroDenomination));
    }

}
