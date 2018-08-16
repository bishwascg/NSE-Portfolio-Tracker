package com.bishwascgupta.mystockalert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.db.Shares;

import java.util.ArrayList;


public class AlertlistAdapter extends ArrayAdapter<Shares> {

    private ArrayList<Shares> alertList = new ArrayList<>();
    private Context context;

    public AlertlistAdapter(@NonNull Context context, int resource, ArrayList<Shares> objects) {
        super(context, resource, objects);
        this.context = context;
        this.alertList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.alertlist, null);
        TextView stockName = v.findViewById(R.id.stockname_text);
        TextView lower = v.findViewById(R.id.below_text);
        TextView upper = v.findViewById(R.id.above_text);
        TextView cmp = v.findViewById(R.id.cmp_text);

        stockName.setText(alertList.get(position).getStockName());

        if(alertList.get(position).getLower() == -9.9)
            lower.setText("LOWER:\n NaN");

        else
            lower.setText("LOWER:\n" + Float.toString(alertList.get(position).getLower()));

        if(alertList.get(position).getUpper() == -9.9)
            upper.setText("UPPER:\n NaN");
        else
            upper.setText("UPPER:\n" + Float.toString(alertList.get(position).getUpper()));

        cmp.setText(Float.toString(alertList.get(position).getCmp()));


        return v;

    }

}