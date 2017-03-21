package es.voghdev.pdfviewpager.library.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import es.voghdev.pdfviewpager.library.util.EmptyClickListener;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by abdulmujibaliu on 3/21/17.
 */

public class PDFRVAdapter extends BasePDFRVAdapter implements PhotoViewAttacher.OnMatrixChangedListener{
    @Override
    public void onMatrixChanged(RectF rect) {

    }

    private static final float DEFAULT_SCALE = 1f;

    SparseArray<WeakReference<PhotoViewAttacher>> attachers;
    PdfScale scale = new PdfScale();
    View.OnClickListener pageClickListener = new EmptyClickListener();

    public PDFRVAdapter(Context context, String pdfPath) {
        super(context, pdfPath);
        attachers = new SparseArray<>();
    }


    @Override
    public void close() {
        super.close();
        if (attachers != null) {
            attachers.clear();
            attachers = null;
        }
    }

    @SuppressWarnings("NewApi")
    @Override
    public void onBindViewHolder(PdfViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PdfRenderer.Page page = getPDFPage(renderer, position);

        Bitmap bitmap = bitmapContainer.get(position);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        PhotoViewAttacher attacher = new PhotoViewAttacher(holder.iv);
        attacher.setScale(scale.getScale(), scale.getCenterX(), scale.getCenterY(), true);
        attacher.setOnMatrixChangeListener(this);

        attachers.put(position, new WeakReference<PhotoViewAttacher>(attacher));

        holder.iv.setImageBitmap(bitmap);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                pageClickListener.onClick(view);
            }
        });
        attacher.update();
    }

    public static class Builder {
        Context context;
        String pdfPath = "";
        float scale = DEFAULT_SCALE;
        float centerX = 0f, centerY = 0f;
        int offScreenSize = DEFAULT_OFFSCREENSIZE;
        LinearLayoutManager linearLayoutManager;
        float renderQuality = DEFAULT_QUALITY;
        View.OnClickListener pageClickListener = new EmptyClickListener();

        public Builder(Context context) {
            this.context = context;
        }

        public PDFRVAdapter.Builder setScale(float scale) {
            this.scale = scale;
            return this;
        }

        public PDFRVAdapter.Builder setScale(PdfScale scale) {
            this.scale = scale.getScale();
            this.centerX = scale.getCenterX();
            this.centerY = scale.getCenterY();
            return this;
        }

        public PDFRVAdapter.Builder setCenterX(float centerX) {
            this.centerX = centerX;
            return this;
        }

        public PDFRVAdapter.Builder setCenterY(float centerY) {
            this.centerY = centerY;
            return this;
        }

        public PDFRVAdapter.Builder setLayoutManger(LinearLayoutManager llm) {
            this.linearLayoutManager = llm;
            return this;
        }

        public PDFRVAdapter.Builder setRenderQuality(float renderQuality) {
            this.renderQuality = renderQuality;
            return this;
        }

        public PDFRVAdapter.Builder setOffScreenSize(int offScreenSize) {
            this.offScreenSize = offScreenSize;
            return this;
        }

        public PDFRVAdapter.Builder setPdfPath(String path) {
            this.pdfPath = path;
            return this;
        }

        public PDFRVAdapter.Builder setOnPageClickListener(View.OnClickListener listener) {
            if (listener != null) {
                pageClickListener = listener;
            }
            return this;
        }

        public PDFRVAdapter create() {
            PDFRVAdapter adapter = new PDFRVAdapter(context, pdfPath);
            adapter.scale.setScale(scale);
            adapter.scale.setCenterX(centerX);
            adapter.scale.setCenterY(centerY);
            adapter.offScreenSize = offScreenSize;
            adapter.renderQuality = renderQuality;
            adapter.pageClickListener = pageClickListener;
            return adapter;
        }
    }

}
