package com.smartcashflow.dao;

import com.smartcashflow.model.PaymentAllocation;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentAllocationDAO {

    public List<PaymentAllocation> getAllAllocations() {

        List<PaymentAllocation> allocations = new ArrayList<>();

        String sql =
                "SELECT allocation_id, " +
                "payment_id, " +
                "invoice_id, " +
                "allocated_amount, " +
                "allocated_at " +
                "FROM payment_allocations";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                PaymentAllocation allocation =
                        new PaymentAllocation();

                allocation.setAllocationId(
                        resultSet.getLong("allocation_id")
                );

                allocation.setPaymentId(
                        resultSet.getString("payment_id")
                );

                allocation.setInvoiceId(
                        resultSet.getString("invoice_id")
                );

                allocation.setAllocatedAmount(
                        resultSet.getBigDecimal("allocated_amount")
                );

                if (resultSet.getTimestamp("allocated_at") != null) {
                    allocation.setAllocatedAt(
                            resultSet.getTimestamp("allocated_at")
                                    .toLocalDateTime()
                    );
                }

                allocations.add(allocation);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return allocations;
    }

    public void insertAllocation(
            Connection connection,
            PaymentAllocation allocation
    ) throws Exception {

        String sql =
                "INSERT INTO payment_allocations " +
                "(payment_id, invoice_id, allocated_amount) " +
                "VALUES (?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    allocation.getPaymentId()
            );

            statement.setString(
                    2,
                    allocation.getInvoiceId()
            );

            statement.setBigDecimal(
                    3,
                    allocation.getAllocatedAmount()
            );

            statement.executeUpdate();
        }
    }
}