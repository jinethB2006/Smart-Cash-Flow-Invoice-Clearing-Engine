
package com.smartcashflow.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.smartcashflow.dao.ClearingTransactionDAO;
import com.smartcashflow.dao.InvoiceDAO;
import com.smartcashflow.dao.PaymentDAO;
import com.smartcashflow.engine.InvoiceClearingEngine;

import com.smartcashflow.model.ClearingTransaction;
import com.smartcashflow.model.Invoice;
import com.smartcashflow.model.Payment;

import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DashboardApi {
    private final InvoiceClearingEngine clearingEngine = new InvoiceClearingEngine();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ClearingTransactionDAO transactionDAO = new ClearingTransactionDAO();

    

public void start() throws IOException {

    // Read the port assigned by the hosting provider
    int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8080")
    );

    // Bind to all network interfaces
    HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", port), 0
    );

    // Frontend
    server.createContext("/", this::handleStaticFiles);

    // Backend API
    server.createContext(
            "/api/dashboard", this::handleDashboard
    );

    server.createContext(
            "/api/invoices", this::handleInvoices
    );

    server.createContext(
            "/api/payments", this::handlePayments
    );

    server.createContext(
            "/api/transactions", this::handleTransactions
    );

    server.createContext(
            "/api/clearing/run", this::handleRunClearing
    );

    server.setExecutor(null);
    server.start();

    System.out.println(
            "Smart Cash Flow application running on port " + port
    );
}

     
private void handleStaticFiles(HttpExchange exchange)
        throws IOException {

    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        send(exchange, 405, "Method not allowed");
        return;
    }

    String path = exchange.getRequestURI().getPath();

    if ("/".equals(path)) {
        path = "/index.html";
    }

    // Only allow these three frontend files
    if (!path.equals("/index.html")
            && !path.equals("/style.css")
            && !path.equals("/script.js")) {

        send(exchange, 404, "File not found");
        return;
    }

    String resourcePath = "/static" + path;

    try (InputStream input =
                 getClass().getResourceAsStream(resourcePath)) {

        if (input == null) {
            send(exchange, 404, "File not found");
            return;
        }

        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();

        byte[] data = new byte[4096];
        int bytesRead;

        while ((bytesRead = input.read(data)) != -1) {
        buffer.write(data, 0, bytesRead);
        }

        byte[] bytes = buffer.toByteArray();

        String contentType =
                URLConnection.guessContentTypeFromName(path);

        if (path.endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (path.endsWith(".js")) {
            contentType = "application/javascript; charset=UTF-8";
        } else {
            contentType = "text/html; charset=UTF-8";
        }

        exchange.getResponseHeaders().set(
                "Content-Type", contentType
        );

        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }

    }
}

    // Dashboard summary
    private void handleDashboard(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {
            send(exchange, 405,
                    "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            List<Invoice> invoices =
                    invoiceDAO.getAllInvoices();

            List<Payment> payments =
                    paymentDAO.getAllPayments();

            BigDecimal invoiceTotal = BigDecimal.ZERO;
            BigDecimal paidTotal = BigDecimal.ZERO;

            int overdueCount = 0;
            int paidInvoiceCount = 0;
            int partialInvoiceCount = 0;
            int unallocatedPaymentCount = 0;

            for (Invoice invoice : invoices) {

                invoiceTotal = invoiceTotal.add(
                        invoice.getInvoiceAmount()
                );

                paidTotal = paidTotal.add(
                        invoice.getPaidAmount()
                );

                String status = invoice.getStatus();

                if ("OVERDUE".equals(status)) {
                    overdueCount++;
                } else if ("PAID".equals(status)) {
                    paidInvoiceCount++;
                } else if ("PARTIALLY_PAID".equals(status)) {
                    partialInvoiceCount++;
                }
            }

            for (Payment payment : payments) {

                String status = payment.getStatus().name();

                if ("UNALLOCATED".equals(status)
                        || "PARTIALLY_ALLOCATED".equals(status)) {
                    unallocatedPaymentCount++;
                }
            }

            String json = "{"
                    + "\"invoiceCount\":" + invoices.size() + ","
                    + "\"paymentCount\":" + payments.size() + ","
                    + "\"invoiceTotal\":"
                    + invoiceTotal.toPlainString() + ","
                    + "\"paidTotal\":"
                    + paidTotal.toPlainString() + ","
                    + "\"overdueInvoiceCount\":" + overdueCount + ","
                    + "\"paidInvoiceCount\":" + paidInvoiceCount + ","
                    + "\"partialInvoiceCount\":" + partialInvoiceCount + ","
                    + "\"unallocatedPaymentCount\":"
                    + unallocatedPaymentCount
                    + "}";

            send(exchange, 200, json);

        } catch (Exception e) {

            e.printStackTrace();

            send(exchange, 500,
                    "{\"error\":\"Unable to load dashboard\"}");
        }
    }

    // All invoices
    private void handleInvoices(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {
            send(exchange, 405,
                    "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            List<Invoice> invoices =
                    invoiceDAO.getAllInvoices();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Invoice invoice : invoices) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{")
                    .append("\"invoiceId\":\"")
                    .append(escape(invoice.getInvoiceId()))
                    .append("\",")
                    .append("\"customerId\":\"")
                    .append(escape(invoice.getCustomerId()))
                    .append("\",")
                    .append("\"amount\":")
                    .append(invoice.getInvoiceAmount().toPlainString())
                    .append(",")
                    .append("\"paidAmount\":")
                    .append(invoice.getPaidAmount().toPlainString())
                    .append(",")
                    .append("\"status\":\"")
                    .append(escape(invoice.getStatus()))
                    .append("\"")
                    .append("}");
            }

            json.append("]");
            send(exchange, 200, json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            send(exchange, 500,
                    "{\"error\":\"Unable to load invoices\"}");
        }
    }

    // All payments
    private void handlePayments(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {
            send(exchange, 405,
                    "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            List<Payment> payments =
                    paymentDAO.getAllPayments();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Payment payment : payments) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{")
                    .append("\"paymentId\":\"")
                    .append(escape(payment.getPaymentId()))
                    .append("\",")
                    .append("\"customerId\":\"")
                    .append(escape(payment.getCustomerId()))
                    .append("\",")
                    .append("\"amount\":")
                    .append(payment.getPaymentAmount().toPlainString())
                    .append(",")
                    .append("\"status\":\"")
                    .append(escape(payment.getStatus().name()))
                    .append("\"")
                    .append("}");
            }

            json.append("]");
            send(exchange, 200, json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            send(exchange, 500,
                    "{\"error\":\"Unable to load payments\"}");
        }
    }

    // All clearing transactions
    private void handleTransactions(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {
            send(exchange, 405,
                    "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            List<ClearingTransaction> transactions =
                    transactionDAO.getAllTransactions();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (ClearingTransaction transaction : transactions) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{")
                    .append("\"paymentId\":\"")
                    .append(escape(transaction.getPaymentId()))
                    .append("\",")
                    .append("\"invoiceId\":\"")
                    .append(escape(transaction.getInvoiceId()))
                    .append("\",")
                    .append("\"action\":\"")
                    .append(escape(transaction.getAction()))
                    .append("\",")
                    .append("\"amount\":")
                    .append(transaction.getAmount().toPlainString())
                    .append("}");
            }

            json.append("]");
            send(exchange, 200, json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            send(exchange, 500,
                    "{\"error\":\"Unable to load transactions\"}");
        }
    }

    // Send JSON response
    private void send(
            HttpExchange exchange,
            int status,
            String body
    ) throws IOException {

        byte[] bytes =
                body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        // Local development only.
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
        "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
);
exchange.getResponseHeaders().set(
        "Access-Control-Allow-Headers", "Content-Type"
);

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output =
                     exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    // Escape strings before adding them to JSON
    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    

private void handleRunClearing(HttpExchange exchange)
        throws IOException {

    // Handle browser CORS preflight request first
    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
        );
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
        );

        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    // Existing POST request check
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.getResponseHeaders().set("Allow", "POST");
        send(exchange, 405,
                "{\"success\":false,"
                + "\"message\":\"Use POST to run clearing\"}");
        return;
    }

    try {
        clearingEngine.processPayments();

        send(exchange, 200,
                "{\"success\":true,"
                + "\"message\":\"Clearing process completed. "
                + "Refresh the dashboard to view the results.\"}");

    } catch (Exception e) {
        e.printStackTrace();

        send(exchange, 500,
                "{\"success\":false,"
                + "\"message\":\"Clearing process failed\"}");
    }
}

}
