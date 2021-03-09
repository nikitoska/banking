package com.banking.account.service;

import com.banking.account.message.MessageProducer;
import com.banking.account.model.Account;
import com.banking.account.repository.AccountRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommonsLog
@Service
public class AccountService {

    private final MessageProducer messageProducer;
    private final AccountRepository accountRepository;

    public AccountService(MessageProducer messageProducer, AccountRepository accountRepository) {
        this.messageProducer = messageProducer;
        this.accountRepository = accountRepository;
    }


    public void save(UUID customerId) {
        var account = accountRepository.save(new Account(UUID.randomUUID(), customerId));
        log.info(String.format("Account %s for customer %s successfully created", account.getCustomerId(), account.getId())); // in real case send success message back
    }

    public void deleteAccount(UUID accountId) {
        accountRepository.findById(accountId).ifPresentOrElse((account -> {
            accountRepository.deleteById(accountId);
            log.info(String.format("Account %s for customer %s successfully deleted", account.getId(), account.getCustomerId()));
        }), () -> log.info(String.format("Account with id %s not found", accountId)));
    }

    public void deposit(UUID accountId, Double amount) {
        accountRepository.findById(accountId).ifPresentOrElse(account -> {
            var newBalance = account.getBalance() + amount;
            if (newBalance < 0) {
//                throw new InsufficientBalanceException()
                log.error(String.format("Not enough balance in account %s", accountId));
            } else {
                account.setBalance(newBalance);
                accountRepository.save(account);
                log.info(String.format("Account %s successfully deposited with %s", accountId, amount));

                messageProducer.operation(accountId, amount);
            }
        }, () -> log.info(String.format("Account with id %s not found", accountId)));
    }

    public List<UUID> findAll(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId).stream()
                .map(Account::getId)
                .collect(Collectors.toList());
    }

    public Double getBalance(UUID accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElse(0.0);
    }

    public Double getTotalBalance(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId).stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }
}
