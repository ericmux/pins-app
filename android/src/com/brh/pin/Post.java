package com.brh.pin;

import android.location.Location;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Post {
    public Post() {

    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private String creator;
    private String content;
    private Location location;

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


    public UrlEncodedFormEntity getFormEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("content", content));
        nameValuePairs.add(new BasicNameValuePair("creator", creator));
        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(location.getLatitude())));
        nameValuePairs.add(new BasicNameValuePair("long", String.valueOf(location.getLongitude())));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return urlEncodedFormEntity;
    }

    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("creator", creator);
            json.put("content", content);
            json.put("lat", location.getLatitude());
            json.put("long", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    };

}
