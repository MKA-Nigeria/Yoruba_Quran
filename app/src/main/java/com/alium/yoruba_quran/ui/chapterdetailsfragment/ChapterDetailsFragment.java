package com.alium.yoruba_quran.ui.chapterdetailsfragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.app.Constants;
import com.alium.yoruba_quran.data.Chapter;
import com.alium.yoruba_quran.ui.main.MainActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lucas on 02/01/2017.
 */

public class ChapterDetailsFragment extends Fragment {

    private static final String KEY_PERSON = "key_person";

    @BindView(R.id.fragment_person_details__toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_person_details__description)
    TextView description;
    @BindView(R.id.pdfView)
    PDFView mDPdfView;
    @BindView(R.id.render_progress)
    ProgressBar mProgressView;


    private Chapter chapter;
    private View mView;

    public static ChapterDetailsFragment newInstance(Chapter chapter) {
        ChapterDetailsFragment fragment = new ChapterDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PERSON, chapter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mView = view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.chapter = getArguments().getParcelable(KEY_PERSON);

        setupToolbar();
        setChapter(chapter);


    }

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.person_details);

        if (!((MainActivity) getActivity()).getContainersLayout().hasTwoColumns()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_left_grey600_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

    }


    private void setChapter(Chapter chapter) {
        toolbar.setTitle(chapter.getName());
        description.setText(chapter.getDescription());
        final String fileURl = Environment.getExternalStorageDirectory() + Constants.FOLDER_NAME + "/" + chapter.getIndexID() + "YOR.PDF";
        File file = new File(fileURl);
        //Uri uri = Uri.parse(fileURl);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileURl);
                mDPdfView.fromFile(file).onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        mProgressView.setVisibility(View.GONE);
                    }
                }).load();

                mDPdfView.useBestQuality(false);
                mDPdfView.enableAnnotationRendering(true);
                mDPdfView.enableSwipe(true);
                mDPdfView.setSwipeVertical(true);
                mDPdfView.setHorizontalScrollBarEnabled(true);

            }
        }, 500);
    }

    //Attempt to load PDF Aynscronously in order to remove frame skipping while loading larger files, suratul baqarah for example!
    //TODO: Switch to newer version of PDF reader that takes bytes or make this version take bytes for the fun of it!
    //Apparently PDF reader already does decoding ASYNC .. Fix =, use handle to delay rendering and not let user notice the stutter during transition

    private class LoadPDFAsync extends AsyncTask<String, Void, File> {
        File file;

        @Override
        protected File doInBackground(String... params) {
            file = new File(params[0]);
            return file;
        }


    }
}
