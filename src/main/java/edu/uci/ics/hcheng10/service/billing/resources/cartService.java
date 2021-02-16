package edu.uci.ics.hcheng10.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.hcheng10.service.billing.core.cart;
import edu.uci.ics.hcheng10.service.billing.logger.ServiceLogger;
import edu.uci.ics.hcheng10.service.billing.model.*;
import org.checkerframework.checker.units.qual.C;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("cart")
public class cartService {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Inserting...");
        insertRequestModel requestModel;
        ResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestModel = mapper.readValue(jsonText, insertRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        String movie_id = requestModel.getMovie_id();
        Integer quantity = requestModel.getQuantity();
        Integer resultCode = cart.insertIntoCart(email, movie_id, quantity);
        if (cart.getPrivilege(email) == 14) {
            ServiceLogger.LOGGER.info("User not found");
            responseModel = new ResponseModel(14, " User not found.");
        } else if (resultCode == 33) {
            ServiceLogger.LOGGER.info("Quantity has invalid value.");
            responseModel = new ResponseModel(resultCode, "Quantity has invalid value.");
        } else if (resultCode == 311) {
            ServiceLogger.LOGGER.info("Duplicate insertion.");
            responseModel = new ResponseModel(resultCode, "Duplicate insertion.");
        } else if (resultCode == 3100) {
            ServiceLogger.LOGGER.info("Shopping cart item inserted successfully.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item inserted successfully.");
        } else if (resultCode == 3150) {
            ServiceLogger.LOGGER.info("Shopping cart operation failed.");
            responseModel = new ResponseModel(resultCode, "Shopping cart operation failed.");
        }
        return Response.status(Response.Status.OK).entity(responseModel).build();
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Updating...");
        insertRequestModel requestModel;
        ResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        try {
            requestModel = mapper.readValue(jsonText, insertRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        String movie_id = requestModel.getMovie_id();
        Integer quantity = requestModel.getQuantity();
        Integer resultCode = cart.updateCart(email, movie_id, quantity);
        if (resultCode == 33) {
            ServiceLogger.LOGGER.info("Quantity has invalid value.");
            responseModel = new ResponseModel(resultCode, "Quantity has invalid value.");
        } else if (resultCode == 312) {
            ServiceLogger.LOGGER.info("Shopping cart item does not exist.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item does not exist.");
        } else if (resultCode == 3110) {
            ServiceLogger.LOGGER.info("Shopping cart item updated successfully.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item updated successfully.");
        } else if (resultCode == 3150) {
            ServiceLogger.LOGGER.info("Shopping cart operation failed.");
            responseModel = new ResponseModel(resultCode, "Shopping cart operation failed.");
        }
        return Response.status(Response.Status.OK).entity(responseModel)
                .header("email", email2)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id).build();
    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Deleting...");
        deleteRequestModel requestModel;
        ResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        try {
            requestModel = mapper.readValue(jsonText, deleteRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        String movie_id = requestModel.getMovie_id();
        Integer resultCode = cart.deleteCart(email, movie_id);
        if (resultCode == 312) {
            ServiceLogger.LOGGER.info("Shopping cart item does not exist.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item does not exist.");
        } else if (resultCode == 3120) {
            ServiceLogger.LOGGER.info("Shopping cart item deleted successfully.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item deleted successfully.");
        } else if (resultCode == 3150) {
            ServiceLogger.LOGGER.info("Shopping cart operation failed.");
            responseModel = new ResponseModel(resultCode, "Shopping cart operation failed.");
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
        ServiceLogger.LOGGER.info("Retrieving...");
        retrieveRequestModel requestModel;
        retrieveModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        try {
            requestModel = mapper.readValue(jsonText, retrieveRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new retrieveModel(resultCode, "JSON Parse Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new retrieveModel(resultCode, "JSON Mapping Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new retrieveModel(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        responseModel = cart.retrieveCart(email);
        return Response.status(Response.Status.OK).entity(responseModel)
                .header("email", email2)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id).build();
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clear(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Clearing...");
        retrieveRequestModel requestModel;
        ResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        String email2 = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");
        try {
            requestModel = mapper.readValue(jsonText, retrieveRequestModel.class);
        } catch (IOException e) {
            int resultCode;
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        String email = requestModel.getEmail();
        Integer resultCode = cart.clearCart(email);
        if (resultCode == 312) {
            ServiceLogger.LOGGER.info("Shopping cart item does not exist.");
            responseModel = new ResponseModel(resultCode, "Shopping cart item does not exist.");
        } else if (resultCode == 3140) {
            ServiceLogger.LOGGER.info("Shopping cart cleared successfully.");
            responseModel = new ResponseModel(resultCode, "Shopping cart cleared successfully.");
        } else if (resultCode == 3150) {
            ServiceLogger.LOGGER.info("Shopping cart operation failed.");
            responseModel = new ResponseModel(resultCode, "Shopping cart operation failed.");
        }
        return Response.status(Response.Status.OK).entity(responseModel)
                .header("email", email2)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id).build();
    }
}
