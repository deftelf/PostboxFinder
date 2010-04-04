package uk.co.deftelf.postbox;

import java.util.*;

public class PostboxDictionary {
    
    private Hashtable<String, Postbox> postboxes = new Hashtable<String, Postbox>();
    
    public void addAll(PostboxCollection newBoxes) {
        for (Postbox box : ((Postbox[])newBoxes.getPostboxes()) ) {
            if (!postboxes.containsKey(box.getRef()))
                postboxes.put(box.getRef(), box);
        }
    }
    
    public Postbox get(String ref) {
        return postboxes.get(ref);
    }
    
    public Collection<Postbox> getAll() {
        return postboxes.values();
    }

}
