package es.voghdev.pdfviewpager.library.PDFRecyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import es.voghdev.pdfviewpager.library.R;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.adapter.PDFRVAdapter;

/**
 * Created by abdulmujibaliu on 3/20/17.
 */

public class PDFRecyclerView extends RecyclerView {
    protected Context context;


    public PDFRecyclerView(Context context, String pdfPath) {
        super(context);
        this.context = context;
        init(pdfPath);
    }

    public PDFRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    protected void init(String pdfPath) {
        initAdapter(context, pdfPath);
    }

    protected void initAdapter(Context context, String pdfPath) {
        PDFRVAdapter pdfrvAdapter = new PDFRVAdapter.Builder(context)
                .setPdfPath(pdfPath)
                .setOffScreenSize(2)
                .create();
        setLayoutManager(new LinearLayoutManager(context));
        setAdapter(pdfrvAdapter);
    }

    protected void init(AttributeSet attrs) {
        if (isInEditMode()) {
            setBackgroundResource(R.drawable.flaticon_pdf_dummy);
            return;
        }

        if (attrs != null) {
            TypedArray a;

            a = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager);
            String assetFileName = a.getString(R.styleable.PDFViewPager_assetFileName);

            if (assetFileName != null && assetFileName.length() > 0) {
                initAdapter(context, assetFileName);
            }

            a.recycle();
        }
    }


    /**
     * PDFRecyclerView uses PhotoView, so this bugfix should be added
     * Issue explained in https://github.com/chrisbanes/PhotoView
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
