package uk.co.deftelf.postbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.google.android.maps.*;

import de.android1.overlaymanager.*;
import de.android1.overlaymanager.ManagedOverlayGestureDetector.OnOverlayGestureListener;

public class Map extends MapActivity {

    PostboxMapView mv;

    LocationManager locManager;

    private Location lastLocation = null;

    private boolean mapDrawn = false;

    PostboxDictionary boxes = new PostboxDictionary();

    int hereLocation = -1;

    Timer drawPostboxThread;

    static final int INIT_ZOOM = 16;

    protected static final long POSTBOX_LOAD_DELAY = 1000;
    MyLocationOverlay myOverlay;
    Handler drawPostboxesHandler;
    
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onCreate(android.os.Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.map);
        mv = (PostboxMapView) findViewById(R.id.map);
        mv.setSatellite(false);
        mv.setBuiltInZoomControls(true);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        drawPostboxesHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                PostboxCollection newBoxes = (PostboxCollection) msg.obj;
                drawPostboxes(newBoxes);
                mv.invalidate();
            }
        };
        myOverlay = new MyLocationOverlay(this, mv);
        mv.getOverlays().add(myOverlay);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            
            myOverlay.enableMyLocation();
            

            if (lastLocation == null) {
                double lat = this.getIntent().getDoubleExtra("lat", 0);
                double lon = this.getIntent().getDoubleExtra("lon", 0);
                lastLocation = new Location("Main");
                lastLocation.setLatitude(lat);
                lastLocation.setLongitude(lon);
            }
            
            double lat = lastLocation.getLatitude();
            double lon = lastLocation.getLongitude();

            try {
                if ((lat == lon) && (lon == 0)) {
                    
                } else {
                    locationFound();
                }

                drawPostboxThread = new Timer();
                drawPostboxThread.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (mv.lastTouched != -1
                                && Calendar.getInstance().getTimeInMillis() > (mv.lastTouched + POSTBOX_LOAD_DELAY)) {
                            lastLocation = myOverlay.getLastFix();
                            PostboxCollection newBoxes = fetchPostboxes();
                            Message msg = new Message();
                            msg.obj = newBoxes;
                            drawPostboxesHandler.sendMessage(msg);
                            mv.lastTouched = -1;
                        }
                    }
                }, 0, 200);

            } catch (Exception e) {
                Log.e(this.getClass().getName(),
                        "Error occured showing the map", e);
            }

        } else {
            myOverlay.disableMyLocation();
            
            if (drawPostboxThread != null) {
                drawPostboxThread.cancel();
                drawPostboxThread = null;
            }
        }

    }

    private void drawMap() {
        mapDrawn = true;
        double lat = lastLocation.getLatitude();
        double lon = lastLocation.getLongitude();
        mv.getController().setZoom(INIT_ZOOM);
        mv.getController().setCenter(
                new GeoPoint((int) (lat * 1000000), (int) (lon * 1000000)));

        
        mv.getZoomButtonsController().setOnZoomListener(new OnZoomListener() {

            public void onZoom(boolean zoomIn) {
                if (!zoomIn) {
                    mv.getController().zoomOut();
                    mv.lastTouched = Calendar.getInstance().getTimeInMillis();
                } else {
                    mv.getController().zoomIn();
                }
            }

            public void onVisibilityChanged(boolean visible) {
                
            }
        });
    }


    private void drawPostboxes(PostboxCollection newBoxes) {

        boxes.addAll(newBoxes);

        Calendar now = Calendar.getInstance();

        ArrayList<OverlayItem> pinsActive = new ArrayList<OverlayItem>();
        ArrayList<OverlayItem> pinsPast = new ArrayList<OverlayItem>();
        for (Postbox aBox : boxes.getAll()) {
            if (!aBox.isDisplayed()) {
                OverlayItem item = new PostboxOverlayItem(new GeoPoint((int) (aBox
                        .getLatitude() * 1000000), (int) (aBox
                        .getLongitude() * 1000000)), aBox
                        .getLocationInfo1(), aBox.getLocationInfo1(), aBox);
                if (aBox.isStillToCollect(now)) {
                    pinsActive.add(item);
                } else {
                    pinsPast.add(item);
                }
                aBox.setDisplayed();
            }
        }

        Drawable marker = getResources()
                .getDrawable(R.drawable.pbox_pin_active);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
                .getIntrinsicHeight());
        mv.getOverlays().add(new MarkerList(marker, pinsActive));
        
        marker = getResources().getDrawable(R.drawable.pbox_pin_past);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
                .getIntrinsicHeight());
        mv.getOverlays().add(new MarkerList(marker, pinsPast));
        
    }

    private PostboxCollection fetchPostboxes() {
        GeoPoint topLeftCornerLoc = mv.getProjection().fromPixels(0, 0);
        GeoPoint bottomRightCornerLoc = mv.getProjection().fromPixels(
                mv.getWidth() - 1, mv.getHeight() - 1);

        double minlat = ((double) bottomRightCornerLoc.getLatitudeE6()) / 1000000;
        double minlon = ((double) topLeftCornerLoc.getLongitudeE6()) / 1000000;
        double maxlat = ((double) topLeftCornerLoc.getLatitudeE6()) / 1000000;
        double maxlon = ((double) bottomRightCornerLoc.getLongitudeE6()) / 1000000;
        
        try {
            double lat = lastLocation.getLatitude();
            double lon = lastLocation.getLongitude();
            Socket serverSocket = new Socket("defteaa1.miniserver.com", 38279);
            //Socket serverSocket = new Socket("192.168.1.123", 38279);

            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    serverSocket.getInputStream()));
            String s = null;

            s = new JSONStringer().object().key("method")
                    .value("searchEncoded").key("lat").value(lat).key("lon")
                    .value(lon).key("minlat").value(minlat).key("minlon")
                    .value(minlon).key("maxlat").value(maxlat).key("maxlon")
                    .value(maxlon).key("maxResults").value(100).endObject()
                    .toString();

            out.println(s);
            out.flush();

            String input = in.readLine();
            PostboxCollection retrieved = PostboxCollection
                    .readFromJson(new JSONObject(input));

            return retrieved;

        } catch (Exception ex) {
            Toast.makeText(
                            this,
                            "Sorry, unable to retrieve postbox locations at the current time.",
                            Toast.LENGTH_LONG).show();
            Log.e("uk.co.deftelf.postbox.Main",
                    "An error occured when retrieving the postbox data.", ex);
        }

        return null;
    }

    private class MarkerList extends ItemizedOverlay {

        private List<OverlayItem> items = new ArrayList<OverlayItem>();

        private Drawable marker = null;

        public MarkerList(Drawable defaultMarker, List<OverlayItem> pins) {
            super(defaultMarker);
            marker = defaultMarker;
            items.addAll(pins);

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return (OverlayItem) items.get(i);
        }

        @Override
        public void draw(android.graphics.Canvas canvas, MapView mapView,
                boolean shadow) {
            super.draw(canvas, mapView, shadow);

            boundCenterBottom(marker);
        }

        @Override
        protected boolean onTap(int i) {

            PostboxOverlayItem item = (PostboxOverlayItem) items.get(i);
            /*Drawable marker = getResources().getDrawable(R.drawable.pbox_pin_active);
            marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
            item.setMarker(marker);*/
            
            Intent intent = new Intent(mv.getContext(), PostboxInfo.class);
            item.postbox.calculateDistance(lastLocation.getLatitude(), lastLocation.getLongitude());
            intent.putExtra("postbox", item.postbox);
            startActivity(intent);
            
            //item.setMarker(null);
            
            return (true);
        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return items.size();
        }

    }

    public void locationFound(Location newLocation) {

        double lat = newLocation.getLatitude();
        double lon = newLocation.getLongitude();

        lastLocation = newLocation;
        locationFound();
    }

    private void locationFound() {
        if (!mapDrawn) {
            drawMap();
            PostboxCollection newBoxes = fetchPostboxes();
            drawPostboxes(newBoxes);
        }

    }

}
