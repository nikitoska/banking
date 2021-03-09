package com.banking.account.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageProducer {
    public static final String DEPOSIT_AMOUNT_TYPE = "deposit";
    public static final String WITHDRAWAL_AMOUNT_TYPE = "withdrawal";

    @Value("${jms.queue.transaction.account}")
    private String transactionQueue;

    private final JmsTemplate jmsTemplate;

    public MessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void operation(UUID accountId, Double amount) {
        jmsTemplate.convertAndSend(transactionQueue, accountId, msg -> {
            msg.setStringProperty("type", amount > 0 ? DEPOSIT_AMOUNT_TYPE : WITHDRAWAL_AMOUNT_TYPE);
            msg.setDoubleProperty("amount", amount);
            return msg;
        });
    }
}
