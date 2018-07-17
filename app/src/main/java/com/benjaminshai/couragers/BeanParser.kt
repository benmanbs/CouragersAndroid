package com.benjaminshai.couragers

import com.benjaminshai.couragers.beans.Day
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.util.ArrayList

/**
 * Created by bshai on 8/14/15.
 */
class BeanParser {

    fun parse(JSON: String): ArrayList<Day>? {
        val listType = object : TypeToken<ArrayList<Day>>() {

        }.type
        return Gson().fromJson<ArrayList<Day>>(JSON, listType)
    }
}
