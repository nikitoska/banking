package com.banking.customer.message;

import com.banking.customer.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Component
public class MessageProducer {
    public static final String CREATE_ACCOUNT_TYPE = "create";
    public static final String DELETE_ACCOUNT_TYPE = "delete";
    public static final String DEPOSIT_ACCOUNT_TYPE = "deposit";
    public static final String BALANCE_ACCOUNT_TYPE = "balance_account";
    public static final String BALANCE_TOTAL_TYPE = "balance_total";
    public static final String ACCOUNT_TOTAL_TYPE = "account_total";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${jms.queue.account}")
    private String accountQueue;

    @Value("${jms.queue.account.blocked}")
    private String accountQueueBlocked;

    @Value("${jms.queue.transaction.customer}")
    private String transactionQueue;

    private final JmsTemplate jmsTemplate;

    public MessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
//        jmsTemplate.setReceiveTimeout(10000);
    }

    private void convertAndSendWithType(Object message, String type) {
        convertAndSendWithType(message, type, msg -> msg);
    }

    private void convertAndSendWithType(Object message, String type, UnaryOperator<Message> postProcess) {
        jmsTemplate.convertAndSend(accountQueue, message, msg -> {
            msg.setStringProperty("type", type);
            return postProcess.apply(msg);
        });
    }

    public void createCustomerAccount(UUID customerId) {
        convertAndSendWithType(customerId, CREATE_ACCOUNT_TYPE);
    }

    public void deleteCustomerAccount(UUID accountId) {
        convertAndSendWithType(accountId, DELETE_ACCOUNT_TYPE);
    }

    public List<UUID> getAllCustomerAccounts(UUID customerId) throws JMSException {
        var message = sendAndReceiveWithType(customerId, ACCOUNT_TOTAL_TYPE);
        if (message instanceof ObjectMessage) {
            return (List<UUID>) ((ObjectMessage) message).getObject();
        }
        return List.of();
    }

    public void deposit(UUID accountId, Double amount) {
        convertAndSendWithType(accountId, DEPOSIT_ACCOUNT_TYPE, new UnaryOperator<Message>() {
            @SneakyThrows(JMSException.class)
            @Override
            public Message apply(Message message) {
                message.setDoubleProperty("amount", amount);
                return message;
            }
        });
    }

    public Double getCustomerAccountBalance(UUID accountId) throws JMSException {
        var message = sendAndReceiveWithType(accountId, BALANCE_ACCOUNT_TYPE);
        if (message != null) {
            return message.getBody(Double.class);
        }
        return 0.0;
    }

    public Double getCustomerBalance(UUID customerId) throws JMSException {
        var message = sendAndReceiveWithType(customerId, BALANCE_TOTAL_TYPE);
        if (message != null) {
            return message.getBody(Double.class);
        }
        return 0.0;
    }

    private Message sendAndReceiveWithType(Serializable message, String type) {
        return jmsTemplate.sendAndReceive(accountQueueBlocked, session -> {
            ObjectMessage objectMessage = session.createObjectMessage(message);
            objectMessage.setStringProperty("type", type);
            return objectMessage;
        });
    }

    @SneakyThrows
    public List<TransactionDTO> getCustomerAccountTransactions(UUID accountId) throws JMSException {
        var message = jmsTemplate.sendAndReceive(transactionQueue, session -> session.createObjectMessage(accountId));
        if (message instanceof TextMessage) {
            return Arrays.asList(OBJECT_MAPPER.readValue(message.getBody(String.class), TransactionDTO[].class));
        }
        return List.of();
    }
}
