package com.banking.customer.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID id) {
        super(String.format("User with id '%s' not found", id));
    }
}
