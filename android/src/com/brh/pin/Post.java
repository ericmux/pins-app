package com.brh.pin;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private final String creator;
    private final String content;
    private final Location location;

    public Post(String content, Location location, String creator) {
        this.creator = creator;
        this.content = content;
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public Location getLocation() {
        return location;
    }

    public String getCreator() {
        return creator;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("creator", creator);
        map.put("content", content);
        map.put("lat", String.valueOf(location.getLatitude()));
        map.put("long", String.valueOf(location.getLongitude()));
        return map;
    }
}
