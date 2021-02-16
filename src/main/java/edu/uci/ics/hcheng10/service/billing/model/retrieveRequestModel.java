package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class retrieveRequestModel {
    @JsonProperty(value="email",required = true)
    private String email;

    @JsonCreator
    public retrieveRequestModel(@JsonProperty(value="email",required = true) String email) {
        this.email = email;
    }

    @JsonProperty("email")
    public String getEmail() {return email;}
}
