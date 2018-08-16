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


public class ActivityEditPortfolio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portfolio);

        final String stockname = getIntent().getExtras().getString("stockname");

        TextView stockname_tv = findViewById(R.id.editportfolio_stockname);
        stockname_tv.setText(stockname);


        final EditText qty = findViewById(R.id.editportfolio_qty);
        int numshares = getIntent().getExtras().getInt("qty");
        qty.setText(String.valueOf(numshares));

        //qty.setText(getIntent().getExtras().getInt("qty"));

        Button cancelbtn = findViewById(R.id.editportfolio_cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button savebtn = findViewById(R.id.editportfolio_donebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qty.getText().toString().matches("[-+]?\\d*\\.?\\d+"))
                {
                    DB db = new DB(getApplicationContext());
                    Shares oldinfo = db.getStock(stockname);
                    db.updateStockPortfolio(new Shares(oldinfo.getStockName(), oldinfo.getCmp(),
                            oldinfo.getChange(), Integer.parseInt(qty.getText().toString())));
                    db.close();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast msg = Toast.makeText(getApplicationContext(), R.string.num_notfound, Toast.LENGTH_SHORT);
                    msg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    msg.show();

                }

            }
        });

    }


}
