package uk.co.deftelf.postbox;



import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class Postbox extends uk.co.deftelf.postbox.common.Postbox {

    /*private Postbox(String ref, String locationInfo1, String locationInfo2,
            double latitude, double longitude, Time lastWeekdayCollection,
            Time lastSaturdayCollection) {
        super(ref, locationInfo1, locationInfo2, latitude, longitude,
                lastWeekdayCollection, lastSaturdayCollection);
    }*/
    
    private boolean displayed = false;
    
    private Postbox() {
        
    }
    
    public static Postbox readFromJson(JSONObject source) throws JSONException {
        Postbox postbox = new Postbox();
        postbox.ref = source.getString("ref");
        postbox.locationInfo1 = (source.has("locationInfo1") ? source.getString("locationInfo1") : null);
        postbox.locationInfo2 = (source.has("locationInfo2") ? source.getString("locationInfo2") : null);
        postbox.latitude = source.getDouble("latitude");
        postbox.longitude = source.getDouble("longitude");
        postbox.lastSaturdayCollection = (source.has("lastSaturdayCollection") ? source.getInt("lastSaturdayCollection") : 0);
        postbox.lastWeekdayCollection = (source.has("lastWeekdayCollection") ? source.getInt("lastWeekdayCollection") : 0);
        return postbox;
    }
    
    public void setDisplayed() {
        displayed = true;
    }
    
    public boolean isDisplayed() {
        return displayed;
    }
    
    public String getLastWeekdayCollectionReadable() {
        return toReadableTime(getLastWeekdayCollection());
    }
    
    public String getLastSaturdayCollectionReadable() {
        return toReadableTime(getLastSaturdayCollection());   
    }
    
    static DecimalFormat timeFormatter = new DecimalFormat("00");
    static String toReadableTime(int minutesAfterMidnight) {
        return timeFormatter.format(minutesAfterMidnight / 60) + ":" + timeFormatter.format(minutesAfterMidnight % 60);
    }

}
