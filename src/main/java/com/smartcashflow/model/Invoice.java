package com.smartcashflow.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {

    private String invoiceId;
    private String customerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal invoiceAmount;
    private BigDecimal paidAmount;
    private String status;

    public Invoice() {
    }

    public Invoice(String invoiceId,
                   String customerId,
                   LocalDate invoiceDate,
                   LocalDate dueDate,
                   BigDecimal invoiceAmount,
                   BigDecimal paidAmount,
                   String status) {

        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.invoiceAmount = invoiceAmount;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRemainingAmount() {
        return invoiceAmount.subtract(paidAmount);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", dueDate=" + dueDate +
                ", invoiceAmount=" + invoiceAmount +
                ", paidAmount=" + paidAmount +
                ", remainingAmount=" + getRemainingAmount() +
                ", status='" + status + '\'' +
                '}';
    }
}