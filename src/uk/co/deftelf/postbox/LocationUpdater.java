package uk.co.deftelf.postbox;

import android.location.Location;

public interface LocationUpdater {
    
    public void locationFound(Location newLocation, boolean gps);

}
