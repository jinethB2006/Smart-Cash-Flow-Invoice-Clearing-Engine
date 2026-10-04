package com.smartcashflow.engine;

import com.smartcashflow.model.Invoice;
import com.smartcashflow.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public class PaymentMatchingEngine {

    public Invoice findExactInvoiceMatch(
            Payment payment,
            List<Invoice> invoices
    ) {

        if (payment.getPaymentReference() == null) {
            return null;
        }

        String reference =
                payment.getPaymentReference().trim();

        return invoices.stream()
                .filter(invoice ->
                        invoice.getInvoiceId()
                                .equalsIgnoreCase(reference)
                )
                .filter(invoice ->
                        invoice.getRemainingAmount()
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .findFirst()
                .orElse(null);
    }

    public Invoice findCustomerAmountMatch(
            Payment payment,
            List<Invoice> invoices
    ) {

        return invoices.stream()
                .filter(invoice ->
                        invoice.getRemainingAmount()
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .filter(invoice ->
                        invoice.getRemainingAmount()
                                .compareTo(
                                        payment.getPaymentAmount()
                                ) == 0
                )
                .findFirst()
                .orElse(null);
    }
}