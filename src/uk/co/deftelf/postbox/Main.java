package uk.co.deftelf.postbox;

import java.io.*;
import java.net.Socket;

import org.json.*;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class Main extends Activity implements LocationUpdater {
    
    Location lastLocation = null;
    TextView statusText;
    Button mapButton;
    Activity mainActivity;
    LocationManager locManager;
    LocationListenerUpdater locationListenerBackup;
    LocationListenerUpdater locationListener;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.startMethodTracing("Postbox");

        setContentView(R.layout.main);
        mainActivity = this;
        statusText = (TextView) this.findViewById(R.id.statusText);
        mapButton = (Button) this.findViewById(R.id.showMapButton);
        mapButton.setEnabled(false);
        mapButton.setText("Fixing your location...");
        
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        
        Criteria gpsCriteria = new Criteria();
        gpsCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String backupGpsName = locManager.getBestProvider(gpsCriteria, true);
        locationListenerBackup = new LocationListenerUpdater(this, false);
        locManager.requestLocationUpdates(backupGpsName, 1000, 2, locationListenerBackup);
        
        gpsCriteria = new Criteria();
        gpsCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestGpsName = locManager.getBestProvider(gpsCriteria, true);
        locationListener = new LocationListenerUpdater(this, true);
        locManager.requestLocationUpdates(bestGpsName, 1000, 2, locationListener);

        mapButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                if (locationListener != null)
                    locManager.removeUpdates(locationListener);
                if (locationListenerBackup != null)
                    locManager.removeUpdates(locationListenerBackup);
                try {
                    
                    Intent intnt = new Intent(mainActivity, Map.class);
                    intnt.putExtra("lat", lastLocation.getLatitude());
                    intnt.putExtra("lon", lastLocation.getLongitude());
                    startActivity(intnt);
                    
                    
                } catch (Exception ex) {
                    Toast.makeText(mainActivity, "Sorry, unable to retrieve postbox locations at the current time.", Toast.LENGTH_LONG).show();
                    Log.e("uk.co.deftelf.postbox.Main", "An error occured when retrieving the postbox data.", ex);
                }
               
                
               
            }
        });

    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        
        if (hasFocus ) {
            
        } else {
            if (locationListener != null)
                locManager.removeUpdates(locationListener);
            if (locationListenerBackup != null)
                locManager.removeUpdates(locationListenerBackup);
        }
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Debug.stopMethodTracing();
    }

    public void locationFound(Location newLocation, boolean gps) {
        
        if (lastLocation == null) {
            mapButton.setText("Find local postboxes");
            mapButton.setEnabled(true);
        }
        
        lastLocation = newLocation;
        statusText.setText("Location fix accurate to " + (int)newLocation.getAccuracy() + " metres.");
        if (gps) { // we have a GPS link so stop getting network updates
            if (locationListenerBackup != null)
                locManager.removeUpdates(locationListenerBackup);
        }

    }
    
    

}