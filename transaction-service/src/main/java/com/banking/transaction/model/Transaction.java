package com.banking.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Transaction {

    @PrimaryKey
    private UUID id;
    private UUID accountId;
    private String type;
    private double amount;
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Timestamp date;

}
