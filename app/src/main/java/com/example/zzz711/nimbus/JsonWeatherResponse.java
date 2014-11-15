package com.example.zzz711.nimbus;

/**
 * @author Margo
 * @version 11/15/2014.
 *
 * REST fills in the variables based on the JSON data
 *      nested classes are a necessity based on the layout of JSON data
 */

public class JsonWeatherResponse {

    class current_condition{ //Information about the current weather.
        public int weatherCode; //int that provides verbose weather information
    }

    class weather{ //Information about the weather forecast.
        public int mintempF; //Minimum temperature of the day in degree Fahrenheit
        public int precipMM; //Precipitation of the day in millimetres
    }

    class hourly{ //Information about hourly weather conditions.
        public int windspeedMiles; //Wind speed in miles per hour
        public int cloudcover; //Cloud cover amount in integer percentage
        public int chanceofrain; //Chance of rain (precipitation) in percentage
        public int chanceofsunny; //Chance of being sunny in percentage
        public int chanceofsnow; //Chance of snow in percentage
    }
}
