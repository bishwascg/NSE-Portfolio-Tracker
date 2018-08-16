package com.bishwascgupta.mystockalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.db.DB;
import com.bishwascgupta.mystockalert.db.Shares;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Bishwas C Gupta on 01-12-2017.
 */



public class FragmentMyAlert extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static  AlertlistAdapter myAdapter;
    public static ArrayList<Shares> alertlist = new ArrayList<>();
    public static View rootView;

    public FragmentMyAlert() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentMyAlert newInstance(int sectionNumber) {
        FragmentMyAlert fragment = new FragmentMyAlert();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.myalert_fragment, container, false);
        ListView outer = (ListView) rootView.findViewById(R.id.alertlistview);

        DB db = new DB(getContext());

        FragmentMyAlert.alertlist.clear();
        FragmentMyAlert.alertlist.addAll(db.getAllStocksAlerts());

        db.close();

        FragmentMyAlert.myAdapter = new AlertlistAdapter(getContext(),R.layout.alertlist,
                FragmentMyAlert.alertlist);
        outer.setAdapter(FragmentMyAlert.myAdapter);

        outer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                DB db = new DB(getContext());
                alertlist = db.getAllStocksAlerts();
                db.close();
                Intent intent = new Intent(getActivity(), ActivityEditAlert.class);
                intent.putExtra("stockname", alertlist.get(pos).getStockName());
                intent.putExtra("lower", alertlist.get(pos).getLower());
                intent.putExtra("upper", alertlist.get(pos).getUpper());
                startActivityForResult(intent,5678);
            }
        });

        registerForContextMenu(outer);

        return rootView;
    }

    private static final int DELETE_MENU_ITEM = 2;
    private static final int EDIT_MENU_ITEM = 3;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("My Context Menu");
        menu.add(0, DELETE_MENU_ITEM, 2, "Delete");
        menu.add(0, EDIT_MENU_ITEM, 3, "Edit");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;

        switch (item.getItemId()) {
            case DELETE_MENU_ITEM:
            {
                DB db = new DB(getContext());
                alertlist = db.getAllStocksAlerts();
                db.deleteStockAlert(alertlist.get(pos));
                AlertlistAdapter myAdapter = new AlertlistAdapter(getContext(), R.layout.alertlist,
                        db.getAllStocksAlerts());
                ListView v = getView().findViewById(R.id.alertlistview);
                v.setAdapter(myAdapter);

                db.close();
                Util.showToast("Deleted", getContext());
                break;
            }
            case EDIT_MENU_ITEM:
            {
                DB db = new DB(getContext());
                alertlist = db.getAllStocksAlerts();
                db.close();
                Intent intent = new Intent(getActivity(), ActivityEditAlert.class);
                intent.putExtra("stockname", alertlist.get(pos).getStockName());
                intent.putExtra("lower", alertlist.get(pos).getLower());
                intent.putExtra("upper", alertlist.get(pos).getUpper());
                startActivityForResult(intent,5678);
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5678 && resultCode == RESULT_OK)
        {

            ListView outer = getView().findViewById(R.id.alertlistview);
            DB db = new DB(outer.getContext());

            AlertlistAdapter myAdapter=new AlertlistAdapter
                    (outer.getContext(),R.layout.alertlist,db.getAllStocksAlerts());

            db.close();
            outer.setAdapter(myAdapter);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENTALERT", "ONRESUME HERE");

        ListView outer = rootView.findViewById(R.id.alertlistview);

        DB db = new DB(getContext());
        FragmentMyAlert.alertlist = db.getAllStocksAlerts();
        db.close();

        FragmentMyAlert.myAdapter=new AlertlistAdapter(getContext(),R.layout.alertlist,
                FragmentMyAlert.alertlist);

        outer.setAdapter(FragmentMyAlert.myAdapter);

    }

}