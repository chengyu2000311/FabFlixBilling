package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class placeResponseModel extends ResponseModel{
    @JsonProperty("approve_url")
    private String approve_url;
    @JsonProperty("token")
    private String token;

    @JsonCreator
    public placeResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode, @JsonProperty(value = "message", required = true) String message,
                              @JsonProperty("approve_url") String approve_url, @JsonProperty("token") String token) {
        super(resultCode, message);
        this.approve_url = approve_url;
        this.token = token;
    }

    @JsonProperty("approve_url")
    public String getApprove_url() {return approve_url;}

    @JsonProperty("token")
    public String getToken() {return token;}
}
