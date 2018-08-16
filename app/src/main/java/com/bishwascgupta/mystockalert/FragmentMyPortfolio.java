package com.bishwascgupta.mystockalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.asynctasks.QuandlUpdateAllStocks;
import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentMyPortfolio extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SwipeRefreshLayout swipe;
    private static final int DELETE_MENU_ITEM = 0;
    private static final int EDIT_MENU_ITEM = 1;

    public static  StocklistAdapter myAdapter;
    public static ArrayList<Shares> stocklist = new ArrayList<>();
    public static View rootView;
    public static ListView outer;

    public FragmentMyPortfolio() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentMyPortfolio newInstance(int sectionNumber) {
        FragmentMyPortfolio fragment = new FragmentMyPortfolio();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.myportfolio_fragment, container, false);

        outer = rootView.findViewById(R.id.stocklistview);
        final TextView summary = rootView.findViewById(R.id.stocksummary);

        swipe = rootView.findViewById(R.id.myportfolio_swipe);
        swipe.setColorSchemeColors(getResources().getColor(R.color.swipe2),
                getResources().getColor(R.color.swipe3));

        DB db = new DB(getContext());

        FragmentMyPortfolio.stocklist.clear();
        FragmentMyPortfolio.stocklist.addAll(db.getAllStocksPortfolio());

        db.close();

        FragmentMyPortfolio.myAdapter = new StocklistAdapter(getContext(),R.layout.stocklist,
                FragmentMyPortfolio.stocklist);
        outer.setAdapter(FragmentMyPortfolio.myAdapter);


        summary.setText(getString(R.string.summary)  + String.format("%d",
                FragmentMyPortfolio.myAdapter.getPortfolioSummary()));



        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                QuandlUpdateAllStocks q = new QuandlUpdateAllStocks(getContext());
                q.execute();
            }
        });

        registerForContextMenu(outer);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENTMYPORTFOLIO", "ONRESUME HERE");

        ListView outer = rootView.findViewById(R.id.stocklistview);
        TextView summary = rootView.findViewById(R.id.stocksummary);

        DB db = new DB(getContext());
        FragmentMyPortfolio.stocklist = db.getAllStocksPortfolio();
        db.close();

        FragmentMyPortfolio.myAdapter=new StocklistAdapter(getContext(),R.layout.stocklist,
                FragmentMyPortfolio.stocklist);
        summary.setText(getString(R.string.summary)  + String.format("%d",
                FragmentMyPortfolio.myAdapter.getPortfolioSummary()));
        outer.setAdapter(FragmentMyPortfolio.myAdapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("My Context Menu");
        menu.add(0, DELETE_MENU_ITEM, 0, "Delete");
        menu.add(0, EDIT_MENU_ITEM, 1, "Edit");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;

        switch (item.getItemId()) {
            case DELETE_MENU_ITEM:
            {
                DB db = new DB(getContext());
                stocklist = db.getAllStocksPortfolio();
                db.deleteStockPortfolio(stocklist.get(pos));
                StocklistAdapter myAdapter = new StocklistAdapter(getContext(), R.layout.stocklist, db.getAllStocksPortfolio());
                ListView v = getView().findViewById(R.id.stocklistview);
                v.setAdapter(myAdapter);

                TextView summary = getView().findViewById(R.id.stocksummary);
                summary.setText(getString(R.string.summary) + String.valueOf(myAdapter.getPortfolioSummary()));

                db.close();
                Util.showToast("Deleted", getContext());
                break;
        }
            case EDIT_MENU_ITEM:
                Intent intent = new Intent(getActivity(), ActivityEditPortfolio.class);
                intent.putExtra("stockname", stocklist.get(pos).getStockName());
                intent.putExtra("qty", stocklist.get(pos).getNoOfShares());
                startActivityForResult(intent,4444);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4444 && resultCode == RESULT_OK)
        {

            ListView outer = getView().findViewById(R.id.stocklistview);
            DB db = new DB(outer.getContext());
            TextView summary = getView().findViewById(R.id.stocksummary);

            StocklistAdapter myAdapter=new StocklistAdapter(outer.getContext(),R.layout.stocklist,db.getAllStocksPortfolio());
            summary.setText(getString(R.string.summary) + String.valueOf(myAdapter.getPortfolioSummary()));

            db.close();
            outer.setAdapter(myAdapter);
        }
    }



}


