package com.server.bank.util;

import com.server.bank.model.TransactionType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BankUtil {

    private BankUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Timestamp convertDate(Timestamp processedDate) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime localDateTime = LocalDateTime.parse(processedDate.toString(), inputFormatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
        return Timestamp.from(zonedDateTime.toInstant());
    }

    public static Timestamp convertToTimestamp(String dateString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
            return Timestamp.valueOf(dateTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }

    public static Map<Integer, Integer> parseDenominations(String input) {
        Map<Integer, Integer> denominations = new HashMap<>();
        try {
            input = input.replaceAll("^\"|\"$", "");
            String[] pairs = input.split(";");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    denominations.put(Integer.parseInt(keyValue[0].trim()), Integer.parseInt(keyValue[1].trim()));
                } else {
                    System.err.println("Invalid denomination pair: " + pair);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing denominations: " + e.getMessage());
        }
        return denominations;
    }


    public static TransactionType convertToTransactionType(String currencyString) {
        return TransactionType.valueOf(currencyString);
    }

    public static Currency convertToCurrency(String currencyString) {
        return Currency.getInstance(currencyString);
    }

    public static String convertMapToCsvFormat(Map<Integer, Integer> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        map.forEach((key, value) -> builder.append(key).append("=").append(value).append(";"));
        if (!map.isEmpty()) {
            builder.setLength(builder.length() - 1);
        }
        builder.append("\"");
        return builder.toString();
    }
}
