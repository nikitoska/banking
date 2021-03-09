package com.banking.transaction.repository;

import com.banking.transaction.model.Transaction;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TransactionRepository extends CassandraRepository<Transaction, UUID> {

    @AllowFiltering
    List<Transaction> findAllByAccountId(UUID accountId);
}
