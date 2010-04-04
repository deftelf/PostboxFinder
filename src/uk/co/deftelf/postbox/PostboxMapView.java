package uk.co.deftelf.postbox;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.google.android.maps.MapView;

public class PostboxMapView extends MapView {
    
    protected long lastTouched = -1;

    public PostboxMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public PostboxMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public PostboxMapView(Context context, String apiKey) {
        super(context, apiKey);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP)
            lastTouched = Calendar.getInstance().getTimeInMillis();
        return super.onTouchEvent(ev);
    }
    
    

}
