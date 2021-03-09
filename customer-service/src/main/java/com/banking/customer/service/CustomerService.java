package com.banking.customer.service;

import com.banking.customer.dto.TransactionDTO;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.message.MessageProducer;
import com.banking.customer.model.Customer;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.util.ThrowingSupplier;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final MessageProducer messageProducer;
    private final CustomerRepository customerRepository;

    public CustomerService(MessageProducer messageProducer, CustomerRepository customerRepository) {
        this.messageProducer = messageProducer;
        this.customerRepository = customerRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void remove(UUID id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void createCustomerAccount(UUID customerId) {
        runIfCustomerExists(customerId, () -> messageProducer.createCustomerAccount(customerId));
    }

    private void runIfCustomerExists(UUID customerId, Runnable action) {
        if (customerRepository.existsById(customerId)) {
            action.run();
        } else throw new CustomerNotFoundException(customerId);
    }

    private <T> T runIfCustomerExists(UUID customerId, ThrowingSupplier<T> action) throws JMSException {
        if (customerRepository.existsById(customerId)) {
            return action.get();
        } else throw new CustomerNotFoundException(customerId);
    }

    public void deleteCustomerAccount(UUID customerId, UUID accountId) {
        runIfCustomerExists(customerId, () -> messageProducer.deleteCustomerAccount(accountId));
    }

    public List<UUID> getAllCustomerAccounts(UUID customerId) throws JMSException {
        return runIfCustomerExists(customerId, () -> messageProducer.getAllCustomerAccounts(customerId));
    }

    public void deposit(UUID customerId, UUID accountId, Double amount) {
        runIfCustomerExists(customerId, () -> messageProducer.deposit(accountId, amount));
    }

    public Double getCustomerAccountBalance(UUID customerId, UUID accountId) throws JMSException {
        return runIfCustomerExists(customerId, () -> messageProducer.getCustomerAccountBalance(accountId));
    }

    public Double getCustomerBalance(UUID customerId) throws JMSException {
        return runIfCustomerExists(customerId, () -> messageProducer.getCustomerBalance(customerId));
    }

    public List<TransactionDTO> getCustomerAccountTransactions(UUID customerId, UUID accountId) throws JMSException {
        return runIfCustomerExists(customerId, () -> messageProducer.getCustomerAccountTransactions(accountId));
    }
}
