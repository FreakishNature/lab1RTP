package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IotResponse {
    @JsonProperty("message")
    Iot message;

    public Iot getMessage() {
        return message;
    }

    public void setMessage(Iot message) {
        this.message = message;
    }
}

