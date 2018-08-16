package com.bishwascgupta.mystockalert;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UpdateService extends JobService {

    JobParameters params;
    public static final String LAST_SYNCED_AT = "last_sync";


    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        Log.d("UpdateService", "Work to be called from here");
        /*
        //Get current Time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //If it is a weekday
        if(day == Calendar.MONDAY || day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                day == Calendar.THURSDAY || day == Calendar.FRIDAY)
        {
            //If Time is more than 7.15
            if(calendar.get(Calendar.HOUR_OF_DAY) >= 19)
            {
                if(calendar.get(Calendar.MINUTE) >= 15)
                {
                    //Call the Update Thread
                    Thread thread = new Thread(new updateThread());
                    thread.start();
                }
            }
        }
*/
        Thread thread = new Thread(new updateThread());
        thread.start();

        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("UpdateService", "System calling to stop the job here");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateService", "Job Destroyed!!!!");
    }


    private class updateThread implements Runnable {

        private ArrayList<String> server_response = new ArrayList<>();


        @Override
        public void run() {

            DB db = DB.getInstance(getApplicationContext());
            ArrayList<String> stocklist;
            stocklist = db.getAllStocksNames();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Date servDate = null;

            Log.d("UpdateService", "In doInBackground" + String.valueOf(stocklist.size()));
            URL url;
            HttpURLConnection urlConnection;

            for (int i = 0; i < stocklist.size(); i++) {
                try {
                    url = new URL(Util.buildURL(stocklist.get(i), getApplicationContext()));
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setConnectTimeout(10000);
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String response = Util.readStream(urlConnection.getInputStream());
                        String date = Util.getDate(response);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        servDate = sdf.parse(date);
                        String last = preferences.getString(LAST_SYNCED_AT, "");
                        //It is first iteration or last sync was before server date
                        if (last.equals("") || sdf.parse(last).before(servDate)) {
                            Log.d("UpdateService", "Server updated");
                            server_response.add(response);
                        } else {
                            //your_date_is_outdated = false;
                            Log.d("UpdateService", "Data not updated");
                            break;
                        }
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

            }


            Log.d("UpdateService", String.valueOf(server_response.size()) + " " +
                    String.valueOf(stocklist.size()));



            if (server_response.size() != 0) {
                //stocklist = db.getAllStocksPortfolio();
                int oldValue = db.getAllStockPortfolioValue();
                for (int i = 0; i < server_response.size(); i++) {
                    try {

                        JSONObject json = new JSONObject(server_response.get(i));
                        JSONObject dataset = json.getJSONObject("dataset");
                        String name =  dataset.getString("dataset_code");
                        JSONArray data = dataset.getJSONArray("data");
                        JSONArray details = data.getJSONArray(0);
                        JSONArray prev = data.getJSONArray(1);

                        db.updateCmp(name, Float.parseFloat(details.getString(4)));
                        if (db.isPortfolio(name)) {

                            db.updateChange(name,
                                    100*(Float.parseFloat(details.getString(4))-Float.parseFloat(prev.getString(5)))/
                                            Float.parseFloat(prev.getString(5)));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int newValue = db.getAllStockPortfolioValue();

                if(newValue-oldValue != 0)
                    Util.createNotification("Today's change is " + String.valueOf(newValue - oldValue),
                            getApplicationContext());
    /*
                if (servDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(LAST_SYNCED_AT, sdf.format(servDate));
                    edit.apply();
                }

*/
            }

            db.close();
            jobFinished(params, false);
        }
    }
}