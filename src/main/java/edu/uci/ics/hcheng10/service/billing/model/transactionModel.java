package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class transactionModel extends ResponseModel{
    @JsonProperty("transactions")
    private transaction [] transactions;

    public transactionModel(@JsonProperty(value = "resultCode",required = true) Integer resultCode, @JsonProperty(value = "message", required = true) String message,
                            @JsonProperty("transactions") transaction[] transactions) {
        super(resultCode, message);
        this.transactions = transactions;
    }

    @JsonProperty("transactions")
    public transaction [] getTransactions() {return transactions;}

    public static class transaction {
        @JsonProperty(value = "capture_id", required = true)
        private String capture_id;

        @JsonProperty(value = "state", required = true)
        private String state;

        @JsonProperty("amount")
        private amount amount;

        @JsonProperty("transaction_fee")
        private transaction_fee transaction_fee;

        @JsonProperty(value = "create_time", required = true)
        private String create_time;

        @JsonProperty(value = "update_time", required = true)
        private String update_time;

        @JsonProperty("items")
        private item[] items;

        public transaction(@JsonProperty(value = "capture_id", required = true) String capture_id, @JsonProperty(value = "state", required = true) String state,
                           @JsonProperty("amount") amount amount, @JsonProperty("transaction_fee") transaction_fee transaction_fee,
                           @JsonProperty(value = "create_time", required = true) String create_time, @JsonProperty(value = "update_time", required = true) String update_time,
                           @JsonProperty("items") item[] items) {
            this.capture_id = capture_id;
            this.state = state;
            this.amount = amount;
            this.transaction_fee = transaction_fee;
            this.create_time = create_time;
            this.update_time = update_time;
            this.items = items;
        }

        @JsonProperty("capture_id")
        public String getCapture_id() {return capture_id;};

        @JsonProperty("state")
        public String getState() {return state;};

        @JsonProperty("amount")
        public amount getAmount() {return amount;};

        @JsonProperty("transaction_fee")
        public transaction_fee getTransaction_fee() {return transaction_fee;}

        @JsonProperty("create_time")
        public String getCreate_time() {return create_time;}

        @JsonProperty("update_time")
        public String getUpdate_time() {return update_time;}

        @JsonProperty("items")
        public item[] getItems() {return items;}

        public static class transaction_fee {
            @JsonProperty(value = "value", required = true)
            private String value;
            @JsonProperty(value = "currency", required = true)
            private String currency;

            public transaction_fee(@JsonProperty(value = "value", required = true) String value, @JsonProperty(value = "currency", required = true) String currency) {
                this.value = value;
                this.currency = currency;
            }

            @JsonProperty("value")
            public String getValue() {return value;}
            @JsonProperty("currency")
            public String getCurrency() {return currency;}
        }

        public static class amount {
            @JsonProperty(value = "total", required = true)
            private String total;
            @JsonProperty(value = "currency", required = true)
            private String currency;

            public amount(@JsonProperty(value = "total", required = true) String total, @JsonProperty(value = "currency", required = true) String currency) {
                this.total = total;
                this.currency = currency;
            }

            @JsonProperty("total")
            public String getTotal() {return total;}
            @JsonProperty("currency")
            public String getCurrency() {return currency;}
        }

        public static class item {
            @JsonProperty(value = "email", required = true)
            private String email;
            @JsonProperty(value = "movie_id", required = true)
            private String movie_id;
            @JsonProperty(value = "quantity", required = true)
            private Integer quantity;
            @JsonProperty(value = "unit_price", required = true)
            private Float unit_price;
            @JsonProperty(value = "discount", required = true)
            private Float discount;
            @JsonProperty(value = "sale_date", required = true)
            private String sale_date;

            public item(@JsonProperty(value = "email", required = true) String email, @JsonProperty(value = "movie_id", required = true) String movie_id,
                        @JsonProperty(value = "quantity", required = true) Integer quantity, @JsonProperty(value = "unit_price", required = true) Float unit_price,
                        @JsonProperty(value = "discount", required = true) Float discount, @JsonProperty(value = "sale_date", required = true) String sale_date) {
                this.email = email;
                this.movie_id = movie_id;
                this.quantity = quantity;
                this.unit_price = unit_price;
                this.discount = discount;
                this.sale_date = sale_date;
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
            @JsonProperty(value = "sale_date", required = true)
            public String getSale_date() {return sale_date;}

        }
    }
}
