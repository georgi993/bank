package com.server.bank.controllers;

import com.server.bank.model.BalanceRequest;
import com.server.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cash-balance")
public class BalanceController {

    private final BankService bankService;

    @Autowired
    public BalanceController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<?> getBalance(@RequestBody BalanceRequest balanceRequest) {

        return bankService.getBalance(balanceRequest);

    }
}
