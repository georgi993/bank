package com.server.bank.model;

import lombok.Data;

import java.util.List;

@Data
public class BalanceResponse {

    private Cage cage;

    public BalanceResponse(Cage cage){
        this.cage = cage;
    }
}
