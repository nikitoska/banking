package com.banking.account.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table
public class Account {

    @PrimaryKey
    private UUID id;
    private UUID customerId;
    private double balance;

    public Account(UUID id, UUID customerId) {
        this.id = id;
        this.customerId = customerId;
        this.balance = 0;
    }
}
