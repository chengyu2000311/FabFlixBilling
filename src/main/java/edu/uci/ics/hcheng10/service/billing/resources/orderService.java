package edu.uci.ics.hcheng10.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import edu.uci.ics.hcheng10.service.billing.core.PayPalOrderClient;
import edu.uci.ics.hcheng10.service.billing.core.order;
import edu.uci.ics.hcheng10.service.billing.logger.ServiceLogger;
import edu.uci.ics.hcheng10.service.billing.model.ResponseModel;
import edu.uci.ics.hcheng10.service.billing.model.placeResponseModel;
import edu.uci.ics.hcheng10.service.billing.model.retrieveRequestModel;
import edu.uci.ics.hcheng10.service.billing.model.transactionModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("order")
public class orderService {
    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response place(@Context HttpHeaders headers, String jsonText) {
        retrieveRequestModel requestModel;
        placeResponseModel responseModel = null;
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, retrieveRequestModel.class);
            String email = requestModel.getEmail();
            Float amount = order.getAmount(email);
            PayPalOrderClient client = new PayPalOrderClient();
            Order theOrder = client.createPayPalOrder(amount);
            String token = theOrder.id();
            order.placeOrder(email, token);
            String approve_url = null;
            for (LinkDescription link: theOrder.links()) {
                if (link.rel().equals("approve")) approve_url = link.href();
            }
            responseModel = new placeResponseModel(3400, "Order placed successfully.", approve_url, token);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new placeResponseModel(resultCode, "JSON Parse Exception", null, null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new placeResponseModel(resultCode, "JSON Mapping Exception", null, null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new placeResponseModel(resultCode, "Internal Server Error", null, null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        return Response.status(Response.Status.OK).entity(responseModel)
                .header("email", email2)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id).build();

    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(@Context HttpHeaders headers, String jsonText) {
        retrieveRequestModel requestModel;
        transactionModel responseModel = null;
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, retrieveRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new transactionModel(resultCode, "JSON Parse Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new transactionModel(resultCode, "JSON Mapping Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new transactionModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        PayPalOrderClient client = new PayPalOrderClient();
        try{
            //client.getOrder("95W40661V7824991F");
            responseModel = order.retrieveOrder(email, client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).entity(responseModel)
                .header("email", email2)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id).build();
    }

    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response complete(@QueryParam("token") String token, @QueryParam("payer_id") String payer_id) {
        ResponseModel responseModel;
        try {
            PayPalOrderClient client = new PayPalOrderClient();
            String capture_id =client.captureOrder(token);
            Integer resultCode = order.completeOrder(token, capture_id);
            if (resultCode.equals(3420)) responseModel = new ResponseModel(resultCode, "Order is completed successfully.");
            else if (resultCode.equals(3421)) responseModel = new ResponseModel(resultCode, "Token not found.");
            else responseModel = new ResponseModel(resultCode, "Order can not be completed.");
        } catch(Exception e) {
            responseModel = new ResponseModel(-1, "Internal Server Error");
            ServiceLogger.LOGGER.severe("Internal Server Error");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
