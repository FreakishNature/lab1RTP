package com.proj.UDP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.model.MessageSensor1;
import com.proj.model.Sensor;

import java.util.concurrent.CopyOnWriteArrayList;

public class ForecasterSubscriber extends Subscriber {

    static CopyOnWriteArrayList<Sensor> sensorDatas = new CopyOnWriteArrayList<>();
    static int AMOUNT_OF_FORECASTING_DATA = 5;
    static int FORECAST_INTERVAL = 5;
    static long lastTime = 0;
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {
        ForecasterSubscriber subscriber = new ForecasterSubscriber();

        subscriber.subscribe("JOINED_DATA", ForecasterSubscriber::doAverage);

        Thread.sleep(60_000);

        subscriber.unsubscribe("JOINED_DATA");
    }

    static void doAverage(String msg, String topic) throws JsonProcessingException {
        Sensor sensor = mapper.readValue(msg, MessageSensor1.class);

        if (sensorDatas.isEmpty()) {
            lastTime = System.currentTimeMillis();
        }

        sensorDatas.add((Sensor) sensor);
        if (System.currentTimeMillis() - lastTime < FORECAST_INTERVAL * 1000) {
            return;
        }

        double temperatureSensor = 0;
        double atmoPressureSensor = 0;
        double humiditySensor = 0;
        double lightSensor = 0;
        double windSpeedSensor = 0;

        for (Sensor entry : sensorDatas) {
            temperatureSensor += entry.getTemperatureSensor();
            atmoPressureSensor += entry.getAtmoPressureSensor();
            humiditySensor += entry.getHumiditySensor();
            lightSensor += entry.getLightSensor();
            windSpeedSensor += entry.getWindSpeedSensor();
        }

        temperatureSensor /= sensorDatas.size();
        atmoPressureSensor /= sensorDatas.size();
        humiditySensor /= sensorDatas.size();
        lightSensor /= sensorDatas.size();
        windSpeedSensor /= sensorDatas.size();

        Sensor message = new MessageSensor1(
                temperatureSensor,
                humiditySensor,
                windSpeedSensor,
                atmoPressureSensor,
                lightSensor
        );
        sensorDatas.clear();
        System.out.println("\n\nForecast - " + doForecast(message));
        CollectorSubscriber.prettyPrint(message);
    }

    private static String doForecast(Sensor message) {
        String forecast = "JUST_A_NORMAL_DAY";

        if (message.getTemperatureSensor() < -2 &&
                message.getLightSensor() < 128 &&
                message.getAtmoPressureSensor() < 720) {
            forecast = "SNOW";
        }

        if (message.getTemperatureSensor() < -2 &&
                message.getLightSensor() > 128 &&
                message.getAtmoPressureSensor() < 680) {
            forecast = "WET_SNOW";
        }

        if (message.getTemperatureSensor() < -8) {
            forecast = "SNOW";
        }

        if (message.getTemperatureSensor() < -15 &&
                message.getWindSpeedSensor() > 45) {
            forecast = "BLIZZARD";
        }

        if (message.getTemperatureSensor() > 0 &&
                message.getAtmoPressureSensor() < 710 &&
                message.getHumiditySensor() > 70 &&
                message.getWindSpeedSensor() < 20) {
            forecast = "SLIGHT_RAIN";
        }

        if (message.getTemperatureSensor() > 0 &&
                message.getAtmoPressureSensor() < 690 &&
                message.getHumiditySensor() > 70 &&
                message.getWindSpeedSensor() > 20) {
            forecast = "HEAVY_RAIN";
        }

        if (message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 770 &&
                message.getHumiditySensor() > 80 &&
                message.getLightSensor() > 192) {
            forecast = "HOT";
        }


        if (message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 770 &&
                message.getHumiditySensor() > 50 &&
                message.getLightSensor() > 192 &&
                message.getWindSpeedSensor() > 35) {
            forecast = "CONVECTION_OVEN";
        }

        if (message.getTemperatureSensor() > 25 &&
                message.getAtmoPressureSensor() < 750 &&
                message.getHumiditySensor() > 70 &&
                message.getLightSensor() < 192 &&
                message.getWindSpeedSensor() < 10) {
            forecast = "CONVECTION_OVEN";
        }

        if (message.getTemperatureSensor() > 25 &&
                message.getAtmoPressureSensor() < 750 &&
                message.getHumiditySensor() > 70 &&
                message.getLightSensor() < 192 &&
                message.getWindSpeedSensor() > 10
        ) {
            forecast = "SLIGHT_BREEZE";
        }
        if (message.getLightSensor() < 128) {
            forecast = "CLOUDY";
        }

        if (message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 660 &&
                message.getHumiditySensor() > 85 &&
                message.getWindSpeedSensor() > 45) {
            forecast = "MONSOON";
        }

        return forecast;
    }
}