package com.smartcashflow.engine;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentAllocationTest {

    @Test
    void shouldAllocateFullPayment() {

        BigDecimal payment = new BigDecimal("5000.00");
        BigDecimal remainingInvoice = new BigDecimal("5000.00");

        BigDecimal allocation =
                payment.min(remainingInvoice);

        assertEquals(
                new BigDecimal("5000.00"),
                allocation
        );
    }

    @Test
    void shouldAllocatePartialPayment() {

        BigDecimal payment = new BigDecimal("3000.00");
        BigDecimal remainingInvoice = new BigDecimal("7000.00");

        BigDecimal allocation =
                payment.min(remainingInvoice);

        assertEquals(
                new BigDecimal("3000.00"),
                allocation
        );
    }

    @Test
    void shouldHandleOverpayment() {

        BigDecimal payment = new BigDecimal("8000.00");
        BigDecimal remainingInvoice = new BigDecimal("5000.00");

        BigDecimal allocation =
                payment.min(remainingInvoice);

        BigDecimal unallocated =
                payment.subtract(allocation);

        assertEquals(
                new BigDecimal("5000.00"),
                allocation
        );

        assertEquals(
                new BigDecimal("3000.00"),
                unallocated
        );
    }
}