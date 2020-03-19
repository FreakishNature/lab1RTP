package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageSensor2 implements Sensor{
    @JsonProperty("temperature_sensor_2")
    double temperatureSensor;
    @JsonProperty("humidity_sensor_2")
    double humiditySensor;
    @JsonProperty("wind_speed_sensor_2")
    double windSpeedSensor;
    @JsonProperty("atmo_pressure_sensor_2")
    double atmoPressureSensor;
    @JsonProperty("light_sensor_2")
    double lightSensor;

    public double getTemperatureSensor() {
        return temperatureSensor;
    }

    public double getHumiditySensor() {
        return humiditySensor;
    }

    public double getWindSpeedSensor() {
        return windSpeedSensor;
    }

    public double getAtmoPressureSensor() {
        return atmoPressureSensor;
    }

    public double getLightSensor() {
        return lightSensor;
    }
}
