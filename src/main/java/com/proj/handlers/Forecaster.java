package com.proj.handlers;

import com.proj.actors.Handler;
import com.proj.model.MessageSensor1;
import com.proj.model.Sensor;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Forecaster extends Handler {
    public Forecaster(int interval){
        FORECAST_INTERVAL = interval;
    }
    CopyOnWriteArrayList<Sensor> sensorDatas = new CopyOnWriteArrayList<>();
    int AMOUNT_OF_FORECASTING_DATA = 5;
    int FORECAST_INTERVAL = 5;
    long lastTime = 0;
    @Override
    public void receive(Object msg) throws IOException {
        if(msg == null) { return; }

//        try{
            if(sensorDatas.isEmpty()){
                lastTime = System.currentTimeMillis();
            }

            sensorDatas.add((Sensor)msg);
            if(System.currentTimeMillis() - lastTime < FORECAST_INTERVAL * 1000){
                return;
            }

            String forecastSensors = msg instanceof MessageSensor1 ? "Forecast 1 : " : "Forecast 2 : ";


            double temperatureSensor = 0;
            double atmoPressureSensor = 0;
            double humiditySensor = 0;
            double lightSensor = 0;
            double windSpeedSensor = 0;

            for (Sensor sensor : sensorDatas) {
                temperatureSensor += sensor.getTemperatureSensor();
                atmoPressureSensor += sensor.getAtmoPressureSensor();
                humiditySensor += sensor.getHumiditySensor();
                lightSensor += sensor.getLightSensor();
                windSpeedSensor += sensor.getWindSpeedSensor();
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

            String forecast = doForecast(message);
            sensorDatas.clear();
            system.sendMessage("printer",
                    "\n" + forecastSensors + forecast +
                            "\n Temperature - " + message.getTemperatureSensor() +
                            "\n Humidity - " + message.getHumiditySensor() +
                            "\n Atmosphere pressure - " + message.getAtmoPressureSensor() +
                            "\n Light - " + message.getLightSensor() +
                            "\n Wind speed - " + message.getWindSpeedSensor()
            );
    }

    private static String doForecast(Sensor message) {
        String forecast = "JUST_A_NORMAL_DAY";

        if( message.getTemperatureSensor() < -2 &&
                message.getLightSensor() < 128 &&
                message.getAtmoPressureSensor() < 720){
            forecast = "SNOW";
        }

        if( message.getTemperatureSensor() < -2 &&
                message.getLightSensor() > 128 &&
                message.getAtmoPressureSensor() < 680){
            forecast = "WET_SNOW";
        }

        if( message.getTemperatureSensor() < -8){
            forecast = "SNOW";
        }

        if( message.getTemperatureSensor() < -15 &&
                message.getWindSpeedSensor() > 45){
            forecast = "BLIZZARD";
        }

        if( message.getTemperatureSensor() > 0 &&
                message.getAtmoPressureSensor() < 710 &&
                message.getHumiditySensor() > 70 &&
                message.getWindSpeedSensor() < 20){
            forecast = "SLIGHT_RAIN";
        }

        if( message.getTemperatureSensor() > 0 &&
                message.getAtmoPressureSensor() < 690 &&
                message.getHumiditySensor() > 70 &&
                message.getWindSpeedSensor() > 20){
            forecast = "HEAVY_RAIN";
        }

        if( message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 770 &&
                message.getHumiditySensor() > 80 &&
                message.getLightSensor() > 192){
            forecast = "HOT";
        }


        if( message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 770 &&
                message.getHumiditySensor() > 50 &&
                message.getLightSensor() > 192 &&
                message.getWindSpeedSensor() > 35){
            forecast = "CONVECTION_OVEN";
        }

        if( message.getTemperatureSensor() > 25 &&
                message.getAtmoPressureSensor() < 750 &&
                message.getHumiditySensor() > 70 &&
                message.getLightSensor() < 192 &&
                message.getWindSpeedSensor() < 10){
            forecast = "CONVECTION_OVEN";
        }

        if(message.getTemperatureSensor() > 25 &&
                message.getAtmoPressureSensor() < 750 &&
                message.getHumiditySensor() > 70 &&
                message.getLightSensor() < 192 &&
                message.getWindSpeedSensor() > 10
        ) {
            forecast = "SLIGHT_BREEZE";
        }
        if(message.getLightSensor() < 128){
            forecast = "CLOUDY";
        }

        if(message.getTemperatureSensor() > 30 &&
                message.getAtmoPressureSensor() < 660 &&
                message.getHumiditySensor() > 85 &&
                message.getWindSpeedSensor() > 45){
            forecast = "MONSOON";
        }

        return forecast;
    }

}
