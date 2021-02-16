package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class insertRequestModel extends deleteRequestModel {

    @JsonProperty(value="quantity",required = true)
    private Integer quantity;

    @JsonCreator
    public insertRequestModel(@JsonProperty(value="email", required = true) String email, @JsonProperty(value="movie_id", required = true) String movie_id,
            @JsonProperty(value="quantity", required = true) Integer quantity) {
        super(email, movie_id);
        this.quantity = quantity;
    }

    @JsonProperty("quantity")
    public Integer getQuantity() {return quantity;}
}
