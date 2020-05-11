package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LegacySensors {
    @JsonProperty("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

