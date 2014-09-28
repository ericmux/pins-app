package com.brh.pin.api;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

class RequestTask extends AsyncTask<Void, Long, Integer> {

    private String url;
    private UrlEncodedFormEntity formEntity;

    public RequestTask(String url, UrlEncodedFormEntity formEntity) {
        this.url = url;
        this.formEntity = formEntity;
    }

    @Override
    protected Integer doInBackground(Void... ignored) {
        HttpResponse httpResponse = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(formEntity);

            httpResponse = httpClient.execute(httpPost);
            Log.e("bizu", httpResponse.getStatusLine().toString());
            return httpResponse.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Integer result) {
        //Create a toast with the status code.
    }
}
