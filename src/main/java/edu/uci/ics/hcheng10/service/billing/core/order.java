package edu.uci.ics.hcheng10.service.billing.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.core.*;
import edu.uci.ics.hcheng10.service.billing.BillingService;
import edu.uci.ics.hcheng10.service.billing.configs.IdmConfigs;
import edu.uci.ics.hcheng10.service.billing.configs.MoviesConfigs;
import edu.uci.ics.hcheng10.service.billing.logger.ServiceLogger;
import edu.uci.ics.hcheng10.service.billing.model.retrieveModel;
import edu.uci.ics.hcheng10.service.billing.model.transactionModel;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class order {
    private static IdmConfigs idm = BillingService.getIdmConfigs();
    private static String idmServicePath = idm.getScheme()+idm.getHostName()+":"+idm.getPort()+idm.getPath();
    private static String idmEndpointPath = idm.getPrivilegePath();

    private static MoviesConfigs movies = BillingService.getMoviesConfigs();
    private static String movieServicePath = movies.getScheme()+movies.getHostName()+":"+movies.getPort()+movies.getPath();
    private static String moviesEndpointPath = movies.getThumbnailPath();

    public static Float getAmount(String email) {
        String query = "SELECT quantity, unit_price, discount FROM cart c INNER JOIN movie_price mp on c.movie_id = mp.movie_id WHERE c.email LIKE ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Get total amount: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            Boolean found = false;
            Float totalAmount = new Float(0);
            while (rs.next()) {
                found = true;
                Integer quantity = rs.getInt(1);
                Float unit_price = rs.getFloat(2);
                Float discount  = rs.getFloat(3);
                totalAmount += quantity*unit_price*discount;
            }
            return totalAmount;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Get amount failed.");
            e.printStackTrace();
        }
        return null;
    }
    public static Integer placeOrder(String email, String token) {
        String query0 = "SELECT movie_id, quantity FROM cart WHERE email LIKE ?";
        String query1 = "INSERT INTO sale(email, movie_id, quantity, sale_date) VALUE (?, ?, ?, ?)";
        String query2 = "INSERT INTO transaction(sale_id, token) VALUE (?, ?)";
        try {
            PreparedStatement ps0 = BillingService.getCon().prepareStatement(query0);
            ps0.setString(1, email);
            ResultSet rs0 = ps0.executeQuery();
            Boolean found = false;
            while (rs0.next()) {
                ServiceLogger.LOGGER.info("Get movie form cart " + rs0.toString());
                found = true;
                String movie_id = rs0.getString(1);
                Integer quantity = rs0.getInt(2);
                PreparedStatement ps = BillingService.getCon().prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, email);
                ps.setString(2, movie_id);
                ps.setInt(3, quantity);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ServiceLogger.LOGGER.info("Inserting into sale "+ ps.toString());
                ps.executeUpdate();
                ResultSet rs1 = ps.getGeneratedKeys();
                rs1.next();
                Integer sale_id = rs1.getInt(1);


                PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
                ps2.setInt(1, sale_id);
                ps2.setString(2, token);
                ServiceLogger.LOGGER.info("Inserting into transaction " + ps2.toString());
                ps2.executeUpdate();
            }
            if (!found) return 312;
            else return 3400;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            return 342;
        }
    }

    public static Integer completeOrder(String token, String capture_id) {
        String query = "UPDATE transaction \n" +
                "SET capture_id = ?\n" +
                "WHERE token = ?";
        Integer sale_id;
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, capture_id);
            ps.setString(2, token);
            ServiceLogger.LOGGER.info("Mark transaction as completed: " + ps.toString());
            Integer numAffected = ps.executeUpdate();
            if (numAffected.equals(0)) return 3421;
            String clearQuery0 = "SELECT cart_id\n" +
                    "FROM cart INNER JOIN sale s on cart.email = s.email INNER JOIN transaction t on s.sale_id = t.sale_id\n" +
                    "WHERE token LIKE ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(clearQuery0);
            ps1.setString(1, token);
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                sale_id = rs.getInt(1);
                String clearQuery1 = "DELETE FROM cart WHERE cart_id = ?";
                PreparedStatement ps2 = BillingService.getCon().prepareStatement(clearQuery1);
                ps2.setInt(1, sale_id);
                ps2.executeUpdate();
            }
            return 3420;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Order can not be completed.");
            e.printStackTrace();
            return 3422;
        }
    }

    public static transactionModel retrieveOrder(String email, PayPalOrderClient client) {
        Integer resultCode = 313;
        String message = "Order history does not exist.";
        transactionModel responseModel;
        String query = "SELECT token, capture_id FROM transaction t INNER JOIN sale s on t.sale_id = s.sale_id WHERE s.email LIKE ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Getting all tokens: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            List<transactionModel.transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Boolean noTranFee = false;
                resultCode = 3410;
                message = "Orders retrieved successfully.";
                String token = rs.getString(1);
                String capture_id = rs.getString(2);
                JsonNode response = client.getOrder(token);
                String status = response.get("status").textValue();
                if (status.equals("CREATED")) noTranFee = true;
                String total = response.get("purchase_units").get(0).get("amount").get("value").textValue();
                String currency = response.get("purchase_units").get(0).get("amount").get("currency_code").textValue();
                transactionModel.transaction.amount amount = new transactionModel.transaction.amount(total, currency);
                transactionModel.transaction.transaction_fee transaction_fee = null;
                if (!noTranFee) {
                    String tvalue = response.get("purchase_units").get(0).get("payments").get("captures").get(0).get("seller_receivable_breakdown").get("paypal_fee").get("value").textValue();
                    String tcurrency = response.get("purchase_units").get(0).get("payments").get("captures").get(0).get("seller_receivable_breakdown").get("paypal_fee").get("currency_code").textValue();
                    transaction_fee = new transactionModel.transaction.transaction_fee(tvalue, tcurrency);
                }
                String capture_time = response.get("create_time").textValue();
                String update_time = null;
                if (!noTranFee) update_time = response.get("update_time").textValue();
                String getItems = "SELECT email, s.movie_id, quantity, unit_price, discount, sale_date\n" +
                        "FROM sale s INNER JOIN movie_price mp on s.movie_id = mp.movie_id\n" +
                        "WHERE email LIKE ?";
                PreparedStatement ps1 = BillingService.getCon().prepareStatement(getItems);
                ps1.setString(1, email);
                ResultSet rs1 = ps1.executeQuery();
                List<transactionModel.transaction.item> items = new ArrayList<>();
                while (rs1.next()) {
                    String temail = rs1.getString(1);
                    String mid = rs1.getString(2);
                    Integer tquantity = rs1.getInt(3);
                    Float uprice = rs1.getFloat(4);
                    Float discount = rs1.getFloat(5);
                    String sale_date = rs1.getTimestamp(6).toString();
                    items.add(new transactionModel.transaction.item(temail, mid, tquantity, uprice, discount, sale_date));
                }
                transactionModel.transaction.item cItems[] = new transactionModel.transaction.item[items.size()];
                cItems = items.toArray(cItems);
                transactionModel.transaction transaction = new transactionModel.transaction(capture_id, status, amount, transaction_fee, capture_time, update_time, cItems);
                transactions.add(transaction);
            }
            transactionModel.transaction[] transactionsS = new transactionModel.transaction[transactions.size()];
            transactionsS = transactions.toArray(transactionsS);
            responseModel = new transactionModel(resultCode, message, transactionsS);
            return responseModel;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Order can not be retrieved.");
            e.printStackTrace();
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Order can not be retrieved by Paypal.");
            e.printStackTrace();
        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Inner logic error.");
            e.printStackTrace();
        }
        return null;
    }
}
