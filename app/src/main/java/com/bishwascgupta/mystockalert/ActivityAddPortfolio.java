package com.bishwascgupta.mystockalert;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bishwascgupta.mystockalert.asynctasks.QuandlAddStock;

import java.util.Arrays;

public class ActivityAddPortfolio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_portfolio);

        //Add Toolbar
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Get the array of the available stock symbols
        Resources r = getResources();
        final String stocklist[] = r.getStringArray(R.array.stocksymbollist);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, stocklist);
        final AutoCompleteTextView stockname_ip = (AutoCompleteTextView) findViewById(R.id.input_stockname);
        stockname_ip.setAdapter(adapter);

        final EditText noshares_ip = (EditText) findViewById(R.id.input_noofshares);

        Button cancelbtn = (Button) findViewById(R.id.addportfolio_cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button savebtn = (Button) findViewById(R.id.addportfolio_btn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Arrays.asList(stocklist).contains(stockname_ip.getText().toString().toUpperCase())) {

                    if(noshares_ip.getText().toString().matches("[-+]?\\d*\\.?\\d+"))
                    {

                        QuandlAddStock q = new QuandlAddStock(ActivityAddPortfolio.this, findViewById(R.id.addportfolio_root));
                        q.execute(q.buildURL(getApplicationContext(), stockname_ip.getText().toString()));
                        /*
                        ContentValues values = new ContentValues();
                        try {
                            String response = SyncAdapter.getResponse(stockname_ip.getText().toString().toUpperCase()
                                    ,getApplicationContext());

                            if(response!=null){
                                Stock stock = SyncAdapter.parseJSON(response);
                                values.put(StockContract.Portfolio.COL_STOCK_NAME, stock.getStockName());
                                values.put(StockContract.Portfolio.COL_NUMSHARES, stock.getNoOfShares());
                                values.put(StockContract.Portfolio.COL_CHANGE, stock.getChange());
                                values.put(StockContract.Portfolio.COL_CMP, stock.getCmp());
                                getContentResolver().insert(StockContract.Portfolio.CONTENT_URI, values);
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        */

                    }
                    else {
                        Toast msg = Toast.makeText(getApplicationContext(), R.string.num_notfound, Toast.LENGTH_SHORT);
                        msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        msg.show();

                    }


                }
                else {
                    Toast msg = Toast.makeText(getApplicationContext(), R.string.sym_notfound, Toast.LENGTH_SHORT);
                    msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    msg.show();
                }
            }
        });


    }

}

/*
    private class QuandlAddStock extends AsyncTask<String , Integer ,String> {

        final ProgressDialog progressDialog = ProgressDialog.show(ActivityAddPortfolio.this,
                "",getString(R.string.fetch));
        String server_response;

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
                AutoCompleteTextView stockname_ip = (AutoCompleteTextView) findViewById(R.id.input_stockname);
                EditText noshares_ip = (EditText) findViewById(R.id.input_noofshares);

                try {
                    JSONObject json = new JSONObject(server_response);
                    JSONObject dataset = json.getJSONObject("dataset");
                    JSONArray data = dataset.getJSONArray("data");
                    JSONArray details = data.getJSONArray(0);
                    JSONArray prev = data.getJSONArray(1);


                    DBHelper db = new DBHelper(getApplicationContext());

                    Log.i("h", details.getString(5));
                    int success = db.addStockPortfolio(new StockInfo(stockname_ip.getText().toString(),
                            Float.parseFloat(details.getString(4)),
                            100*(Float.parseFloat(details.getString(4))-Float.parseFloat(prev.getString(5)))/
                            Float.parseFloat(prev.getString(5)),
                            Integer.parseInt(noshares_ip.getText().toString())));
                    db.close();

                    if (success == 0) {
                        this.progressDialog.dismiss();
                        Toast msg = Toast.makeText(getApplicationContext(), R.string.sym_presenterror, Toast.LENGTH_LONG);
                        msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        msg.show();
                    } else {
                        this.progressDialog.dismiss();
                        Intent i = new Intent(ActivityAddPortfolio.this, ActivityMain.class);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else
            {
                Toast msg = Toast.makeText(getApplicationContext(), R.string.error_net, Toast.LENGTH_SHORT);
                msg.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                msg.show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog.setCancelable(true);
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

}


*/
