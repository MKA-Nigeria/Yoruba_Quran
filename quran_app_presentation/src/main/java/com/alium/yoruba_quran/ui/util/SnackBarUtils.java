package com.alium.yoruba_quran.ui.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by aliumujib on 21/03/2018.
 */

public class SnackBarUtils {
    public static final int DEFAULT_LENGTH_LONG_DURATION = 3000;

    public SnackBarUtils() {
    }

    public static void showSimpleSnackbar(View _anchorView, String _title) {
        Snackbar.make(_anchorView, _title, 0).show();
    }

    public static void showActionSnackbar(View _anchorView, String _title, String _actionText, View.OnClickListener _listener) {
        Snackbar.make(_anchorView, _title, 0).setAction(_actionText, _listener).show();
    }

    public static void showActionSnackbar(View _anchorView, String _title, String _actionText, int _actionTextColor, View.OnClickListener _listener) {
        Snackbar.make(_anchorView, _title, 0).setAction(_actionText, _listener).setActionTextColor(_actionTextColor).show();
    }

    public static void showActionSnackbarIndefinite(View _anchorView, String _title, String _action, View.OnClickListener _listener) {
        Snackbar.make(_anchorView, _title, -2).setAction(_action, _listener).show();
    }
}
