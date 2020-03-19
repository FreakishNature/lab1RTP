package model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseSensor1 {
    MessageSensor1 message;

    public MessageSensor1 getMessage() {
        return message;
    }
}
