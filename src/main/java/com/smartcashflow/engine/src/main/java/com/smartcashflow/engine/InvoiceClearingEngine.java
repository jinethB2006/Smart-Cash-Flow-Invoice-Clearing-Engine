package com.smartcashflow.engine;

import com.smartcashflow.dao.ClearingExceptionDAO;
import com.smartcashflow.dao.ClearingTransactionInsertDAO;
import com.smartcashflow.dao.InvoiceUpdateDAO;
import com.smartcashflow.dao.PaymentAllocationDAO;
import com.smartcashflow.dao.PaymentUpdateDAO;
import com.smartcashflow.enums.PaymentStatus;
import com.smartcashflow.model.ClearingException;
import com.smartcashflow.model.ClearingTransaction;
import com.smartcashflow.model.Invoice;
import com.smartcashflow.model.Payment;
import com.smartcashflow.model.PaymentAllocation;
import com.smartcashflow.service.InvoiceService;
import com.smartcashflow.service.PaymentService;
import com.smartcashflow.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class InvoiceClearingEngine {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    private final PaymentAllocationDAO allocationDAO;
    private final InvoiceUpdateDAO invoiceUpdateDAO;
    private final PaymentUpdateDAO paymentUpdateDAO;
    private final ClearingTransactionInsertDAO transactionDAO;
    private final PaymentMatchingEngine matchingEngine;
    private final ClearingExceptionDAO exceptionDAO;

    public InvoiceClearingEngine() {

        this.invoiceService = new InvoiceService();
        this.paymentService = new PaymentService();

        this.allocationDAO = new PaymentAllocationDAO();
        this.invoiceUpdateDAO = new InvoiceUpdateDAO();
        this.paymentUpdateDAO = new PaymentUpdateDAO();
        this.transactionDAO = new ClearingTransactionInsertDAO();
        this.matchingEngine = new PaymentMatchingEngine();
        this.exceptionDAO = new ClearingExceptionDAO();
    }

    public void processPayments() {

        List<Payment> payments =
                paymentService.getUnallocatedPayments();

        Map<String, List<Invoice>> customerInvoiceMap =
                new HashMap<>();

        for (Payment payment : payments) {

            String customerId =
                    payment.getCustomerId();

            List<Invoice> invoices =
                    invoiceService.getCustomerInvoices(
                            customerId
                    );

            customerInvoiceMap.put(
                    customerId,
                    invoices
            );
        }

        for (Payment payment : payments) {

            processSinglePayment(
                    payment,
                    customerInvoiceMap.get(
                            payment.getCustomerId()
                    )
            );
        }
    }

    private void processSinglePayment(
            Payment payment,
            List<Invoice> invoices
    ) {

        if (invoices == null || invoices.isEmpty()) {

            System.out.println(
                    "No invoices found for customer: " +
                    payment.getCustomerId()
            );

            saveException(
                    payment,
                    "NO_OPEN_INVOICES"
            );

            return;
        }

        Invoice exactMatch =
                matchingEngine.findExactInvoiceMatch(
                        payment,
                        invoices
                );

        if (exactMatch != null) {

            System.out.println(
                    "Exact invoice reference match found: " +
                    exactMatch.getInvoiceId()
            );

            invoices = Arrays.asList(exactMatch);

        } else {

            Invoice amountMatch =
                    matchingEngine.findCustomerAmountMatch(
                            payment,
                            invoices
                    );

            if (amountMatch != null) {

                System.out.println(
                        "Customer + amount match found: " +
                        amountMatch.getInvoiceId()
                );

                invoices = Arrays.asList(amountMatch);
            }
        }

        PriorityQueue<Invoice> invoiceQueue =
                new PriorityQueue<>(
                        Comparator
                                .comparing(Invoice::getDueDate)
                                .thenComparing(
                                        Invoice::getInvoiceId
                                )
                );

        for (Invoice invoice : invoices) {

            if (
                    invoice.getRemainingAmount()
                            .compareTo(BigDecimal.ZERO) > 0
            ) {

                invoiceQueue.offer(invoice);
            }
        }

        BigDecimal remainingPayment =
                payment.getPaymentAmount();

        Connection connection = null;

        try {

            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            while (
                    !invoiceQueue.isEmpty() &&
                    remainingPayment.compareTo(
                            BigDecimal.ZERO
                    ) > 0
            ) {

                Invoice invoice =
                        invoiceQueue.poll();

                BigDecimal remainingInvoice =
                        invoice.getRemainingAmount();

                BigDecimal allocationAmount =
                        remainingPayment.min(
                                remainingInvoice
                        );

                PaymentAllocation allocation =
                        new PaymentAllocation(
                                payment.getPaymentId(),
                                invoice.getInvoiceId(),
                                allocationAmount,
                                null
                        );

                allocationDAO.insertAllocation(
                        connection,
                        allocation
                );

                invoice.setPaidAmount(
                        invoice.getPaidAmount()
                                .add(allocationAmount)
                );

                if (
                        invoice.getPaidAmount()
                                .compareTo(
                                        invoice.getInvoiceAmount()
                                ) >= 0
                ) {

                    invoice.setStatus("PAID");

                } else {

                    invoice.setStatus("PARTIALLY_PAID");
                }

                invoiceUpdateDAO.updateInvoice(
                        connection,
                        invoice
                );

                remainingPayment =
                        remainingPayment.subtract(
                                allocationAmount
                        );

                System.out.println(
                        "Payment " +
                        payment.getPaymentId() +
                        " -> Invoice " +
                        invoice.getInvoiceId() +
                        " = " +
                        allocationAmount
                );

                ClearingTransaction transaction =
                        new ClearingTransaction(
                                payment.getPaymentId(),
                                invoice.getInvoiceId(),
                                "AUTO_CLEAR",
                                allocationAmount,
                                "Payment automatically allocated to invoice",
                                LocalDateTime.now()
                        );

                transactionDAO.insertTransaction(
                        connection,
                        transaction
                );
            }

            BigDecimal originalPayment =
                    payment.getPaymentAmount();

            if (
                    remainingPayment.compareTo(
                            BigDecimal.ZERO
                    ) == 0
            ) {

                payment.setStatus(
                        PaymentStatus.FULLY_ALLOCATED
                );

            } else if (
                    remainingPayment.compareTo(
                            originalPayment
                    ) < 0
            ) {

                payment.setStatus(
                        PaymentStatus.PARTIALLY_ALLOCATED
                );

                saveException(
                        payment,
                        remainingPayment,
                        "PARTIAL_PAYMENT_REMAINING"
                );

            } else {

                payment.setStatus(
                        PaymentStatus.UNALLOCATED
                );

                saveException(
                        payment,
                        remainingPayment,
                        "UNALLOCATED_PAYMENT"
                );
            }

            paymentUpdateDAO.updatePaymentStatus(
                    connection,
                    payment
            );

            if (
                    remainingPayment.compareTo(
                            BigDecimal.ZERO
                    ) > 0
            ) {

                System.out.println(
                        "Overpayment amount: " +
                        remainingPayment
                );

                saveException(
                        payment,
                        remainingPayment,
                        "OVERPAYMENT"
                );
            }

            connection.commit();

            System.out.println(
                    "Payment cleared successfully: " +
                    payment.getPaymentId()
            );

        } catch (Exception e) {

            System.out.println(
                    "Clearing failed for payment: " +
                    payment.getPaymentId()
            );

            if (connection != null) {

                try {
                    connection.rollback();

                    System.out.println(
                            "Transaction rolled back for payment: " +
                            payment.getPaymentId()
                    );

                } catch (Exception rollbackException) {

                    System.out.println(
                            "Rollback failed"
                    );

                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

        } finally {

            if (connection != null) {

                try {
                    connection.close();

                } catch (Exception closeException) {

                    System.out.println(
                            "Failed to close database connection"
                    );

                    closeException.printStackTrace();
                }
            }
        }
    }

    private void saveException(
            Payment payment,
            String reason
    ) {

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            ClearingException exception =
                    new ClearingException(
                            payment.getPaymentId(),
                            payment.getCustomerId(),
                            payment.getPaymentAmount(),
                            reason,
                            LocalDateTime.now()
                    );

            exceptionDAO.insertException(
                    connection,
                    exception
            );

            System.out.println(
                    "Payment moved to exception queue: " +
                    payment.getPaymentId()
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to save clearing exception"
            );

            e.printStackTrace();
        }
    }

    private void saveException(
            Payment payment,
            BigDecimal amount,
            String reason
    ) {

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            ClearingException exception =
                    new ClearingException(
                            payment.getPaymentId(),
                            payment.getCustomerId(),
                            amount,
                            reason,
                            LocalDateTime.now()
                    );

            exceptionDAO.insertException(
                    connection,
                    exception
            );

            System.out.println(
                    "Exception recorded: " +
                    payment.getPaymentId() +
                    " -> " +
                    amount
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to save clearing exception"
            );

            e.printStackTrace();
        }
    }
}