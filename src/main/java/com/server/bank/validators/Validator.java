package com.server.bank.validators;

import com.server.bank.model.BalanceRequest;
import com.server.bank.model.Transaction;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Validator {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final jakarta.validation.Validator validator = factory.getValidator();

    public Set<String> validateOperations(Transaction transaction) {
        Set<ConstraintViolation<Transaction>> violation = validator.validate(transaction);
        if (!violation.isEmpty()) {
            return violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
