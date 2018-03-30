package com.alium.yoruba_quran.ui.chapterdetailsfragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.ui.PresentationConstants;
import com.alium.yoruba_quran.ui.main.MainActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aliumujib on 02/01/2017.
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


    private ChapterEntity chapter;
    private View mView;

    public static ChapterDetailsFragment newInstance(ChapterEntity chapter) {
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


    private void setChapter(ChapterEntity chapter) {
        toolbar.setTitle(chapter.getName());
        description.setText(chapter.getDescription());
        final String fileURl = Environment.getExternalStorageDirectory() + PresentationConstants.TEMP_FOLDER_NAME + PresentationConstants.FOLDER_NAME + "/" + chapter.getIndexID() + "YOR.PDF";
        File file = new File(fileURl);
        //Uri uri = Uri.parse(fileURl);
        mDPdfView.fromFile(file)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .pageFitPolicy(FitPolicy.WIDTH)
                .onLoad(new LoadCompleteListener())
                .load();

    }


    class LoadCompleteListener implements OnLoadCompleteListener {

        @Override
        public void loadComplete(int nbPages) {
            mProgressView.setVisibility(View.GONE);
        }
    }

}
