package com.benjaminshai.couragers;

import com.benjaminshai.couragers.beans.Day;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bshai on 8/14/15.
 */
public class BeanParser {

    public ArrayList<Day> parse(String JSON) {
        Type listType = new TypeToken<ArrayList<Day>>() {}.getType();
        ArrayList<Day> days = new Gson().fromJson(JSON, listType);
        return days;
    }
}
