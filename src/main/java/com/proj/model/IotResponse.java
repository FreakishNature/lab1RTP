package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Iot {
    @JsonProperty("wind_speed_sensor_1")
    double windSpeedSensor1;
    @JsonProperty("wind_speed_sensor_2")
    double windSpeedSensor2;
    @JsonProperty("atmo_pressure_sensor_1")
    double atmoPressureSensor1;
    @JsonProperty("atmo_pressure_sensor_2")
    double atmoPressureSensor2;
    @JsonProperty("unix_timestamp_100us")
    String timestamp;

    public double getWindSpeedSensor1() {
        return windSpeedSensor1;
    }

    public void setWindSpeedSensor1(double windSpeedSensor1) {
        this.windSpeedSensor1 = windSpeedSensor1;
    }

    public double getWindSpeedSensor2() {
        return windSpeedSensor2;
    }

    public void setWindSpeedSensor2(double windSpeedSensor2) {
        this.windSpeedSensor2 = windSpeedSensor2;
    }

    public double getAtmoPressureSensor1() {
        return atmoPressureSensor1;
    }

    public void setAtmoPressureSensor1(double atmoPressureSensor1) {
        this.atmoPressureSensor1 = atmoPressureSensor1;
    }

    public double getAtmoPressureSensor2() {
        return atmoPressureSensor2;
    }

    public void setAtmoPressureSensor2(double atmoPressureSensor2) {
        this.atmoPressureSensor2 = atmoPressureSensor2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

//{"message": {"wind_speed_sensor_1": 34.8570392150276,"wind_speed_sensor_2": 7.990571003157799,"atmo_pressure_sensor_1": 711.651161675058,"atmo_pressure_sensor_2": 642.9946096470643,"unix_timestamp_100us": 15886868230972}}
