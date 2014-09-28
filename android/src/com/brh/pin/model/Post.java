package com.brh.pin.model;

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


    private String creator;
    private String content;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;

    public Post(String creator, String content, double latitude, double longitude) {
        this.creator = creator;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getContent() {
        return content;
    }

    public String getCreator() {
        return creator;
    }


    public UrlEncodedFormEntity getFormEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("content", content));
        nameValuePairs.add(new BasicNameValuePair("creator", creator));
        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
        nameValuePairs.add(new BasicNameValuePair("long", String.valueOf(longitude)));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return urlEncodedFormEntity;
    }

    public static Post fromJson(JSONObject json) {
        String content, creator;
        double latitude, longitude;

        try {
            content = json.getString("content");
            creator = json.getString("creator");
            latitude = json.getDouble("lat");
            longitude = json.getDouble("long");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return new Post(creator, content, latitude, longitude);
    }

    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("creator", creator);
            json.put("content", content);
            json.put("lat", latitude);
            json.put("long", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    };

}
