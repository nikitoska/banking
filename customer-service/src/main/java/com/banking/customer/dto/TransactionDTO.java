package com.banking.customer.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class TransactionDTO {

    private UUID id;
    private UUID accountId;
    private String type;
    private double amount;
    private Timestamp date;
}
