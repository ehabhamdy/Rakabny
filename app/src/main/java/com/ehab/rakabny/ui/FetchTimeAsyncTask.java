package com.ehab.rakabny.ui;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ehab.rakabny.model.Legs;
import com.ehab.rakabny.model.Route;
import com.ehab.rakabny.model.TimeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ehabhamdy on 1/10/18.
 */

public class FetchTimeAsyncTask extends AsyncTask<String, Void, Route> {
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    public final String ORIGIN_PARAM = "origin";
    public final String DESTINATION_PARAM = "destination";
    public final String DEPT_TIME_PARAM = "departure_time";
    public final String TRAFFIC_MODE_PARAM = "traffic_model";
    public final String KEY_PARAM = "key";

    @Override
    protected Route doInBackground(String... params) {
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(ORIGIN_PARAM, params[0])
                .appendQueryParameter(DESTINATION_PARAM, params[1])
                .appendQueryParameter(DEPT_TIME_PARAM, params[2])
                .appendQueryParameter(TRAFFIC_MODE_PARAM, "best_guess")
                .appendQueryParameter(KEY_PARAM, params[3])
                .build();

        String stringURL = builtUri.toString();
        Log.d("My Request URL", stringURL);

        TimeData data = null;

        try {
            //URL url = new URL(builtUri.toString());
            URL url = new URL(stringURL);
            String jsonDataResponse = getResponseFromHttpUrl_v2(url);
            JSONObject obj = new JSONObject(jsonDataResponse);
            Gson gson = new GsonBuilder().create();
            data = gson.fromJson(obj.toString(), TimeData.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.getRoutes().get(0)/*.getLegs().get(0).getDuration().getText()*/;
    }

    public static String getResponseFromHttpUrl_v2(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
