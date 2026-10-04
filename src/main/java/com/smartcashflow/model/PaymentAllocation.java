package com.smartcashflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentAllocation {

    private Long allocationId;
    private String paymentId;
    private String invoiceId;
    private BigDecimal allocatedAmount;
    private LocalDateTime allocatedAt;

    public PaymentAllocation() {
    }

    public PaymentAllocation(String paymentId,
                             String invoiceId,
                             BigDecimal allocatedAmount,
                             LocalDateTime allocatedAt) {

        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.allocatedAmount = allocatedAmount;
        this.allocatedAt = allocatedAt;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
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

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }

    public void setAllocatedAt(LocalDateTime allocatedAt) {
        this.allocatedAt = allocatedAt;
    }

    @Override
    public String toString() {
        return "PaymentAllocation{" +
                "allocationId=" + allocationId +
                ", paymentId='" + paymentId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", allocatedAmount=" + allocatedAmount +
                ", allocatedAt=" + allocatedAt +
                '}';
    }
}