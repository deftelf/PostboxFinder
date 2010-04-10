package uk.co.deftelf.postbox;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class PostboxInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.postboxinfo);
        
        Intent i = getIntent();
        Postbox displayed = (Postbox)i.getSerializableExtra("postbox");
        
        ((TextView) findViewById(R.id.boxlocation1)).setText(displayed.getLocationInfo1());
        ((TextView) findViewById(R.id.boxlocation2)).setText(displayed.getLocationInfo2());
        ((TextView) findViewById(R.id.boxref)).setText(displayed.getRef());
        ((TextView) findViewById(R.id.lastWeekdayCollection)).setText(displayed.getLastWeekdayCollectionReadable());
        ((TextView) findViewById(R.id.lastSaturdayCollection)).setText(displayed.getLastSaturdayCollectionReadable());
        double distance = displayed.getDistance();
        int distanceMetres = (int)(distance * 1000);
        ((TextView) findViewById(R.id.distanceFromPosition)).setText(distanceMetres + " metres");
        
    }
    
}
