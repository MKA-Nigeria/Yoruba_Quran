package com.lucasurbas.masterdetail.ui.warning_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucasurbas.masterdetail.R;
import com.lucasurbas.masterdetail.ui.main.MainActivity;

import butterknife.ButterKnife;

/**
 * Created by abdulmujibaliu on 3/16/17.
 */

public class WarningFragment extends DialogFragment {


    private Activity mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.data_warning_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButterKnife.bind(getActivity());
        mContext = getActivity();

        TextView mTitleText = (TextView) dialog.findViewById(R.id.title);
        TextView mDescriptionText = (TextView) dialog.findViewById(R.id.message_content);
        TextView mOKayButton = (TextView) dialog.findViewById(R.id.ok_button);
        ImageView mBackImage = (ImageView) dialog.findViewById(R.id.back_ground_dialog);

        mTitleText.setText("Large Download");
        mDescriptionText.setText("This app requires that you download some data files, it is recoomended that you continue on an un-metered wifi connection");
        mOKayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return dialog;
    }

    public static WarningFragment getInstance(MainActivity mainActivity) {
        return new WarningFragment();
    }
}

