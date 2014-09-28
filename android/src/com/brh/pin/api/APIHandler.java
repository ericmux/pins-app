package com.brh.pin.api;

import android.content.Context;
import com.brh.pin.User;
import android.util.Log;
import com.brh.pin.model.LatLongL;
import com.brh.pin.model.Post;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class APIHandler  {
    private final String URL_BASE = "http://pins-awareness-app.herokuapp.com";
    private final String URL_SAVE_POST = URL_BASE + "/save_post";
    private final String URL_GET_POSTS = URL_BASE + "/get_posts";
    private final String URL_SIGN_UP = URL_BASE + "/signup_user";
    private final int radius = 1000;
    private Context context;


    public void savePost(Post post) {
        RequestTask task = new RequestTask(URL_SAVE_POST, post.getFormEntity(), null);
        task.execute();
    }

    public void signUp(User user){
        RequestTask task = new RequestTask(URL_SIGN_UP, user.getFormEntity(), null);
        task.execute();
    }

    public void getPosts(LatLongL latLong, final GetPostsListener getPostsListener) {
        RequestTask task = new RequestTask(URL_GET_POSTS, latLong.getFormEntity(), new RequestTask.Callback() {

            @Override
            public void onFinished(HttpResponse httpResponse) {
                List<Post> posts = new ArrayList<Post>();
                final HttpEntity entity = httpResponse.getEntity();
                try {
                    JSONArray jsonArray = new JSONArray(entity);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        posts.add(Post.fromJson(jsonArray.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    Log.e("pinapp", "Error parsing server response.");
                    e.printStackTrace();
                    getPostsListener.onGotPosts(new ArrayList<Post>());
                }
            }
        });

    }

    interface GetPostsListener {
        void onGotPosts(List<Post> posts);
    }
}
