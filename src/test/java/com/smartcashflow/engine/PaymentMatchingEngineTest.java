package com.smartcashflow.engine;

import com.smartcashflow.enums.PaymentMethod;
import com.smartcashflow.enums.PaymentStatus;
import com.smartcashflow.model.Invoice;
import com.smartcashflow.model.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMatchingEngineTest {

    @Test
    void shouldFindExactInvoiceMatch() {

        Invoice invoice = new Invoice(
                "INV001",
                "CUST001",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 20),
                new BigDecimal("5000.00"),
                new BigDecimal("0.00"),
                "PENDING"
        );

        Payment payment = new Payment(
                "PAY001",
                "CUST001",
                LocalDate.of(2026, 9, 20),
                new BigDecimal("5000.00"),
                "INV001",
                PaymentMethod.BANK_TRANSFER,
                PaymentStatus.UNALLOCATED
        );

        List<Invoice> invoices =
                Arrays.asList(invoice);

        PaymentMatchingEngine engine =
                new PaymentMatchingEngine();

        Invoice result =
                engine.findExactInvoiceMatch(
                        payment,
                        invoices
                );

        assertNotNull(result);
        assertEquals(
                "INV001",
                result.getInvoiceId()
        );
    }

    @Test
    void shouldReturnNullWhenExactMatchDoesNotExist() {

        Invoice invoice = new Invoice(
                "INV001",
                "CUST001",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 20),
                new BigDecimal("5000.00"),
                new BigDecimal("0.00"),
                "PENDING"
        );

        Payment payment = new Payment(
                "PAY002",
                "CUST001",
                LocalDate.of(2026, 9, 26),
                new BigDecimal("6000.00"),
                "CUST001",
                PaymentMethod.UPI,
                PaymentStatus.UNALLOCATED
        );

        List<Invoice> invoices =
                Arrays.asList(invoice);

        PaymentMatchingEngine engine =
                new PaymentMatchingEngine();

        Invoice result =
                engine.findExactInvoiceMatch(
                        payment,
                        invoices
                );

        assertNull(result);
    }

    @Test
    void shouldFindCustomerAmountMatch() {

        Invoice invoice = new Invoice(
                "INV004",
                "CUST002",
                LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 22),
                new BigDecimal("10000.00"),
                new BigDecimal("0.00"),
                "PENDING"
        );

        Payment payment = new Payment(
                "PAY003",
                "CUST002",
                LocalDate.of(2026, 9, 23),
                new BigDecimal("10000.00"),
                "CUST002",
                PaymentMethod.BANK_TRANSFER,
                PaymentStatus.UNALLOCATED
        );

        List<Invoice> invoices =
                Arrays.asList(invoice);

        PaymentMatchingEngine engine =
                new PaymentMatchingEngine();

        Invoice result =
                engine.findCustomerAmountMatch(
                        payment,
                        invoices
                );

        assertNotNull(result);
        assertEquals(
                "INV004",
                result.getInvoiceId()
        );
    }
}