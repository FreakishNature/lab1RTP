package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseSensor2 {
    MessageSensor2 message;

    public MessageSensor2 getMessage() {
        return message;
    }
}
