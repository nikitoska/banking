package com.banking.transaction.message;

import com.banking.transaction.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Component
public class MessageConsumer {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TransactionService transactionService;

    public MessageConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @JmsListener(destination = "${jms.queue.transaction.account}")
    public void operation(Message message) throws JMSException {
        var accountId = message.getBody(UUID.class);
        var type = message.getStringProperty("type");
        var amount = message.getDoubleProperty("amount");
        transactionService.save(accountId, type, amount);
    }

    @JmsListener(destination = "${jms.queue.transaction.customer}")
    public String getCustomerAccountTransactions(Message message) throws JMSException, JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(transactionService.findAllByAccountId(message.getBody(UUID.class)));
    }

}
