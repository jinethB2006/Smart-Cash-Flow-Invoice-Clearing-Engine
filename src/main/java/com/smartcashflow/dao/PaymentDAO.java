package com.smartcashflow.dao;

import com.smartcashflow.enums.PaymentMethod;
import com.smartcashflow.enums.PaymentStatus;
import com.smartcashflow.model.Payment;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<Payment> getAllPayments() {

        List<Payment> payments = new ArrayList<>();

        String sql =
                "SELECT payment_id, " +
                "customer_id, " +
                "payment_date, " +
                "payment_amount, " +
                "payment_reference, " +
                "payment_method, " +
                "status " +
                "FROM payments";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Payment payment = new Payment();

                payment.setPaymentId(
                        resultSet.getString("payment_id")
                );

                payment.setCustomerId(
                        resultSet.getString("customer_id")
                );

                payment.setPaymentDate(
                        resultSet.getDate("payment_date").toLocalDate()
                );

                payment.setPaymentAmount(
                        resultSet.getBigDecimal("payment_amount")
                );

                payment.setPaymentReference(
                        resultSet.getString("payment_reference")
                );

                payment.setPaymentMethod(
                        PaymentMethod.valueOf(
                                resultSet.getString("payment_method")
                        )
                );

                payment.setStatus(
                        PaymentStatus.valueOf(
                                resultSet.getString("status")
                        )
                );

                payments.add(payment);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return payments;
    }
}