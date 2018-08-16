package com.bishwascgupta.mystockalert;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.db.Shares;

import java.util.ArrayList;



public class StocklistAdapter extends ArrayAdapter<Shares> {

    private ArrayList<Shares> stockList = new ArrayList<>();
    private Context context;

    public StocklistAdapter(@NonNull Context context, int resource, ArrayList<Shares> objects) {
        super(context, resource, objects);
        this.context = context;
        this.stockList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.stocklist, null);
        TextView stockName = v.findViewById(R.id.stockname_textview);
        TextView change = v.findViewById(R.id.change_textview);
        TextView cmp = v.findViewById(R.id.cmp_textview);
        TextView noOfShares = v.findViewById(R.id.noofshares_textview);
        TextView value = v.findViewById(R.id.value_textview);

        stockName.setText(stockList.get(position).getStockName());
        change.setText(Float.toString(stockList.get(position).getChange()) + "%");
        cmp.setText("CMP:\n" + Float.toString(stockList.get(position).getCmp()));
        noOfShares.setText("#Shares:\n" + Integer.toString(stockList.get(position).getNoOfShares()));
        value.setText("Value:\n" + Integer.toString(stockList.get(position).getValue()));


        if (stockList.get(position).getChange() < 0.0) {
            change.setTextColor(Color.parseColor("#C62828"));
        }
        else
            change.setTextColor(Color.parseColor("#2E7D32"));



        return v;

    }

    public int getPortfolioSummary()
    {
        int sum = 0;
        for(int i=0;i<stockList.size();i++)
            sum += stockList.get(i).getValue();
        return sum;
    }

}
