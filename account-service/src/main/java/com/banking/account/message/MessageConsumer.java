package com.banking.account.message;

import com.banking.account.service.AccountService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.UUID;

@Component
public class MessageConsumer {
    private final AccountService accountService;

    public MessageConsumer(AccountService accountService) {
        this.accountService = accountService;
    }

    @JmsListener(destination = "${jms.queue.account}", selector = "type = 'create'")
    public void createAccount(UUID customerId) {
        accountService.save(customerId);
    }

    @JmsListener(destination = "${jms.queue.account}", selector = "type = 'delete'")
    public void deleteAccount(UUID accountId) {
        accountService.deleteAccount(accountId);
    }

    @JmsListener(destination = "${jms.queue.account.blocked}", selector = "type = 'account_total'")
    public List<UUID> getAllCustomerAccounts(UUID customerId) {
        return accountService.findAll(customerId);
    }

    @JmsListener(destination = "${jms.queue.account.blocked}", selector = "type = 'balance_account'")
    public Double getBalance(UUID accountId) {
        return accountService.getBalance(accountId);
    }

    @JmsListener(destination = "${jms.queue.account.blocked}", selector = "type = 'balance_total'")
    public Double getTotalBalance(UUID customerId) {
        return accountService.getTotalBalance(customerId);
    }

    @JmsListener(destination = "${jms.queue.account}", selector = "type = 'deposit'")
    public void deposit(Message message) throws JMSException {
        var accountId = message.getBody(UUID.class);
        var amount = message.getDoubleProperty("amount");
        accountService.deposit(accountId, amount);
    }
}
