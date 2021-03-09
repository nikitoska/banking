package com.banking.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.jms.JMSException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomerNofFoundException(CustomerNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(JMSException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String handleJMSException(JMSException e) {
        return e.getMessage();
    }

}
