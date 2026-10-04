package com.smartcashflow.dao;

import com.smartcashflow.model.Customer;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT customer_id, customer_name, email, phone, status FROM customers";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Customer customer = new Customer();

                customer.setCustomerId(resultSet.getString("customer_id"));
                customer.setCustomerName(resultSet.getString("customer_name"));
                customer.setEmail(resultSet.getString("email"));
                customer.setPhone(resultSet.getString("phone"));
                customer.setStatus(resultSet.getString("status"));

                customers.add(customer);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return customers;
    }
}