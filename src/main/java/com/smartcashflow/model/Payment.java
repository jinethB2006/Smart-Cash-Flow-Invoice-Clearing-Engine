package com.smartcashflow.model;

import com.smartcashflow.enums.PaymentMethod;
import com.smartcashflow.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {

    private String paymentId;
    private String customerId;
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
    private String paymentReference;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;

    public Payment() {
    }

    public Payment(String paymentId,
                   String customerId,
                   LocalDate paymentDate,
                   BigDecimal paymentAmount,
                   String paymentReference,
                   PaymentMethod paymentMethod,
                   PaymentStatus status) {

        this.paymentId = paymentId;
        this.customerId = customerId;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentReference = paymentReference;
        this.paymentMethod = paymentMethod;
        this.status = status;
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

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", paymentDate=" + paymentDate +
                ", paymentAmount=" + paymentAmount +
                ", paymentReference='" + paymentReference + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                '}';
    }
}