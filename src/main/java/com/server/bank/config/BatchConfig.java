package com.server.bank.config;

import com.server.bank.model.Balance;
import com.server.bank.model.Transaction;
import com.server.bank.util.BankUtil;
import com.server.bank.util.CustomFieldSetMapper;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemWriter<Transaction> transactionItemWriter() {
        return new FlatFileItemWriterBuilder<Transaction>()
                .name("transactionItemWriter")
                .resource(new FileSystemResource("src/main/resources/history.csv"))
                .append(true)
                .lineAggregator(new DelimitedLineAggregator<Transaction>() {{
                    setDelimiter(",");
                    setFieldExtractor(new FieldExtractor<Transaction>() {
                        @Override
                        public Object[] extract(Transaction transaction) {
                            return new Object[] {
                                    transaction.getAmount(),
                                    transaction.getCurrency(),
                                    transaction.getProcessedDate(),
                                    BankUtil.convertMapToCsvFormat(transaction.getDenominations()), // Map with ; separator
                                    transaction.getCashier(),
                                    transaction.getTransactionType()
                            };
                        }
                    });
                }})
                .headerCallback(writer -> {
                    File file = new File("history.csv");
                    if (file.length() == 0) {
                        writer.write("amount,currency,processedDate,denominations,cashier,transactionType");
                    }
                })
                .build();
    }

    @Bean
    public FlatFileItemReader<Transaction> reader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("reader")
                .resource(new FileSystemResource("src/main/resources/history.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("amount", "currency", "processedDate", "denominations", "cashier", "transactionType")
                .fieldSetMapper(new CustomFieldSetMapper())
                .strict(false)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Balance> balanceItemWriter() {
        return new FlatFileItemWriterBuilder<Balance>()
                .name("balanceItemWriter")
                .resource(new FileSystemResource("src/main/resources/balance.csv"))
                .append(true)
                .lineAggregator(new DelimitedLineAggregator<Balance>() {{
                    setDelimiter(",");
                    setFieldExtractor(new FieldExtractor<Balance>() {
                        @Override
                        public Object[] extract(Balance balance) {
                            return new Object[]{
                                    balance.getAmount(),
                                    BankUtil.convertMapToCsvFormat(balance.getDenominations())
                            };
                        }
                    });
                }})
                .headerCallback(writer -> {
                    File file = new File("balance.csv");
                    if (file.length() == 0) {
                        writer.write("amount,denominations");
                    }
                })
                .build();
    }








}
