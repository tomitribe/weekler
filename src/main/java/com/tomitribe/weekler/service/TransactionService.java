package com.tomitribe.weekler.service;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;

import java.util.function.Supplier;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Singleton
@ConcurrencyManagement(BEAN)
public class TransactionService {
    @TransactionAttribute(REQUIRES_NEW)
    public <T> T execute(final Supplier<T> supplier) {
        return supplier.get();
    }
}
