package uk.co.deftelf.postbox;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PostboxOverlayItem extends OverlayItem {
    
    final Postbox postbox;

    public PostboxOverlayItem(GeoPoint point, String title, String snippet, Postbox postbox) {
        super(point, title, snippet);
        this.postbox = postbox;
    }
    
    

}
