package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensors {
    @JsonProperty("light_sensor_1")
    double lightSensor1;
    @JsonProperty("light_sensor_2")
    double lightSensor2;
    @JsonProperty("unix_timestamp_100us")
    String timestamp;

    public double getLightSensor1() {
        return lightSensor1;
    }

    public void setLightSensor1(double lightSensor1) {
        this.lightSensor1 = lightSensor1;
    }

    public double getLightSensor2() {
        return lightSensor2;
    }

    public void setLightSensor2(double lightSensor2) {
        this.lightSensor2 = lightSensor2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

//{"message": {"light_sensor_1": 242.0,"light_sensor_2": 128.0,"unix_timestamp_100us": 15886868150803}}