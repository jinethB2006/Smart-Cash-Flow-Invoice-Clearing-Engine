package com.smartcashflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClearingException {

    private Long exceptionId;
    private String paymentId;
    private String customerId;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime createdAt;

    public ClearingException() {
    }

    public ClearingException(
            String paymentId,
            String customerId,
            BigDecimal amount,
            String reason,
            LocalDateTime createdAt) {

        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Long exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ClearingException{" +
                "exceptionId=" + exceptionId +
                ", paymentId='" + paymentId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}