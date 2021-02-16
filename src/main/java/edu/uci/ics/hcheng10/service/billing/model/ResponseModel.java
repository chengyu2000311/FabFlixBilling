package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel {
    @JsonProperty(value="resultCode", required = true)
    private Integer resultCode;

    @JsonProperty(value="message", required = true)
    private String message;

    @JsonCreator
    public ResponseModel(@JsonProperty(value="resultCode", required = true) Integer resultCode, @JsonProperty(value="message", required = true) String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty("resultCode")
    public Integer getResultCode() {return resultCode;}

    @JsonProperty("message")
    public String getMessage() {return message;}
}
