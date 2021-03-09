package com.banking.customer.controller;

import com.banking.customer.dto.TransactionDTO;
import com.banking.customer.model.Customer;
import com.banking.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Account management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Add customer", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Customer.class)))})
    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.save(new Customer(UUID.randomUUID(), customer.getName()));
    }

    @Operation(summary = "Remove customer by id", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200")})
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable UUID id) {
        customerService.remove(id);
    }

    @Operation(summary = "Get all customers", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Customer.class))))})
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @Operation(summary = "Add customer account", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @PostMapping("/customers/{customerId}/accounts")
    public void createCustomerAccount(@PathVariable UUID customerId) {
        customerService.createCustomerAccount(customerId);
    }

    @Operation(summary = "Remove customer account", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @DeleteMapping("/customers/{customerId}/accounts/{accountId}")
    public void deleteCustomerAccount(@PathVariable UUID customerId, @PathVariable UUID accountId) {
        customerService.deleteCustomerAccount(customerId, accountId);
    }

    @Operation(summary = "Get all customer accounts", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UUID.class)))),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @GetMapping("/customers/{customerId}/accounts")
    public List<UUID> getAllCustomerAccounts(@PathVariable UUID customerId) throws JMSException {
        return customerService.getAllCustomerAccounts(customerId);
    }

    @Operation(summary = "Deposit to customer account", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200"),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @PostMapping("/customers/{customerId}/accounts/{accountId}/deposit/{amount}")
    public void depositToAccount(@PathVariable UUID customerId, @PathVariable UUID accountId, @PathVariable Double amount) {
        customerService.deposit(customerId, accountId, amount);
    }

    @Operation(summary = "Withdrawal from customer account", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200"),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @PostMapping("/customers/{customerId}/accounts/{accountId}/withdrawal/{amount}")
    public void withdrawalFromAccount(@PathVariable UUID customerId, @PathVariable UUID accountId, @PathVariable Double amount) {
        customerService.deposit(customerId, accountId, -amount);
    }

    @Operation(summary = "Get customer account balance", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Double.class))),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @GetMapping("/customers/{customerId}/accounts/{accountId}/balance")
    public Double getCustomerAccountBalance(@PathVariable UUID customerId, @PathVariable UUID accountId) throws JMSException {
        return customerService.getCustomerAccountBalance(customerId, accountId);
    }

    @Operation(summary = "Get customer balance", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Double.class))),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @GetMapping("/customers/{customerId}/total")
    public Double getCustomerBalance(@PathVariable UUID customerId) throws JMSException {
        return customerService.getCustomerBalance(customerId);
    }

    @Operation(summary = "Get customer account operations", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = TransactionDTO.class)))),
            @ApiResponse(description = "Jms problem", responseCode = "204"),
            @ApiResponse(description = "Customer not found", responseCode = "404")})
    @GetMapping("/customers/{customerId}/accounts/{accountId}/transactions")
    public List<TransactionDTO> getCustomerAccountTransactions(@PathVariable UUID customerId, @PathVariable UUID accountId) throws JMSException {
        return customerService.getCustomerAccountTransactions(customerId, accountId);
    }

}
