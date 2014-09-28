package com.brh.pin.model;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LatLongL {
    private double mLat;
    private double mLong;

    public double getLat() {
        return mLat;
    }

    public void setLat(double mLat) {
        this.mLat = mLat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double mLong) {
        this.mLong = mLong;
    }

    public UrlEncodedFormEntity getFormEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(mLat)));
        nameValuePairs.add(new BasicNameValuePair("long", String.valueOf(mLong)));

        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return urlEncodedFormEntity;
    }
}
