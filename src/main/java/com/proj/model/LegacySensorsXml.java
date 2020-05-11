package com.proj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Arrays;
import java.util.List;

public class LegacySensorsXml {
    @JsonProperty("unix_timestamp_100us")
    String unix_timestamp_100us;
    @JsonProperty("humidity_percent")
    HumidityPercent humidity_percent;
    @JsonProperty("temperature_celsius")
    TemperatureCelsius temperature_celsius;
    public static class HumidityPercent{
        @JsonProperty("value")
        @JacksonXmlElementWrapper(useWrapping=false)
        List<Double> value;

        public List<Double> getValue() {
            return value;
        }
    }
    public static class TemperatureCelsius{
        @JsonProperty("value")
        @JacksonXmlElementWrapper(useWrapping=false)
        List<Double> value;

        public List<Double> getValue() {
            return value;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String str = "{\"message\": \"<SensorReadings unix_timestamp_100us='15886868197961'>    <humidity_percent>        <value>81.8625687837649</value>        <value>64.17303405893723</value>    </humidity_percent>    <temperature_celsius>        <value>4.812248650292794</value>        <value>7.600272223055896</value>    </temperature_celsius></SensorReadings>\"}";
        LegacySensors sensors = mapper.readValue(str,LegacySensors.class);

        XmlMapper mapper1 = new XmlMapper();
        System.out.println(mapper.writeValueAsString(mapper1.readValue(sensors.message, LegacySensorsXml.class)));
    }

    @Override
    public String toString() {
        return "LegacySensorsXml{" +
                "unix_timestamp_100us='" + unix_timestamp_100us + '\'' +
                ", humidity_percent=" + humidity_percent +
                ", temperature_celsius=" + temperature_celsius +
                '}';
    }

    public String getUnix_timestamp_100us() {
        return unix_timestamp_100us;
    }

    public void setUnix_timestamp_100us(String unix_timestamp_100us) {
        this.unix_timestamp_100us = unix_timestamp_100us;
    }

    public HumidityPercent getHumidity_percent() {
        return humidity_percent;
    }

    public void setHumidity_percent(HumidityPercent humidity_percent) {
        this.humidity_percent = humidity_percent;
    }

    public TemperatureCelsius getTemperature_celsius() {
        return temperature_celsius;
    }

    public void setTemperature_celsius(TemperatureCelsius temperature_celsius) {
        this.temperature_celsius = temperature_celsius;
    }
}

