package com.banking.customer.util;

import javax.jms.JMSException;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws JMSException;
}
