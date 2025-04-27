package com.server.bank.service;

import com.server.bank.model.*;
import com.server.bank.util.BankUtil;
import com.server.bank.validators.Validator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankService {

    private final Map<String, Cage> cashiers = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    private final FlatFileItemWriter<Transaction> writer;

    private final FlatFileItemWriter<Balance> balanceWriter;

    private final FlatFileItemReader<Transaction> reader;

    private final Validator validator;

    @Autowired
    public BankService(FlatFileItemWriter<Transaction> writer,
                       FlatFileItemReader<Transaction> reader,
                       FlatFileItemWriter<Balance> balanceWriter,
                       Validator validator) {
        this.writer = writer;
        this.reader = reader;
        this.balanceWriter = balanceWriter;
        this.validator = validator;

    }

    @PostConstruct
    public void init() {
        cashiers.put("MARTINA", new Cage());
        cashiers.put("PETER", new Cage());
        cashiers.put("LINDA", new Cage());
    }

    public ResponseEntity<String> processTransaction(Transaction transaction) {

        if (!validator.validateOperations(transaction).isEmpty()) {
            return ResponseEntity.badRequest().body(String.join(" | ", validator.validateOperations(transaction)));
        }

        ResponseEntity<String> status;

        if (transaction.getTransactionType().equals(TransactionType.DEPOSIT)) {
            status = deposit(transaction);
        } else {
            status = withdraw(transaction);
        }
        return status;
    }

    public ResponseEntity<String> deposit(Transaction transaction) {

        Cage cage = cashiers.get(transaction.getCashier());
        Currency currency = transaction.getCurrency();
        Balance balance = cage.getBalances().computeIfAbsent(currency, k -> new Balance(0.0D, new HashMap<>()));

        balance.setAmount(balance.getAmount() + transaction.getAmount());

        for (Map.Entry<Integer, Integer> entry : transaction.getDenominations().entrySet()) {
            balance.getDenominations().put(entry.getKey(), balance.getDenominations().getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        logger.info("Deposit: {}", transaction.getAmount());
        writeTransaction(transaction);
        writeBalance(balance);
        return ResponseEntity.ok("The operation was successful!");
    }

    public ResponseEntity<String> withdraw(Transaction transaction) {

        Cage cage = cashiers.get(transaction.getCashier());
        Currency currency = transaction.getCurrency();
        Balance balance = cage.getBalances().get(currency);

        double totalSumDenomination = transaction.getDenominations().entrySet().stream()
                .mapToInt(entry -> entry.getKey() * entry.getValue())
                .sum();

        Optional<String> errorMessage = transaction.getDenominations().entrySet().stream()
                .filter(entry -> balance.getDenominations().getOrDefault(entry.getKey(), 0) < entry.getValue())
                .map(entry -> "The operation failed! Insufficient " + entry.getKey() + " denomination notes.")
                .findFirst();

        if (errorMessage.isPresent()) {
            return ResponseEntity.badRequest().body(errorMessage.get());
        }

        if (balance.getAmount() < transaction.getAmount()) {
            return ResponseEntity.badRequest().body("Insufficient availability!");
        }

        if (transaction.getAmount() != totalSumDenomination) {
            return ResponseEntity.badRequest().body("The amount for withdraw is not the same with the sum of denominations!");
        }

        for (Map.Entry<Integer, Integer> entry : transaction.getDenominations().entrySet()) {
            balance.getDenominations().put(entry.getKey(), balance.getDenominations().get(entry.getKey()) - entry.getValue());
        }

        balance.setAmount(balance.getAmount() - transaction.getAmount());
        logger.info("Withdraw: {}", transaction.getAmount());
        writeTransaction(transaction);
        return ResponseEntity.ok("The operation was successful!");
    }

    public ResponseEntity<?> getBalance(BalanceRequest balanceRequest) {

        boolean isByDateRange = balanceRequest.getDateFrom() != null && balanceRequest.getDateTo() != null;

        if (isByDateRange) {
            List<Transaction> transactionList = getBalanceByDate(balanceRequest);
            return ResponseEntity.ok(new BalanceTransactionResponse(transactionList));
        }

        Cage cage = cashiers.get(balanceRequest.getCashier());
        return ResponseEntity.ok(new BalanceResponse(cage));

    }

    public List<Transaction> getBalanceByDate(BalanceRequest balanceRequest) {

        Timestamp start = balanceRequest.getDateFrom();
        Timestamp end = balanceRequest.getDateTo();

        List<Transaction> transactionsByDate = new ArrayList<>();

        try {
            reader.open(new ExecutionContext());
            Transaction transaction;
            while ((transaction = reader.read()) != null) {
                Timestamp processedDate = BankUtil.convertDate(transaction.getProcessedDate());

                if (processedDate.compareTo(start) >= 0 && processedDate.compareTo(end) <= 0) {
                    transactionsByDate.add(transaction);
                }
            }
            reader.close();
        } catch (Exception e) {
            logger.error("Error occur while process batch! {}", String.valueOf(e));
        }
        return transactionsByDate;
    }

    public void writeTransaction(Transaction transaction) {

        ExecutionContext executionContext = new ExecutionContext();

        try {
            writer.open(executionContext);
            Chunk<Transaction> chunk = new Chunk<>(Collections.singletonList(transaction));
            writer.write(chunk);
            writer.update(executionContext);
        } catch (Exception e) {
            logger.error("An error occur while writing in csv file!{}", String.valueOf(e));
        } finally {
            writer.close();
        }

    }

    public void writeBalance(Balance balance) {

        ExecutionContext executionContext = new ExecutionContext();

        try {
            balanceWriter.open(executionContext);
            Chunk<Balance> chunk = new Chunk<>(Collections.singletonList(balance));
            balanceWriter.write(chunk);
            balanceWriter.update(executionContext);
        } catch (Exception e) {
            logger.error("An error occur while writing in balance csv file!{}", String.valueOf(e));
        } finally {
            balanceWriter.close();
        }

    }

}
