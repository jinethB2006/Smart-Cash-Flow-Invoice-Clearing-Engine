package com.smartcashflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClearingTransaction {

    private Long transactionId;
    private String paymentId;
    private String invoiceId;
    private String action;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

    public ClearingTransaction() {
    }

    public ClearingTransaction(String paymentId,
                               String invoiceId,
                               String action,
                               BigDecimal amount,
                               String description,
                               LocalDateTime createdAt) {

        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.action = action;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ClearingTransaction{" +
                "transactionId=" + transactionId +
                ", paymentId='" + paymentId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", action='" + action + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}