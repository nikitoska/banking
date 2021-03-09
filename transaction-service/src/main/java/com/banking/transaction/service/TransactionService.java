package com.banking.transaction.service;

import com.banking.transaction.model.Transaction;
import com.banking.transaction.repository.TransactionRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@CommonsLog
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(UUID accountId, String type, double amount) {
        var transaction = transactionRepository.save(new Transaction(UUID.randomUUID(), accountId, type, amount, Timestamp.from(Instant.now())));
        log.info(String.format("Transaction %s for account %s successfully created", transaction.getId(), accountId)); // in real case send success message back
    }

    public List<Transaction> findAllByAccountId(UUID accountId) {
        return transactionRepository.findAllByAccountId(accountId);
    }
}
