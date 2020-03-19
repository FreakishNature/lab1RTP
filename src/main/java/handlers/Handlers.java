package handlers;

import actors.Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.MessageSensor1;
import model.ResponseSensor1;
import model.ResponseSensor2;
import model.Sensor;

public class Handlers {
    static ObjectMapper mapper = new ObjectMapper();


    public static Handler dataReceiver = new Handler() {
        @Override
        public void receive(Object msg) throws Exception {
            if(msg == null) { return; }
            String data = msg.toString();
            system.sendMessage("processor",data);
        }
    };

    public static Handler processor = new Handler() {
        @Override
        public void receive(Object msg) throws Exception {
            if(msg == null) { return; }
            if(msg.toString().contains("panic")){
                throw new Exception("Panic exception");
            }
            try {
                system.sendMessage("forecaster_1",
                        mapper.readValue(msg.toString(), ResponseSensor1.class).getMessage()
                );

                system.sendMessage("forecaster_2",
                        mapper.readValue(msg.toString(), ResponseSensor2.class).getMessage()
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    };


    public static Handler forecaster = new Handler() {
        @Override
        public void receive(Object msg) throws Exception {
            if(msg == null) { return; }

            Sensor message = (Sensor)msg;

            String forecastSensors = msg instanceof MessageSensor1 ? "Forecast 1 : " : "Forecast 2 : ";
            String forecast = doForecast(message);

            system.sendMessage("printer",
                    forecastSensors + forecast +
                            "\n Temperature - " + message.getTemperatureSensor() +
                            "\n Humidity - " + message.getHumiditySensor() +
                            "\n Atmosphere pressure - " + message.getAtmoPressureSensor() +
                            "\n Light - " + message.getLightSensor() +
                            "\n Wind speed - " + message.getWindSpeedSensor()
            );
        }
    };

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

    public static Handler printer = new Handler() {
        @Override
        public void receive(Object msg) throws Exception {
            if ( msg == null) return;
//            system.getLoad();
            System.out.println("--------------------------");
            System.out.println(msg);
            System.out.println("--------------------------");
        }
    };


}
