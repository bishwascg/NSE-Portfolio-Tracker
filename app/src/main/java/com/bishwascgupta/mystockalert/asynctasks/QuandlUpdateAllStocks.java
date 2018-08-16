package com.bishwascgupta.mystockalert.asynctasks;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.AlertlistAdapter;
import com.bishwascgupta.mystockalert.FragmentMyAlert;
import com.bishwascgupta.mystockalert.FragmentMyPortfolio;
import com.bishwascgupta.mystockalert.R;
import com.bishwascgupta.mystockalert.StocklistAdapter;
import com.bishwascgupta.mystockalert.Util;
import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class QuandlUpdateAllStocks extends AsyncTask<String , Integer ,String> {

    private ArrayList<String> server_response = new ArrayList<>();
    private WeakReference<Context> context;
    private ArrayList<String> stocklist;


    public QuandlUpdateAllStocks(Context c) {
        context = new WeakReference<>(c);
    }


    @Override
    protected String doInBackground(String... strings) {

        DB db = new DB(context.get());
        stocklist = db.getAllStocksNames();
        URL url;
        HttpURLConnection urlConnection;
        for(int i=0; i<db.getAllStocksNames().size();i++)
        {
            try {
                url = new URL(Util.buildURL(stocklist.get(i), context.get()));
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String response = Util.readStream(urlConnection.getInputStream());
                    server_response.add(response);
                }
            } catch(IOException e)
            {
                e.printStackTrace();
            }

        }
        db.close();

        if(server_response.size() == stocklist.size())
            return "responses_recd_not_null";
        else
            return null;

    }


    @Override
    protected void onPostExecute(String s) {
        DB db = new DB(context.get());

        if(s!=null) {

            for (int i = 0; i < server_response.size(); i++) {
                try {
                    JSONObject json = new JSONObject(server_response.get(i));
                    JSONObject dataset = json.getJSONObject("dataset");
                    JSONArray data = dataset.getJSONArray("data");
                    JSONArray details = data.getJSONArray(0);
                    JSONArray prev = data.getJSONArray(1);

                    db.updateCmp(stocklist.get(i), Float.parseFloat(details.getString(4)));
                    if (db.isPortfolio(stocklist.get(i))) {

                        db.updateChange(stocklist.get(i),
                                100*(Float.parseFloat(details.getString(4))-Float.parseFloat(prev.getString(5)))/
                                        Float.parseFloat(prev.getString(5)));

                    }
                }

                 catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        FragmentMyPortfolio.stocklist.clear();
        //FragmentMyAlert.alertlist.clear();
        FragmentMyPortfolio.stocklist.addAll(db.getAllStocksPortfolio());
        //FragmentMyAlert.alertlist.addAll(db.getAllStocksAlerts());

        ListView outer = ((Activity)context.get()).findViewById(R.id.stocklistview);
        ListView outer1 = ((Activity)context.get()).findViewById(R.id.alertlistview);
        TextView summary = ((Activity)context.get()).findViewById(R.id.stocksummary);

        FragmentMyPortfolio.myAdapter = new StocklistAdapter(context.get(),R.layout.stocklist,
                FragmentMyPortfolio.stocklist);
        //FragmentMyAlert.myAdapter = new AlertlistAdapter(context.get(), R.layout.alertlist, FragmentMyAlert.alertlist);

        String summaryText = context.get().getResources().getString(R.string.summary)
                + String.format(Locale.getDefault(),"%d", FragmentMyPortfolio.myAdapter.getPortfolioSummary());
        summary.setText(summaryText);

        outer.setAdapter(FragmentMyPortfolio.myAdapter);
       // outer1.setAdapter(FragmentMyAlert.myAdapter);

        db.close();

        SwipeRefreshLayout swipe = ((Activity)context.get()).findViewById(R.id.myportfolio_swipe);
        if(swipe.isRefreshing())
            swipe.setRefreshing(false);



    }

}





