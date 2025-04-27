package com.server.bank.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BalanceRequest {

    @NotNull(message = "The dateFrom should not be null")
    private Timestamp dateFrom;

    @NotNull(message = "The dateTo should not be null")
    private Timestamp dateTo;

    @NotBlank(message = "The cashier should not be blank")
    private String cashier;


    public BalanceRequest(){

    }
}
