package com.bishwascgupta.mystockalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;


public class ActivityEditAlert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alert);

        final String stockname = getIntent().getExtras().getString("stockname");

        final TextView stockname_tv = findViewById(R.id.editalert_stockname);
        stockname_tv.setText(stockname);


        final EditText lower = findViewById(R.id.editalert_lower);
        final EditText upper = findViewById(R.id.editalert_upper);
        float low = getIntent().getExtras().getFloat("lower");
        float up = getIntent().getExtras().getFloat("upper");
        lower.setText(String.valueOf(low));
        upper.setText(String.valueOf(up));

        //qty.setText(getIntent().getExtras().getInt("qty"));

        Button cancelbtn = findViewById(R.id.editalert_cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button savebtn = findViewById(R.id.editalert_donebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(upper.getText().toString().matches("[-+]?\\d*\\.?\\d+") ||
                        lower.getText().toString().matches("[-+]?\\d*\\.?\\d+")) {
                    DB db = new DB(getApplicationContext());

                    String name = stockname_tv.getText().toString().toUpperCase();
                    float up = -99;
                    float low = -99;
                    if (upper.getText().toString().matches("[-+]?\\d*\\.?\\d+"))
                        up = Float.parseFloat(upper.getText().toString());
                    if (lower.getText().toString().matches("[-+]?\\d*\\.?\\d+"))
                        low = Float.parseFloat(lower.getText().toString());


                   db.updateStockAlert(new Shares(name, low, up));

                    db.close();
                    setResult(RESULT_OK);
                    finish();
                }

                else {
                        Toast msg = Toast.makeText(getApplicationContext(), R.string.alert_presenterror, Toast.LENGTH_SHORT);
                        msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        msg.show();
                    }

            }
        });

    }


}
