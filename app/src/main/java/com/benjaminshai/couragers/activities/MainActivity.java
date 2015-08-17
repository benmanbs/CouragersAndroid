package com.benjaminshai.couragers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.benjaminshai.couragers.MyResultReceiver;
import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.ResponseStatus;
import com.benjaminshai.couragers.activities.schedule.DaysActivity;
import com.benjaminshai.couragers.beans.Day;
import com.benjaminshai.couragers.services.DayService;

import java.util.ArrayList;

public class MainActivity extends Activity implements MyResultReceiver.Receiver {

    public MyResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new MyResultReceiver(new Handler());
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
                ArrayList<Day> days = resultData.getParcelableArrayList("results");
                Intent myIntent = new Intent(this, DaysActivity.class);
                myIntent.putParcelableArrayListExtra("days", days); //Optional parameters
                this.startActivity(myIntent);
                break;
            case ResponseStatus.ERROR:
                // if we error, make sure it goes to a page.
                ArrayList<Day> daysEmpty = new ArrayList<Day>();
                Intent intent = new Intent(this, DaysActivity.class);
                intent.putParcelableArrayListExtra("days", daysEmpty); //Optional parameters
                this.startActivity(intent);
                break;

        }
    }
}
