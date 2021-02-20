package edu.uci.ics.hcheng10.service.billing.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hcheng10.service.billing.BillingService;
import edu.uci.ics.hcheng10.service.billing.configs.IdmConfigs;
import edu.uci.ics.hcheng10.service.billing.configs.MoviesConfigs;
import edu.uci.ics.hcheng10.service.billing.logger.ServiceLogger;
import edu.uci.ics.hcheng10.service.billing.model.*;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class cart {
    private static IdmConfigs idm = BillingService.getIdmConfigs();
    private static String idmServicePath = idm.getScheme()+idm.getHostName()+":"+idm.getPort()+idm.getPath();
    private static String idmEndpointPath = idm.getPrivilegePath();

    private static MoviesConfigs movies = BillingService.getMoviesConfigs();
    private static String movieServicePath = movies.getScheme()+movies.getHostName()+":"+movies.getPort()+movies.getPath();
    private static String moviesEndpointPath = movies.getThumbnailPath();

    public static Integer getPrivilege(String email) {
        plevelRequestModel requestModel = new plevelRequestModel(email, 4);
        ResponseModel responseModel = null;

        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(idmServicePath).path(idmEndpointPath);

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, ResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }

        ServiceLogger.LOGGER.info("The resultCode is " + responseModel.getResultCode());
        return responseModel.getResultCode();
    }

    public static thumbnailResponseModel getMovies(String[] movie_ids) {
        thumbnailRequestModel requestModel = new thumbnailRequestModel(movie_ids);
        thumbnailResponseModel responseModel = null;
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(movieServicePath).path(moviesEndpointPath);

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, thumbnailResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }

        ServiceLogger.LOGGER.info("The resultCode is " + responseModel.getResultCode());
        return responseModel;
    }

    public static Integer insertIntoCart(String email, String movie_id, Integer quantity) {
        String query = "INSERT INTO cart(email, movie_id, quantity) VALUE (?, ?, ?)";
        String check = "SELECT movie_id FROM movie WHERE movie_id LIKE ?";
        if (quantity <= 0) return 33;
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(check);
            ps1.setString(1, movie_id);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) return 3150;
            ps.setString(1, email);
            ps.setString(2, movie_id);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            return 3100;
        } catch(SQLIntegrityConstraintViolationException e) {
            ServiceLogger.LOGGER.warning("Duplicate entry for " + email + " and " + movie_id);
            e.printStackTrace();
            return 311;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            return 3150;
        }
    }

    public static Integer updateCart(String email, String movie_id, Integer quantity) {
        if (quantity <= 0) return 33;
        String query = "UPDATE cart\n" +
                "SET\n" +
                "    quantity = ?\n" +
                "WHERE email LIKE ? AND movie_id = ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1, quantity);
            ps.setString(2, email);
            ps.setString(3, movie_id);
            Integer numOfRowsAffected = ps.executeUpdate();
            if (numOfRowsAffected.equals(0)) return 312;
            else return 3110;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            return 3150;
        }
    }

    public static Integer deleteCart(String email, String movie_id) {
        String query = "DELETE FROM cart WHERE email LIKE ? AND movie_id LIKE ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, movie_id);
            Integer numOfRowsAffected = ps.executeUpdate();
            if (numOfRowsAffected.equals(0)) return 312;
            else return 3120;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            return 3150;
        }
    }

    public static retrieveModel retrieveCart(String email) {
        String query = "SELECT c.email, mp.unit_price, mp.discount, c.quantity, c.movie_id\n" +
                "FROM cart c LEFT JOIN movie_price mp on c.movie_id = mp.movie_id\n" +
                "WHERE c.email LIKE ?";
        retrieveModel responseModel = null;
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps1.setString(1, email);
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query: " + ps1.toString());
            Integer len;
            for (len=0; rs1.next(); ++len);
            if (len.equals(0)) {
                ServiceLogger.LOGGER.info("Shopping cart item does not exist.");
                responseModel = new retrieveModel(312, " Shopping cart item does not exist.", null);
                return responseModel;
            }
            retrieveModel.itemModel [] items = new  retrieveModel.itemModel[len];
            ResultSet rs = ps.executeQuery();
            for (int i=0; rs.next(); ++i) {
                String theEmail = rs.getString(1);
                Float unit_price = rs.getFloat(2);
                Float discount = rs.getFloat(3);
                Integer quantity = rs.getInt(4);
                String movie_id = rs.getString(5);
                String[] movie_ids = {movie_id};
                thumbnailResponseModel res = getMovies(movie_ids);
                if (res.getResultCode().equals(210)) {
                    String movie_title = res.getThumbnails()[0].getTitle();
                    String backdrop_path = res.getThumbnails()[0].getBackdrop_path();
                    String poster_path = res.getThumbnails()[0].getPoster_path();
                    items[i] = new retrieveModel.itemModel(theEmail, unit_price, discount, quantity, movie_id, movie_title, backdrop_path, poster_path);
                }
            }
            ServiceLogger.LOGGER.info("Shopping cart retrieved successfully.");
            responseModel = new retrieveModel(3130, "Shopping cart retrieved successfully.",  items);
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            responseModel = new retrieveModel(3150, "Shopping cart operation failed.", null);
        }
        return responseModel;
    }

    public static Integer clearCart(String email) {
        String query = "DELETE FROM cart WHERE email LIKE ?";
        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            Integer numOfRowsAffected = ps.executeUpdate();
            if (numOfRowsAffected.equals(0)) return 312;
            else return 3140;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Shopping cart operation failed.");
            e.printStackTrace();
            return 3150;
        }
    }
}
