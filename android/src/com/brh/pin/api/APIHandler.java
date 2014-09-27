package com.brh.pin.api;

import com.brh.pin.Post;
import org.json.JSONException;

import java.io.IOException;

public class APIHandler {
    private final String URL_BASE = "http://pins-awareness-app.herokuapp.com";
    private final String URL_SAVE_POST = URL_BASE + "/save_post";
    private final String URL_GET_POSTS = URL_BASE + "/get_posts";


    public void savePost(Post post) {
        try {
            WebUtils.makeRequest(URL_SAVE_POST, post.getMap());
        } catch(IOException  e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
