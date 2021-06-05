package ru.netology.data;

import lombok.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import ru.netology.data.entity.CreditRequestEntity;
import ru.netology.data.entity.OrderEntity;
import ru.netology.data.entity.PaymentEntity;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNull;

public class SQLHelper {
    private static String url = System.getProperty("test.dburl");
    private static String user = System.getProperty("test.dblogin");
    private static String password = System.getProperty("test.dbpassword");

    private SQLHelper() {}

    @SneakyThrows
    public static void cleanData() {
        QueryRunner runner = new QueryRunner();
        val cleanCreditRequest = "DELETE FROM credit_request_entity;";
        val cleanPayment = "DELETE FROM payment_entity;";
        val cleanOrder = "DELETE FROM order_entity;";

        try (
                val conn = DriverManager.getConnection(url, user, password);
        ) {
            runner.update(conn, cleanCreditRequest);
            runner.update(conn, cleanPayment);
            runner.update(conn, cleanOrder);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SneakyThrows
    public static PaymentEntity payData() {
        QueryRunner runner = new QueryRunner();
        val reqStatus = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (
                val conn = DriverManager.getConnection(url, user, password);
        ) {
            val payData = runner.query(conn, reqStatus, new BeanHandler<>(PaymentEntity.class));
            return payData;
        }
    }

    @SneakyThrows
    public static CreditRequestEntity creditData(){
        val selectStatus = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            val creditData = runner.query(conn, selectStatus, new BeanHandler<>(CreditRequestEntity.class));
            return creditData;
        }
    }

    @SneakyThrows
    public static OrderEntity orderData() {
        val selectStatus = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            val orderData = runner.query(conn, selectStatus, new BeanHandler<>(OrderEntity.class));
            return orderData;
        }
    }

    @SneakyThrows
    public static void checkEmptyOrderEntity() {
        val orderRequest = "SELECT * FROM order_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            val orderBlock = runner.query(conn, orderRequest, new BeanHandler<>(OrderEntity.class));
            assertNull(orderBlock);
        }
    }

    @SneakyThrows
    public static void checkEmptyPaymentEntity(){
        val orderRequest = "SELECT * FROM payment_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            val paymentBlock = runner.query(conn, orderRequest, new BeanHandler<>(PaymentEntity.class));
            assertNull(paymentBlock);
        }
    }

    @SneakyThrows
    public static void checkEmptyCreditEntity(){
        val orderRequest = "SELECT * FROM credit_request_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            val creditBlock = runner.query(conn, orderRequest, new BeanHandler<>(CreditRequestEntity.class));
            assertNull(creditBlock);
        }
    }

}
