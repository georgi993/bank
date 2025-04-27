package com.server.bank.controllers;

import com.server.bank.model.Transaction;
import com.server.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cash-operation")
public class OperationsController {

    private final BankService bankService;

    @Autowired
    public OperationsController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    public ResponseEntity<String> update(@RequestBody Transaction transactions){

        return bankService.processTransaction(transactions);

    }
}
