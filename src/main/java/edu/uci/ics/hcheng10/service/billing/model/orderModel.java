package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class orderModel {
    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonProperty(value = "intent", required = true)
    private String intent;

    @JsonProperty(value = "status", required = true)
    private String status;

    @JsonProperty(value = "purchase_unit", required = true)
    private purchase_unit[] purchase_units;

    @JsonProperty(value = "payer")
    private payer payer;

    @JsonProperty(value = "create_time",required = true)
    private String create_time;

    @JsonProperty(value = "update_time",required = true)
    private String update_time;

    @JsonProperty(value = "link", required = true)
    private link[] links;

    public class purchase_unit {
        @JsonProperty("reference_id")
        private String reference_id;

        @JsonProperty("amount")
        private amount amount;

        @JsonProperty("payee")
        private payee payee;

        public class amount {
            @JsonProperty("currency_code")
            private String currency_code;

            @JsonProperty("value")
            private String value;
        }
        public class payee {
            @JsonProperty("email_address")
            private String email_address;

            @JsonProperty("merchant_id")
            private String merchant_id;
        }

    }

    public class payer {

    }

    public class link {

    }
}
