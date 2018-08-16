package com.bishwascgupta.mystockalert;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bishwascgupta.mystockalert.db.DB;

import java.util.Locale;

import static com.bishwascgupta.mystockalert.Util.getRandomNumberInRange;


public class ActivityMain extends AppCompatActivity {




    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public long SYNC_FREQUENCY = 2 * 60 * 1000L;
    public static final String IS_JOB_FIRST_RUN = "job_scheduled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter mSectionsPagerAdapter;

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), UpdateService.class);


       // Log.d("UpdateService", String.valueOf(jobScheduler.getAllPendingJobs().size()));


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        assert jobScheduler != null;
        if (preferences.getBoolean(IS_JOB_FIRST_RUN, true) || jobScheduler.getAllPendingJobs().size() == 0) {

            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setPeriodic(SYNC_FREQUENCY)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            jobScheduler.schedule(jobInfo);

            preferences.edit().putBoolean(IS_JOB_FIRST_RUN, false).apply();
        }

        Log.d("UpdateService", String.valueOf(jobScheduler.getAllPendingJobs().size()));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //FAB Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If on My Portfolio Fragment
                if(mViewPager.getCurrentItem()==0)
                {
                    //Call Add Portfolio Activity
                    Intent intent = new Intent(ActivityMain.this, ActivityAddPortfolio.class);
                    startActivityForResult(intent, 8888);
                }
                //If on My Alerts Fragment
                else if(mViewPager.getCurrentItem()==1) {
                    //Call Add Alert Activity
                    Intent intent = new Intent(ActivityMain.this, ActivityAddAlert.class);
                    startActivityForResult(intent,1234);
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //From Add Portfolio Activity back to Main Activity
        if (requestCode == 8888 && resultCode == RESULT_OK)
        {
            //Get instances of views
            ListView outer = findViewById(R.id.stocklistview);
            DB db = new DB(outer.getContext());
            TextView summary = findViewById(R.id.stocksummary);

            //Set Adapter
            StocklistAdapter myAdapter=new StocklistAdapter(outer.getContext(),R.layout.stocklist,db.getAllStocksPortfolio());
            String summaryText = getResources().getString(R.string.summary)
                    + String.format(Locale.getDefault(),"%d", myAdapter.getPortfolioSummary());
            summary.setText(summaryText);

            outer.setAdapter(myAdapter);
            db.close();

        }

        //From Add Alert Activity back to Main Activity
        if (requestCode == 1234 && resultCode == RESULT_OK)
        {
            //Get instances of views
            ListView outer = findViewById(R.id.alertlistview);
            DB db = new DB(outer.getContext());

            //Set Adapter
            AlertlistAdapter myAdapter=new AlertlistAdapter(outer.getContext(),R.layout.alertlist,db.getAllStocksAlerts());

            outer.setAdapter(myAdapter);
            db.close();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return FragmentMyPortfolio.newInstance(position + 1);
            //CHANGE_HERE

            else
                return FragmentMyAlert.newInstance(position+1);

        }

        @Override
        public int getCount() {
            // Show 2 total fragments.
            //CHANGE_HERE
            return 1;
        }
    }
}
