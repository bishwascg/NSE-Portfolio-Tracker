package com.bishwascgupta.mystockalert.asynctasks;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.bishwascgupta.mystockalert.ActivityMain;
import com.bishwascgupta.mystockalert.R;
import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class QuandlAddStock extends AsyncTask<String , Integer ,String> {

    private ProgressDialog progressDialog ;
    private String server_response;
    private Context context;
    private View rootView;
    private Activity activity;

    public QuandlAddStock(Context c, View v) {
        this.context = c;
        this.rootView = v;
        this.activity = (Activity)c;
    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", server_response);
                return  server_response;
            }
            else
                return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        if(s!=null) {
            AutoCompleteTextView stockname_ip = (AutoCompleteTextView) rootView.findViewById(R.id.input_stockname);
            EditText noshares_ip = (EditText) rootView.findViewById(R.id.input_noofshares);

            try {
                JSONObject json = new JSONObject(server_response);
                JSONObject dataset = json.getJSONObject("dataset");
                JSONArray data = dataset.getJSONArray("data");
                JSONArray details = data.getJSONArray(0);
                JSONArray prev = data.getJSONArray(1);


                DB db = new DB(context);

                Log.i("h", details.getString(5));


                int success = db.addStockPortfolio(new Shares(stockname_ip.getText().toString(),
                        Float.parseFloat(details.getString(4)),
                        100*(Float.parseFloat(details.getString(4))-Float.parseFloat(prev.getString(5)))/
                                Float.parseFloat(prev.getString(5)),
                        Integer.parseInt(noshares_ip.getText().toString())));

                db.close();

                if (success == 0) {
                    this.progressDialog.dismiss();
                    Toast msg = Toast.makeText(context, R.string.sym_presenterror, Toast.LENGTH_LONG);
                    msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    msg.show();
                } else {
                    this.progressDialog.dismiss();
                    Intent i = new Intent(context, ActivityMain.class);
                    activity.setResult(RESULT_OK, i);
                    activity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else
        {
            Toast msg = Toast.makeText(context, R.string.error_net, Toast.LENGTH_SHORT);
            msg.setGravity(Gravity.CENTER_VERTICAL, 0,0);
            msg.show();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage(activity.getResources().getString(R.string.fetch));
        this.progressDialog.show();

    }


    @Override
    protected void onProgressUpdate (Integer... progress)
    {
        this.progressDialog.setProgress(progress[0]);
    }


    // Converting InputStream to String
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
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

    public String buildURL(Context c, String symbol)
    {
        return c.getString(R.string.url_prefix)+symbol+c.getString(R.string.url_suffix);
    }
}





