package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class retrieveModel extends ResponseModel {
    @JsonProperty("items")
    private itemModel [] items;

    @JsonCreator
    public retrieveModel(@JsonProperty("resultCode") Integer resultCode, @JsonProperty("message") String message, @JsonProperty("items") itemModel[] items) {
        super(resultCode, message);
        this.items = items;
    }

    @JsonProperty("items")
    public itemModel[] getItems() {return items;}

    public static class itemModel {
        @JsonProperty(value = "email", required = true)
        private String email;
        @JsonProperty(value = "unit_price", required = true)
        private Float unit_price;
        @JsonProperty(value = "discount", required = true)
        private Float discount;
        @JsonProperty(value = "quantity", required = true)
        private Integer quantity;
        @JsonProperty(value = "movie_id", required = true)
        private String movie_id;
        @JsonProperty(value = "movie_title", required = true)
        private String movie_title;
        @JsonProperty("backdrop_path")
        private String backdrop_path;
        @JsonProperty("poster_path")
        private String poster_path;

        @JsonCreator
        public itemModel(@JsonProperty(value = "email", required = true) String email, @JsonProperty(value = "unit_price", required = true) Float unit_price,
                         @JsonProperty(value = "discount", required = true) Float discount, @JsonProperty(value = "quantity", required = true) Integer quantity,
                         @JsonProperty(value = "movie_id", required = true) String movie_id, @JsonProperty(value = "movie_title", required = true) String movie_title,
                         @JsonProperty("backdrop_path") String backdrop_path, @JsonProperty("poster_path") String poster_path) {
            this.email = email;
            this.unit_price = unit_price;
            this.discount = discount;
            this.quantity = quantity;
            this.movie_id = movie_id;
            this.movie_title = movie_title;
            this.backdrop_path = backdrop_path;
            this.poster_path = poster_path;
        }

        @JsonProperty(value = "email", required = true)
        public String getEmail() {return email;}
        @JsonProperty(value = "unit_price", required = true)
        public Float getUnit_price() {return unit_price;}
        @JsonProperty(value = "discount", required = true)
        public Float getDiscount() {return discount;}
        @JsonProperty(value = "quantity", required = true)
        public Integer getQuantity() {return quantity;}
        @JsonProperty(value = "movie_id", required = true)
        public String getMovie_id() {return movie_id;}
        @JsonProperty(value = "movie_title", required = true)
        public String getMovie_title() {return movie_title;}
        @JsonProperty("backdrop_path")
        public  String getBackdrop_path() {return backdrop_path;}
        @JsonProperty("poster_path")
        public  String getPoster_path() {return poster_path;}
    }
}
