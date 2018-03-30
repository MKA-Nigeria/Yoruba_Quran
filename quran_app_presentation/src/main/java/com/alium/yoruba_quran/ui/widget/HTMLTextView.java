package com.alium.yoruba_quran.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;

/**
 * Created by abdulmujibaliu on 3/16/17.
 */

public class HTMLTextView extends AppCompatTextView {


    public HTMLTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setText(Html.fromHtml(getText().toString()));

    }
}
