package bots.skill.api.type.weather;

public class WeatherData {

    private final double latitude;
    private final double longitude;
    private long currentDate;

    public WeatherData(double lat, double lon, long date) {
        this.latitude = lat;
        this.longitude = lon;
        this.currentDate = date;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public long getCurrentDate() {
        return this.currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }
}
