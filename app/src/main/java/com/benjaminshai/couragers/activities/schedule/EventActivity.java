package com.benjaminshai.couragers.activities.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.benjaminshai.couragers.activities.ActivityWithToolbar;
import com.benjaminshai.couragers.views.FontTextView;
import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.beans.Day;
import com.benjaminshai.couragers.beans.Event;

import java.util.Arrays;
import java.util.List;

public class EventActivity extends ActivityWithToolbar {

    private EventAdapter eventAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        attachToolbar();

        Intent intent = getIntent();
        Day day = intent.getParcelableExtra("day");

        setBackgroundImage("event_bg_", day.getDayID(), R.id.backgroundView);
        setBackgroundImage("event_header_", day.getDayID(), R.id.headerView);
        setBackgroundImage("event_footer_", day.getDayID(), R.id.footerView);

        listView = (ListView) findViewById(R.id.mainList);

        eventAdapter = new EventAdapter(this, R.layout.event_row, Arrays.asList(day.getEvents()), Color.rgb(day.getTextRed(), day.getTextGreen(), day.getTextBlue()));
        listView.setAdapter(eventAdapter);
        TextView empty = new TextView(this);
        empty.setHeight(400);
        listView.addFooterView(empty);
    }

    private void setBackgroundImage(String drawablePrefix, int id, int viewId) {
        final int resourceId = getResources().getIdentifier(drawablePrefix + id, "drawable",
                getPackageName());
        Drawable image = getResources().getDrawable(resourceId);
        ImageView i = (ImageView)findViewById(viewId);
        i.setImageDrawable(image);
    }

    private class EventAdapter extends ArrayAdapter<Event> {

        private List<Event> events;
        private int color;

        public EventAdapter(Context context, int resource, List<Event> objects, int color) {
            super(context, resource, objects);
            this.events = objects;
            this.color = color;
        }


        @Override
        public View getView(int position, View view, ViewGroup group) {
            View v = view;

            final Event event = events.get(position);

            if(v == null){

                LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v=inflater.inflate(R.layout.event_row, null);

            }

            FontTextView time = (FontTextView) v.findViewById(R.id.list_time_textview);

            if (time != null) {
                time.setText(event.getTime());
                time.setTextColor(color);
            }

            FontTextView title = (FontTextView) v.findViewById(R.id.list_title_textview);

            if (title != null) {
                title.setText(event.getTitle());
                title.setTextColor(color);
            }

            FontTextView detail = (FontTextView) v.findViewById(R.id.list_details_textview);

            if (detail != null) {
                detail.setText(event.getDetails());
                detail.setTextColor(color);
            }

            ImageView mapButton = (ImageView) v.findViewById(R.id.mapImage);

            if (mapButton != null) {
                if (event.getHasMap() == 1) {
                    mapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(EventActivity.this, MapActivity.class);
                            i.putExtra("mapImageUrl", event.getMapURL());
                            startActivity(i);
                        }
                    });
                } else {
                    mapButton.setVisibility(View.GONE);
                }
            }

            return v;
        }
    }
}
