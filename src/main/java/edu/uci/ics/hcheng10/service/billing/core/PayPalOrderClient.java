package edu.uci.ics.hcheng10.service.billing.core;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.braintreepayments.http.serializer.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import edu.uci.ics.hcheng10.service.billing.logger.ServiceLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayPalOrderClient {
    // Client id and secret retrieved from sandbox.
    private static final String clientId = "AdQwfVEVnCn-GeIyVVqFFzvkwLF-UrYpuYS2h9KuuHH1Xo5KFVUmz3AsyR3ALvTRPmdyBjdsRTvNxjBY";
    private static final String clientSecret = "EGT9Uc-YAVupw8Vk2VQ35ppXveixqPs3nC8Od4ZhoMc5j96FG_I7zsGLT3VE-I-ZaHal7bIVj85RmPeU";

    // Set up paypal environment
    public PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);

    //Create client for environment
    public PayPalHttpClient client = new PayPalHttpClient(environment);

    public Order createPayPalOrder(Float values) {
        Order order = null;

        //Construct a request object and set desired parameters
        //Here orderscreaterequest creates a post request to v2/checkout/orders

        OrderRequest orderRequest = new OrderRequest();

        //MUST use this method instead of intent to create capture.
        orderRequest.checkoutPaymentIntent("CAPTURE");

        //Create application context with return url upon payer completion.
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:12345/api/billing/order/complete")
                .cancelUrl("http://localhost:12345/api/billing/order/place");

        orderRequest.applicationContext(applicationContext);
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        String amount = String.format("%.2f", values);
        purchaseUnits.add(new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(amount)));
        orderRequest.purchaseUnits(purchaseUnits);
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            // Call API with your client and get a response for your call
            HttpResponse<Order> response = this.client.execute(request);

            // If call returns body in response, you can get the de-serialized version by
            // calling result() on the response
            order = response.result();
            System.out.println("Order ID: " + order.id());
            order.links().forEach(link -> ServiceLogger.LOGGER.info(link.rel() + " => " + link.method() + ":" + link.href()));
            return order;
        } catch (IOException ioe) {
            System.err.println("*******COULD NOT CREATE ORDER*******");
            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
                he.headers().forEach(x -> System.out.println(x + " :" + he.headers().header(x)));
            } else {


                // Something went wrong client-side
            }
            return null;
        }
    }

    public String captureOrder(String token) {
        String capture_id = null;
        Order order;
        OrdersCaptureRequest request = new OrdersCaptureRequest(token);

        // The orderId is the order_id/token generated from the orders/create request.

        try {
            // Call API with your client and get a response for your call
            HttpResponse<Order> response = this.client.execute(request);

            // If call returns body in response, you can get the de-serialized version
            // by calling result() on the response
            order = response.result();

            //Retrieve capture_id
            capture_id = order.purchaseUnits().get(0).payments().captures().get(0).id();
            ServiceLogger.LOGGER.info("Capture ID: " +
                    order.purchaseUnits().get(0).payments().captures().get(0).id());

            order.purchaseUnits().get(0).payments().captures().get(0).links()
                    .forEach(link -> ServiceLogger.LOGGER.info(link.rel() + " => " + link.method() + ":" + link.href()));
            return capture_id;
        } catch (IOException ioe) {

            if (ioe instanceof HttpException) {
                // Something went wrong server-side
                HttpException he = (HttpException) ioe;
                System.out.println(he.getMessage());
                he.headers().forEach(x -> ServiceLogger.LOGGER.info(x + " :" +
                        he.headers().header(x)));
            } else {
                // Something went wrong client-side
            }
        }
        return capture_id;
    }

    public JsonNode getOrder(String orderID) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(orderID);
        HttpResponse<Order> response = this.client.execute(request);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new Json().serialize(response.result()));
        System.out.println("Full response body:" + (new Json().serialize( response.result())));
//        for (JsonNode obJ: rootNode.get("purchase_units")) {
//            System.out.println(obJ.get("amount").get("value"));
//        }
        return rootNode;
        // System.out.println(new JSONObject(new Json().serialize(response.result())).toString(4));
    }

}

