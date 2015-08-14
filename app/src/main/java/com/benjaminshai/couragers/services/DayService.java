package com.benjaminshai.couragers.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.benjaminshai.couragers.BeanParser;
import com.benjaminshai.couragers.ResponseStatus;
import com.benjaminshai.couragers.beans.Day;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by bshai on 8/13/15.
 */
public class DayService extends IntentService {

    private static final String TAG = "DayService";
    private static final String DAYS_URL = "http://teddysappserver.com:8080/api/days";

    public DayService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if (command.equals("query")) {
            receiver.send(ResponseStatus.RUNNING, Bundle.EMPTY);
            try {
                String JSON = getJSON();
                ArrayList<Day> days = new BeanParser().parse(JSON);
                b.putParcelableArrayList("results", days);
                receiver.send(ResponseStatus.FINISHED, b);
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(ResponseStatus.ERROR, b);
            }
        }

    }

    private String getJSON() throws IOException {

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(DAYS_URL)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().string();

    }
}
