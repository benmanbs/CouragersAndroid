package com.benjaminshai.couragers.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

import com.benjaminshai.couragers.R

/**
 * Created by User on 8/11/2014.
 */
class FontTextView : TextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)

    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView)
            val fontName = a.getString(R.styleable.FontTextView_fontName)
            if (fontName != null) {
                val myTypeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
                typeface = myTypeface
            }
            a.recycle()
        }
    }

}
