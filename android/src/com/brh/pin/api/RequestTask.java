package com.brh.pin.api;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

class RequestTask extends AsyncTask<Void, Long, String> {

    private String url;
    private UrlEncodedFormEntity formEntity;
    private final RequestTask.Callback finished;

    public RequestTask(String url, UrlEncodedFormEntity formEntity, RequestTask.Callback finished) {
        this.url = url;
        this.formEntity = formEntity;
        this.finished = finished;
    }

    @Override
    protected String doInBackground(Void... ignored) {
        HttpResponse httpResponse = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(formEntity);

            httpResponse = httpClient.execute(httpPost);
            Log.e("bizu", httpResponse.getStatusLine().toString());
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String jsonReturned) {
        if (finished != null)
            finished.onFinished(jsonReturned);
    }

    public interface Callback {
        void onFinished(String jsonReceived);
    }
}
