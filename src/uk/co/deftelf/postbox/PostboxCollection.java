package uk.co.deftelf.postbox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.PopupWindow;


public class PostboxCollection extends
        uk.co.deftelf.postbox.common.PostboxCollection {
    
    protected PostboxCollection() {
        
    }

    /*private PostboxCollection(Postbox[] postboxes, double minLat, double maxLat,
            double minLon, double maxLon) {
        super(postboxes, minLat, maxLat, minLon, maxLon);
        
    }*/
    
    public static PostboxCollection readFromJson(JSONObject source) throws JSONException {
        PostboxCollection postboxColl = new PostboxCollection();
        postboxColl.centreLat = source.getDouble("centreLat");
        postboxColl.centreLon = source.getDouble("centreLon");
        JSONArray jsonBoxes = source.getJSONArray("postboxes");
        Postbox[] postboxItems = new Postbox[jsonBoxes.length()];
        for (int i=0; i < jsonBoxes.length(); i++) {
            JSONObject box = jsonBoxes.getJSONObject(i);
            Postbox p = Postbox.readFromJson(box);
            postboxItems[i] = p;   
        }
        postboxColl.postboxes = postboxItems;
        return postboxColl;
    }

    
}
