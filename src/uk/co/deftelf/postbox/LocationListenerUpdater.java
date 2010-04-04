package uk.co.deftelf.postbox;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationListenerUpdater implements LocationListener {
    
    LocationUpdater updater;
    boolean gps = false;
    
    public LocationListenerUpdater(LocationUpdater updater, boolean gps) {
        this.updater = updater;
        this.gps = gps;
    }

    
    public void onLocationChanged(Location location) {
        
        updater.locationFound(location, gps);

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
    
    

}
