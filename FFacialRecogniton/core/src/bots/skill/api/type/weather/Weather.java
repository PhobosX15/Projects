package bots.skill.api.type.weather;

import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.slot.slotType.SlotTypeName;
import bots.skill.api.type.APIType;
import bots.skill.api.type.APITypeName;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Weather extends APIType {

    private static final String DEFAULT_CITY = "Maastricht";
    private static final String BASE_URl = "http://api.openweathermap.org/data/2.5/";

    public Weather(String key) {
        super(APITypeName.WEATHER, key);
        this.key = "1cd388ae337c452317ef14692b816091";
    }

    @Override
    public String getInfo(Map<SlotType, String> typesWithInfo) {
        String namePlace = null;
        String[] date = null;
        for (SlotType type : typesWithInfo.keySet()) {
            if (type.getName() == SlotTypeName.PLACE) {
                namePlace = typesWithInfo.get(type);
            } else if (type.getName() == SlotTypeName.DATE) {
                String dateString = typesWithInfo.get(type);
                date = type.checkFormat(dateString);
            }
        }

        WeatherDataType dataType = WeatherDataType.current;
        if (namePlace == null) {
            namePlace = DEFAULT_CITY;
        }

        // Gets the coordinates and the current date
        WeatherData coords = getCurrentData(namePlace);
        boolean forecast = true;

        if (date != null && date.length == 3) {
            dataType = WeatherDataType.daily;

            GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(date[2]),
                    Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
            long unixTimeStamp = cal.getTime().getTime()/1000;
            long currentUnix = coords.getCurrentDate();
            forecast = unixTimeStamp >= currentUnix;

            if (!dateInRange(currentUnix, unixTimeStamp)) {
                dataType = WeatherDataType.current;
                forecast = true;
            } else if (!forecast) {
                dataType = WeatherDataType.current;
                coords.setCurrentDate(unixTimeStamp);
            } else {
                coords.setCurrentDate(unixTimeStamp);
            }
        }

        String json = weatherDataAsJSONString(getOneCallURL(coords, dataType, getKey(), forecast));
        assert json != null;

        JSONObject object = new JSONObject(json);
        Map<String, String> weatherData;
        if (dataType == WeatherDataType.current) {
            JSONObject weatherDataJson = object.getJSONObject(dataType.name());
            weatherData = getWeatherData(weatherDataJson);
        } else {
            // Then type is daily
            weatherData = getFutureWeatherData(object.getJSONArray(dataType.name()), coords.getCurrentDate());
        }

        return "The requested weather is: " + weatherData.get("weather_description") +
                ". The temperature is " + weatherData.get("temp") + " Celsius. " +
                "The wind speed is " + weatherData.get("wind_speed") + " meters per second.";
    }

    private boolean dateInRange(long current, long date) {
        return Math.abs(current - date) < (86400 * 4);
    }

    private Map<String, String> getWeatherData(JSONObject weatherDataJson) {
        Map<String, String> weatherData = new HashMap<>();

        weatherData.put("temp", String.valueOf(weatherDataJson.getDouble("temp")));
        weatherData.put("wind_speed", String.valueOf(weatherDataJson.getDouble("wind_speed")));

        JSONObject weatherDescription = weatherDataJson.getJSONArray("weather").getJSONObject(0);
        weatherData.put("weather_description", weatherDescription.getString("description"));

        return weatherData;
    }

    private Map<String, String> getFutureWeatherData(JSONArray weatherDataJson, long day) {
        Map<String, String> weatherData = new HashMap<>();

        JSONObject correctObject = null;

        for (Object object : weatherDataJson) {
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                if (correctDay(jsonObject.getInt("dt"), day)) {
                    correctObject = jsonObject;
                }
            }
        }

        assert correctObject != null;
        JSONObject temperatureObject = correctObject.getJSONObject("temp");
        weatherData.put("temp", String.valueOf(temperatureObject.getDouble("day")));

        weatherData.put("wind_speed", String.valueOf(correctObject.getDouble("wind_speed")));

        JSONObject weatherDescription = correctObject.getJSONArray("weather").getJSONObject(0);
        weatherData.put("weather_description", weatherDescription.getString("description"));

        return weatherData;
    }

    /**
     * Checks if one day is close enough (max difference of 43200 (half a day))
     */
    private boolean correctDay(long day1, long day2) {
        return (Math.abs(day1 - day2) < 43200);
    }

    // TODO 3: Add exception for no internet
    private String weatherDataAsJSONString(String urlString) {
        // Creates the xml in string format.
        StringBuilder json = new StringBuilder();
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json.toString();
    }

    private String getOneCallURL(WeatherData weatherData, WeatherDataType type, String key, boolean forecast) {
        String units = "&units=metric";
        String address = "onecall";
        String time = "";
        if (!forecast) {
            address += "/timemachine";
            time = "&dt=" + weatherData.getCurrentDate();
        }
        StringBuilder exclude = new StringBuilder("&exclude=alerts,minutely");
        for (WeatherDataType dataType : WeatherDataType.values()) {
            if (dataType != type) {
                exclude.append(",").append(dataType.name());
            }
        }
        return BASE_URl + address + "?lat=" + weatherData.getLatitude() + "&lon=" + weatherData.getLongitude() + time +
                exclude.toString() + units + "&appid=" + key;
    }

    /**
     * Gets the coordinates of the given city plus the current time in Unix.
     * If the city is not found, then the default city will be chosen.
     * @param cityName The name of the city, the coords should be found for.
     * @return A double array of coordinates. Where the first element is the longitude
     * and the second element is the latitude. This will always have 2 elements.
     */
    private WeatherData getCurrentData(String cityName) {
        double latitude;
        double longitude;
        int unixTime;

        String weatherDataString = weatherDataAsJSONString(getWeatherURL("json", cityName, getKey()));

        JSONObject json;
        try {
            assert weatherDataString != null;
            json = new JSONObject(weatherDataString);
        } catch (JSONException e) {
            return getCurrentData(DEFAULT_CITY);
        }
        unixTime = json.getInt("dt");
        JSONObject coords = json.getJSONObject("coord");
        latitude = coords.getDouble("lat");
        longitude = coords.getDouble("lon");

        return new WeatherData(latitude, longitude, unixTime);
    }

    /**
     * Gets the URL for getting the current weather information
     * @param mode The way the information is given. This can be HTML, XML, or JSON. To get the data in XML format,
     *             this parameter needs to be named "xml". To get the data in HTML format,
     *             this parameter needs to be named "html". JSON format is the default format and will be used
     *             if anything other than "xml" or "html" is given for this parameter.
     * @param key The api key
     * @return The URL, properly formatted.
     */
    private String getWeatherURL(String mode, String cityName, String key) {
        String modeText = "";
        cityName = cityName.replaceAll(" ", "%20");
        if (mode.equals("xml") || mode.equals("html")) {
            modeText = "&mode=" + mode;
        }
        return BASE_URl + "weather" + "?q=" + cityName + modeText + "&appid=" + key;
    }
}
