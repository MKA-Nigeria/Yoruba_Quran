package es.voghdev.pdfviewpager.library.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import es.voghdev.pdfviewpager.library.R;

/**
 * Created by abdulmujibaliu on 3/20/17.
 */

public class BasePDFRVAdapter extends RecyclerView.Adapter<BasePDFRVAdapter.PdfViewHolder> {

    protected static final int FIRST_PAGE = 0;
    protected static final float DEFAULT_QUALITY = 2.0f;
    protected static final int DEFAULT_OFFSCREENSIZE = 1;

    String pdfPath;
    Context context;
    PdfRenderer renderer;
    BitmapContainer bitmapContainer;
    LayoutInflater inflater;

    protected float renderQuality;
    protected int offScreenSize;

    public BasePDFRVAdapter(Context context, String pdfPath) {
        this.pdfPath = pdfPath;
        this.context = context;
        this.renderQuality = DEFAULT_QUALITY;
        this.offScreenSize = DEFAULT_OFFSCREENSIZE;

        init();
    }

    @Override
    public PdfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_pdf_page, parent, false);
        return new PdfViewHolder(view);
    }

    @SuppressWarnings("NewApi")
    protected void init() {
        try {
            try {
                renderer = new PdfRenderer(getSeekableFileDescriptor(pdfPath));
                inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                PdfRendererParams[] params = new ProcessPDFAsync().execute(renderer, renderQuality).get();
                bitmapContainer = new SimpleBitmapPool(params);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ProcessPDFAsync extends AsyncTask<Object, Void, PdfRendererParams[]> {
        @SuppressWarnings("NewApi")
        private PdfRendererParams[] extractPdfParamsFromAllPages(PdfRenderer renderer, float renderQuality) {
            PdfRendererParams[] paramsList = new PdfRendererParams[renderer.getPageCount()];
            for (int i = 0; i < renderer.getPageCount(); i++) {
                PdfRenderer.Page samplePage = getPDFPage(renderer, i);
                PdfRendererParams params = new PdfRendererParams();
                params.setRenderQuality(renderQuality);
                params.setOffScreenSize(offScreenSize);
                params.setWidth((int) (samplePage.getWidth() * renderQuality));
                params.setHeight((int) (samplePage.getHeight() * renderQuality));
                paramsList[i] = params;
                samplePage.close();
            }
            return paramsList;
        }

        @Override
        protected PdfRendererParams[] doInBackground(Object... params) {
            return extractPdfParamsFromAllPages((PdfRenderer) params[0], (Float) params[1]);
        }
    }

    @SuppressWarnings("NewApi")
    private PdfRendererParams extractPdfParamsFromFirstPage(PdfRenderer renderer, float renderQuality) {
        PdfRenderer.Page samplePage = getPDFPage(renderer, FIRST_PAGE);
        PdfRendererParams params = new PdfRendererParams();

        params.setRenderQuality(renderQuality);
        params.setOffScreenSize(offScreenSize);
        params.setWidth((int) (samplePage.getWidth() * renderQuality));
        params.setHeight((int) (samplePage.getHeight() * renderQuality));

        samplePage.close();

        return params;
    }

    protected ParcelFileDescriptor getSeekableFileDescriptor(String path) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor;

        File pdfCopy = new File(path);

        if (pdfCopy.exists()) {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY);
            return parcelFileDescriptor;
        }

        if (isAnAsset(path)) {
            pdfCopy = new File(context.getCacheDir(), path);
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY);
        } else {
            URI uri = URI.create(String.format("file://%s", path));
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(uri.toString()), "rw");
        }

        return parcelFileDescriptor;
    }

    private boolean isAnAsset(String path) {
        return !path.startsWith("/");
    }

    @SuppressWarnings("NewApi")
    protected PdfRenderer.Page getPDFPage(PdfRenderer renderer, int position) {
        return renderer.openPage(position);
    }

    @SuppressWarnings("NewApi")
    public void close() {
        releaseAllBitmaps();
        if (renderer != null) {
            renderer.close();
        }
    }

    protected void releaseAllBitmaps() {
        if (bitmapContainer != null) {
            bitmapContainer.clear();
        }
    }

    @SuppressWarnings("NewApi")
    @Override
    public void onBindViewHolder(PdfViewHolder holder, int position) {
        PdfRenderer.Page page = getPDFPage(renderer, position);

        Bitmap bitmap = bitmapContainer.get(position);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        holder.iv.setImageBitmap(bitmap);
    }

    @Override
    @SuppressWarnings("NewApi")
    public int getItemCount() {
        return renderer != null ? renderer.getPageCount() : 0;
    }

    public class PdfViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        PdfViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.imageView);

        }

    }
}
