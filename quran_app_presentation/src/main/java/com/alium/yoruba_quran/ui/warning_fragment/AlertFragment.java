package com.alium.yoruba_quran.ui.warning_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.ui.main.MainActivity;

import butterknife.ButterKnife;

/**
 * Created by abdulmujibaliu on 3/16/17.
 */

public class AlertFragment extends DialogFragment {


    private Activity mContext;
    private OnButtonClickListener buttonClickListener;
    private TextView mTitleText, mDescriptionText;
    private TextView mOKayButton;
    private ImageView mBackImage;
    private String tag, desc, title;


    public void setButtonClickListener(OnButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.data_warning_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButterKnife.bind(getActivity());
        mContext = getActivity();


        mTitleText = (TextView) dialog.findViewById(R.id.title);
        mDescriptionText = (TextView) dialog.findViewById(R.id.message_content);
        mOKayButton = (TextView) dialog.findViewById(R.id.ok_button);
        mBackImage = (ImageView) dialog.findViewById(R.id.back_ground_dialog);


        mOKayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (buttonClickListener != null) {
                    buttonClickListener.onButtonClicked(tag);
                }
            }
        });
        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();

        tag = args.getString(TAG_STRING);
        desc = args.getString(DESC_STRING);
        title = args.getString(TITLE_STRING);

        mTitleText.setText(title);
        mDescriptionText.setText(desc);
    }

    public static String TITLE_STRING = "TITLE_STRING";
    public static String DESC_STRING = "DESC_STRING";
    public static String TAG_STRING = "TAG_STRING";

    public interface OnButtonClickListener {
        void onButtonClicked(String tag);
    }

    public static AlertFragment newInstance(String title, String description, String tag) {
        Bundle args = new Bundle();
        args.putString(TITLE_STRING, title);
        args.putString(DESC_STRING, description);
        args.putString(TAG_STRING, tag);
        AlertFragment fragment = new AlertFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

