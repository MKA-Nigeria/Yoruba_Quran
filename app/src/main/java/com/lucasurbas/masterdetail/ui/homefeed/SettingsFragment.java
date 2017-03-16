package com.lucasurbas.masterdetail.ui.homefeed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucasurbas.masterdetail.R;
import com.lucasurbas.masterdetail.ui.main.MainActivity;
import com.lucasurbas.masterdetail.ui.widget.CustomAppBar;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lucas on 03/01/2017.
 */

public class SettingsFragment extends Fragment {

    @BindView(R.id.about_app_text)
    TextView mAboutAppTextView;

    @BindView(R.id.opensouce_libs_lay)
    LinearLayout mOpensourceLinearLayout;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAboutAppTextView.setMovementMethod(new LinkMovementMethod());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar();
    }

    @OnClick(R.id.opensouce_libs_lay)
    void showLisencesDiag() {
        new EasyLicensesDialogCompat(getContext())
                .setTitle("Licenses")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void setupToolbar() {
        CustomAppBar appBar = ((MainActivity) getActivity()).getCustomAppBar();
        appBar.setTitle(getString(R.string.menu_favorites__settings));
        appBar.setMenuRes(R.menu.homefeed_general, R.menu.homefeed_specific, R.menu.homefeed_merged);
    }
}
