package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class plevelRequestModel {
    @JsonProperty(value="email",required = true)
    private String email;

    @JsonProperty(value="plevel",required = true)
    private Integer plevel;


    @JsonCreator
    public plevelRequestModel(@JsonProperty(value="email", required = true) String email, @JsonProperty(value="plevel", required = true) Integer plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @JsonProperty("email")
    public String getEmail() {return email;}

    @JsonProperty("plevel")
    public Integer getPlevel() {return plevel;}

}
