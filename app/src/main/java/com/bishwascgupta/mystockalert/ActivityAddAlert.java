package com.bishwascgupta.mystockalert;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bishwascgupta.mystockalert.asynctasks.QuandlAddAlert;
import com.bishwascgupta.mystockalert.asynctasks.QuandlAddStock;
import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import java.util.Arrays;

public class ActivityAddAlert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        //Add Toolbar
      //  Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Get the array of the available stock symbols
        Resources r = getResources();
        final String stocklist[] = r.getStringArray(R.array.stocksymbollist);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, stocklist);
        final AutoCompleteTextView stockname_ip = findViewById(R.id.input_stockname);
        stockname_ip.setAdapter(adapter);

        final EditText upper_ip = findViewById(R.id.input_upper);
        final EditText lower_ip = findViewById(R.id.input_lower);


        Button cancelbtn = findViewById(R.id.addalert_cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button savebtn = findViewById(R.id.addalert_btn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Arrays.asList(stocklist).contains(stockname_ip.getText().toString().toUpperCase())) {

                    if(upper_ip.getText().toString().matches("[-+]?\\d*\\.?\\d+") ||
                            lower_ip.getText().toString().matches("[-+]?\\d*\\.?\\d+"))
                    {
                        DB db = new DB(getApplicationContext());

                        String name = stockname_ip.getText().toString().toUpperCase();



                        QuandlAddAlert q = new QuandlAddAlert(ActivityAddAlert.this, findViewById(R.id.addalert_root));
                        q.execute(q.buildURL(getApplicationContext(), stockname_ip.getText().toString()));




                    }
                    else {
                        Toast msg = Toast.makeText(getApplicationContext(), R.string.enter_one, Toast.LENGTH_SHORT);
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
