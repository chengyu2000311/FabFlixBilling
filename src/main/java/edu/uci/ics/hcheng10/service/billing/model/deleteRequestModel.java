package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class deleteRequestModel {
    @JsonProperty(value="email",required = true)
    private String email;

    @JsonProperty(value="movie_id",required = true)
    private String movie_id;


    @JsonCreator
    public deleteRequestModel(@JsonProperty(value="email", required = true) String email, @JsonProperty(value="movie_id", required = true) String movie_id) {
        this.email = email;
        this.movie_id = movie_id;
    }

    @JsonProperty("email")
    public String getEmail() {return email;}

    @JsonProperty("movie_id")
    public String getMovie_id() {return movie_id;}

}
