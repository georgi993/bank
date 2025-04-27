package com.server.bank.model;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Map;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class Transaction {

    @NotNull(message = "The amount cannot be null")
    @DecimalMin(value = "0.01", message = "The amount must be greater than 0")
    private Double amount;

    @NotNull(message = "The currency cannot be null")
    private Currency currency;

    private Timestamp processedDate;

    @NotNull(message = "The denominations map cannot be null")
    @Size(min = 1, message = "The denominations map must have at least one entry")
    private Map<Integer, Integer> denominations;

    @NotBlank(message = "The cashier name cannot be blank")
    private String cashier;

    @NotNull(message = "The transaction type cannot be null")
    private TransactionType transactionType;

    public Transaction() {
         this.processedDate = new Timestamp(System.currentTimeMillis());
    }

}
