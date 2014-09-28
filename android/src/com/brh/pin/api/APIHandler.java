package com.brh.pin.api;

import android.content.Context;
import com.brh.pin.Post;

public class APIHandler {
    private final String URL_BASE = "http://pins-awareness-app.herokuapp.com";
    private final String URL_SAVE_POST = URL_BASE + "/save_post";
    private final String URL_GET_POSTS = URL_BASE + "/get_posts";
    private Context context;


    public void savePost(Post post) {
        RequestTask task = new RequestTask(URL_SAVE_POST, post.getFormEntity());
        task.execute();
    }


}
