package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorsResponse {
    @JsonProperty("message")
    Sensors message;

    public Sensors getMessage() {
        return message;
    }

    public void setMessage(Sensors message) {
        this.message = message;
    }
}

