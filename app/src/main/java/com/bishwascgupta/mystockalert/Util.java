package com.bishwascgupta.mystockalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import com.bishwascgupta.mystockalert.db.Shares;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class Util {

    static void createNotification(String contentText, Context context) {


        NotificationManager mNotificationManager;
        int NOTIFICATION_ID = 1;

        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);


        Intent myIntent = new Intent(context, ActivityMain.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,
                myIntent, 0);

        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(context)
               // .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Stocks Updated")
                .setContentText(contentText)
                .setContentIntent(pendingIntent);

        //Show the notification
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }


    static String getDate(String jsonResponse)
    {
        String date = null;

        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONObject dataset = json.getJSONObject("dataset");
            date = dataset.getString("newest_available_date");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static Shares getStockResponse(String server_response, int noOfShares) throws JSONException {

        JSONObject json = new JSONObject(server_response);
        JSONObject dataset = json.getJSONObject("dataset");
        JSONArray data = dataset.getJSONArray("data");
        JSONArray details = data.getJSONArray(0);
        JSONArray prev = data.getJSONArray(1);

        return new Shares(dataset.getString("dataset_code"),
                Float.parseFloat(details.getString(4)),
                100 * (Float.parseFloat(details.getString(4)) - Float.parseFloat(prev.getString(5))) /
                        Float.parseFloat(prev.getString(5)), noOfShares);

    }


    public static String buildURL(String symbol, Context c)
    {
        return c.getString(R.string.url_prefix)+symbol+c.getString(R.string.url_suffix);
    }


    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static void showToast(String message, Context context){
        Toast msg = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.CENTER_VERTICAL, 0,0);
        msg.show();
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


}
