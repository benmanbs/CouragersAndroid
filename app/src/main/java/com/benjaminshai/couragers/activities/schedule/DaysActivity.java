package com.benjaminshai.couragers.activities.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.benjaminshai.couragers.MyResultReceiver;
import com.benjaminshai.couragers.ResponseStatus;
import com.benjaminshai.couragers.activities.ActivityWithToolbar;
import com.benjaminshai.couragers.services.DayService;
import com.benjaminshai.couragers.views.FontTextView;
import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.beans.Day;

import java.util.ArrayList;
import java.util.List;

public class DaysActivity extends ActivityWithToolbar implements SwipeRefreshLayout.OnRefreshListener, MyResultReceiver.Receiver{

    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DayService.DaysSingleton daySingleton = DayService.DaysSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        attachToolbar();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) findViewById(R.id.mainList);

        Intent intent = getIntent();
        final List<Day> days;
        if (daySingleton.getDays().isEmpty()) {
            days = intent.getParcelableArrayListExtra("days");
            daySingleton.setDays(days);
        } else {
            days = daySingleton.getDays();
        }


        DayAdapter adapter = new DayAdapter(this,
                R.layout.day_row, days);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(DaysActivity.this, EventActivity.class);
                i.putExtra("day", days.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    public void onRefresh() {
        MyResultReceiver mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DayService.class);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("command", "query");
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ResponseStatus.RUNNING:
                break;
            case ResponseStatus.FINISHED:
                ArrayList<Day> days =  resultData.getParcelableArrayList("results");
                this.daySingleton.setDays(days);
                this.recreate();
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case ResponseStatus.ERROR:
                break;

        }
    }

    private class DayAdapter extends ArrayAdapter<Day> {

        private List<Day> days;

        public DayAdapter(Context context, int resource, List<Day> days) {
            super(context, resource, days);
            this.days = days;
        }


        @Override
        public View getView(int position, View view, ViewGroup group) {
            View v = view;

            Day day = days.get(position);

            if(v == null){

                LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v=inflater.inflate(R.layout.day_row,null);

            }

            FontTextView dayView = (FontTextView) v.findViewById(R.id.list_day_textview);

            if (dayView != null) {
                dayView.setText(day.getDisplayName());
                dayView.setTextColor(Color.rgb(day.getTextRed(), day.getTextGreen(), day.getTextBlue()));
            }

            return v;
        }
    }
}
