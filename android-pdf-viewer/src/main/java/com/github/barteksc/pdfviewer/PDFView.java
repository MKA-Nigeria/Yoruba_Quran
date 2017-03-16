/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.exception.FileNotFoundException;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.model.PagePart;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.ArrayUtils;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.MathUtils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * It supports animations, zoom, cache, and swipe.
 * <p>
 * To fully understand this class you must know its principles :
 * - The PDF document is seen as if we always want to draw all the pages.
 * - The thing is that we only draw the visible parts.
 * - All parts are the same size, this is because we can't interrupt a native page rendering,
 * so we need these renderings to be as fast as possible, and be able to interrupt them
 * as soon as we can.
 * - The parts are loaded when the current offset or the current zoom level changes
 * <p>
 * Important :
 * - DocumentPage = A page of the PDF document.
 * - UserPage = A page as defined by the user.
 * By default, they're the same. But the user can change the pages order
 * using {@link #load(String, boolean, String, OnLoadCompleteListener, OnErrorListener, int[])}. In this
 * particular case, a userPage of 5 can refer to a documentPage of 17.
 */
public class PDFView extends RelativeLayout {

    private static final String TAG = PDFView.class.getSimpleName();

    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;

    private float minZoom = DEFAULT_MIN_SCALE;
    private float midZoom = DEFAULT_MID_SCALE;
    private float maxZoom = DEFAULT_MAX_SCALE;

    /**
     * START - scrolling in first page direction
     * END - scrolling in last page direction
     * NONE - not scrolling
     */
    enum ScrollDir {
        NONE, START, END
    }

    private ScrollDir scrollDir = ScrollDir.NONE;

    /**
     * Rendered parts go to the cache manager
     */
    CacheManager cacheManager;

    /**
     * Animation manager manage all offset and zoom animation
     */
    private AnimationManager animationManager;

    /**
     * Drag manager manage all touch events
     */
    private DragPinchManager dragPinchManager;

    /**
     * The pages the user want to display in order
     * (ex: 0, 2, 2, 8, 8, 1, 1, 1)
     */
    private int[] originalUserPages;

    /**
     * The same pages but with a filter to avoid repetition
     * (ex: 0, 2, 8, 1)
     */
    private int[] filteredUserPages;

    /**
     * The same pages but with a filter to avoid repetition
     * (ex: 0, 1, 1, 2, 2, 3, 3, 3)
     */
    private int[] filteredUserPageIndexes;

    /**
     * Number of pages in the loaded PDF document
     */
    private int documentPageCount;

    /**
     * The index of the current sequence
     */
    private int currentPage;

    /**
     * The index of the current sequence
     */
    private int currentFilteredPage;

    /**
     * 页面的实际大小
     */
    private Pair<Integer, Integer> pageDynamicSizes[]; // 每页的尺寸: index page -> 尺寸

    /**
     * 显示优化过后的大小
     */
    private float optimalPageFixSize; // swipeVertical为true表示宽度，false表示高度
    private float optimalPageDynamicSizes[]; // 每页的尺寸: index page -> 尺寸。 swipeVertical为true表示高度，false表示宽度

    /**
     * If you picture all the pages side by side in their optimal width,
     * and taking into account the zoom level, the current offset is the
     * position of the left border of the screen in this big picture
     */
    private float currentXOffset = 0;

    /**
     * If you picture all the pages side by side in their optimal width,
     * and taking into account the zoom level, the current offset is the
     * position of the left border of the screen in this big picture
     */
    private float currentYOffset = 0;

    /**
     * The zoom level, always >= 1
     */
    private float zoom = 1f;

    /**
     * True if the PDFView has been recycled
     */
    private boolean recycled = true;

    /**
     * Current state of the view
     */
    private State state = State.DEFAULT;

    /**
     * Async task used during the loading phase to decode a PDF document
     */
    private DecodingAsyncTask decodingAsyncTask;

    /**
     * Async task always playing in the background and proceeding rendering tasks
     */
    RenderingAsyncTask renderingAsyncTask;

    private PagesLoader pagesLoader;

    /**
     * Call back object to call when the PDF is loaded
     */
    private OnLoadCompleteListener onLoadCompleteListener;

    private OnErrorListener onErrorListener;

    /**
     * Call back object to call when the page has changed
     */
    private OnPageChangeListener onPageChangeListener;

    /**
     * Call back object to call when the page is scrolled
     */
    private OnPageScrollListener onPageScrollListener;

    /**
     * Call back object to call when the above layer is to drawn
     */
    private OnDrawListener onDrawListener;

    /**
     * Paint object for drawing
     */
    private Paint paint;

    /**
     * Paint object for drawing debug stuff
     */
    private Paint debugPaint;

    private int defaultPage = 0;

    /**
     * True if should scroll through pages vertically instead of horizontally
     */
    private boolean swipeVertical = true;

    /**
     * Pdfium core for loading and rendering PDFs
     */
    private PdfiumCore pdfiumCore;

    private PdfDocument pdfDocument;

    private ScrollHandle scrollHandle;

    private boolean isScrollHandleInit = false;

    ScrollHandle getScrollHandle() {
        return scrollHandle;
    }

    /**
     * True if bitmap should use ARGB_8888 format and take more memory
     * False if bitmap should be compressed by using RGB_565 format and take less memory
     */
    private boolean bestQuality = false;

    /**
     * True if annotations should be rendered
     * False otherwise
     */
    private boolean annotationRendering = false;

    /**
     * Construct the initial view
     */
    public PDFView(Context context, AttributeSet set) {
        super(context, set);

        if (isInEditMode()) {
            return;
        }

        cacheManager = new CacheManager();
        animationManager = new AnimationManager(this);
        dragPinchManager = new DragPinchManager(this, animationManager);

        paint = new Paint();
        debugPaint = new Paint();
        debugPaint.setStyle(Style.STROKE);

        pdfiumCore = new PdfiumCore(context);
        setWillNotDraw(false);
    }

    private void load(String path, boolean isAsset, String password, OnLoadCompleteListener listener, OnErrorListener onErrorListener) {
        load(path, isAsset, password, listener, onErrorListener, null);
    }

    private void load(String path, boolean isAsset, String password, OnLoadCompleteListener onLoadCompleteListener, OnErrorListener onErrorListener, int[] userPages) {

        if (!recycled) {
            throw new IllegalStateException("Don't call load on a PDF View without recycling it first.");
        }

        // Manage UserPages if not null
        if (userPages != null) {
            this.originalUserPages = userPages;
            this.filteredUserPages = ArrayUtils.deleteDuplicatedPages(originalUserPages);
            this.filteredUserPageIndexes = ArrayUtils.calculateIndexesInDuplicateArray(originalUserPages);
        }

        this.onLoadCompleteListener = onLoadCompleteListener;
        this.onErrorListener = onErrorListener;

        recycled = false;
        // Start decoding document
        decodingAsyncTask = new DecodingAsyncTask(path, isAsset, password, this, pdfiumCore);
        decodingAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // New: get actual width
    int getPageActualWidth(int pageNumber) {
        if (pageNumber >= 0 && pageDynamicSizes != null && pageNumber < pageDynamicSizes.length) {
            return pageDynamicSizes[pageNumber].first;
        } else {
            return 0;
        }
    }

    // New: get actual height
    int getPageActualHeight(int pageNumber) {
        if (pageNumber >= 0 && pageDynamicSizes != null && pageNumber < pageDynamicSizes.length) {
            return pageDynamicSizes[pageNumber].second;
        } else {
            return 0;
        }
    }

    // New: calc page offset
    float getOptimalPageOffset(int pageNumber) {
        float offset = 0;
        if (optimalPageDynamicSizes != null) {
            for (int index = 0; index < optimalPageDynamicSizes.length && index < pageNumber; index++) {
                offset += optimalPageDynamicSizes[index];
            }
        }
        return offset;
    }

    float getOptimalPageTotalOffset() {
        return getOptimalPageOffset(getPageCount());
    }

    // New: get page size(width or height)
    float getOptimalPageWidth(int pageNumber) {
        if (swipeVertical) {
            return optimalPageFixSize;
        } else {
            if (pageNumber >= 0 && optimalPageDynamicSizes != null && pageNumber < optimalPageDynamicSizes.length) {
                return optimalPageDynamicSizes[pageNumber];
            } else {
                return 0;
            }
        }
    }

    float getOptimalPageHeight(int pageNumber) {
        if (swipeVertical) {
            if (pageNumber >= 0 && optimalPageDynamicSizes != null && pageNumber < optimalPageDynamicSizes.length) {
                return optimalPageDynamicSizes[pageNumber];
            } else {
                return 0;
            }
        } else {
            return optimalPageFixSize;
        }
    }

    int getPageNumberOfOffset(float offset) {
        int page = 0;
        float totalOffset = 0;
        for (int index = 0; index < optimalPageDynamicSizes.length; index++) {
            page = index;
            totalOffset += optimalPageDynamicSizes[index];
            if (totalOffset > offset) {
                break;
            }
        }
        return page;
    }

    /**
     * Go to the given page.
     *
     * @param page Page index.
     */
    public void jumpTo(int page, boolean withAnimation) {
        if (swipeVertical) {
            float toY = -getOptimalPageOffset(page);
            if (withAnimation) {
                animationManager.startYAnimation(currentYOffset, toY);
            } else {
                moveTo(currentXOffset, toY);
            }
        } else {
            float toX = -getOptimalPageOffset(page);
            if (withAnimation) {
                animationManager.startXAnimation(currentXOffset, toX);
            } else {
                moveTo(toX, currentYOffset);
            }
        }
        showPage(page);
    }

    public void jumpTo(int page) {
        jumpTo(page, false);
    }

    void showPage(int pageNumber) {
        if (recycled) {
            return;
        }
        state = State.SHOWN;

        // Check the page number and makes the
        // difference between UserPages and DocumentPages
        pageNumber = determineValidPageNumberFrom(pageNumber);
        currentPage = pageNumber;
        currentFilteredPage = pageNumber;
        if (filteredUserPageIndexes != null) {
            if (pageNumber >= 0 && pageNumber < filteredUserPageIndexes.length) {
                pageNumber = filteredUserPageIndexes[pageNumber];
                currentFilteredPage = pageNumber;
            }
        }

        loadPages();

        if (scrollHandle != null && !documentFitsView()) {
            scrollHandle.setPageNum(currentPage + 1);
        }

        if (onPageChangeListener != null) {
            onPageChangeListener.onPageChanged(currentPage, getPageCount());
        }
    }

    /**
     * Get current position as ratio of document length to visible area.
     * 0 means that document start is visible, 1 that document end is visible
     *
     * @return offset between 0 and 1
     */
    public float getPositionOffset() {
        float offset;
        if (swipeVertical) {
            offset = -currentYOffset / toCurrentScale(getOptimalPageTotalOffset() - getHeight());
        } else {
            offset = -currentXOffset / toCurrentScale(getOptimalPageTotalOffset() - getWidth());
        }
        return MathUtils.limit(offset, 0, 1);
    }

    /**
     * @param progress   must be between 0 and 1
     * @param moveHandle whether to move scroll handle
     * @see PDFView#getPositionOffset()
     */
    public void setPositionOffset(float progress, boolean moveHandle) {
        if (swipeVertical) {
            moveTo(currentXOffset, (-toCurrentScale(getOptimalPageTotalOffset()) + getHeight()) * progress, moveHandle);
        } else {
            moveTo((-toCurrentScale(getOptimalPageTotalOffset()) + getWidth()) * progress, currentYOffset, moveHandle);
        }
        loadPageByOffset();
    }

    public void setPositionOffset(float progress) {
        setPositionOffset(progress, true);
    }

    public void stopFling() {
        animationManager.stopFling();
    }

    public int getPageCount() {
        if (originalUserPages != null) {
            return originalUserPages.length;
        }
        return documentPageCount;
    }

    public void enableSwipe(boolean enableSwipe) {
        dragPinchManager.setSwipeEnabled(enableSwipe);
    }

    public void enableDoubletap(boolean enableDoubletap) {
        this.dragPinchManager.enableDoubletap(enableDoubletap);
    }

    private void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    OnPageChangeListener getOnPageChangeListener() {
        return this.onPageChangeListener;
    }

    private void setOnPageScrollListener(OnPageScrollListener onPageScrollListener) {
        this.onPageScrollListener = onPageScrollListener;
    }

    OnPageScrollListener getOnPageScrollListener() {
        return this.onPageScrollListener;
    }

    private void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    public void recycle() {

        animationManager.stopAll();

        // Stop tasks
        if (renderingAsyncTask != null) {
            renderingAsyncTask.cancel(true);
        }
        if (decodingAsyncTask != null) {
            decodingAsyncTask.cancel(true);
        }

        // Clear caches
        cacheManager.recycle();

        if (scrollHandle != null && isScrollHandleInit) {
            scrollHandle.destroyLayout();
        }

        if (pdfiumCore != null && pdfDocument != null) {
            pdfiumCore.closeDocument(pdfDocument);
        }

        originalUserPages = null;
        filteredUserPages = null;
        filteredUserPageIndexes = null;
        pdfDocument = null;
        scrollHandle = null;
        isScrollHandleInit = false;
        currentXOffset = currentYOffset = 0;
        zoom = 1f;
        recycled = true;
        state = State.DEFAULT;

        pageDynamicSizes = null;
        optimalPageDynamicSizes = null;
    }

    public boolean isRecycled() {
        return recycled;
    }

    @Override
    protected void onDetachedFromWindow() {
        recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (isInEditMode()) {
            return;
        }
        animationManager.stopAll();
        calculateOptimalWidthAndHeight();
        loadPages();
        if (swipeVertical)
            moveTo(currentXOffset, calculateCenterOffsetForPage(currentFilteredPage));
        else
            moveTo(calculateCenterOffsetForPage(currentFilteredPage), currentYOffset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        // As I said in this class javadoc, we can think of this canvas as a huge
        // strip on which we draw all the images. We actually only draw the rendered
        // parts, of course, but we render them in the place they belong in this huge
        // strip.

        // That's where Canvas.translate(x, y) becomes very helpful.
        // This is the situation :
        //  _______________________________________________
        // |   			 |					 			   |
        // | the actual  |					The big strip  |
        // |	canvas	 | 								   |
        // |_____________|								   |
        // |_______________________________________________|
        //
        // If the rendered part is on the bottom right corner of the strip
        // we can draw it but we won't see it because the canvas is not big enough.

        // But if we call translate(-X, -Y) on the canvas just before drawing the object :
        //  _______________________________________________
        // |   			  					  _____________|
        // |   The big strip     			 |			   |
        // |		    					 |	the actual |
        // |								 |	canvas	   |
        // |_________________________________|_____________|
        //
        // The object will be on the canvas.
        // This technique is massively used in this method, and allows
        // abstraction of the screen position when rendering the parts.

        // Draws background
        canvas.drawColor(Color.WHITE);

        if (recycled) {
            return;
        }

        if (state != State.SHOWN) {
            return;
        }

        // Moves the canvas before drawing any element
        float currentXOffset = this.currentXOffset;
        float currentYOffset = this.currentYOffset;
        canvas.translate(currentXOffset, currentYOffset);

        // Draws thumbnails
        for (PagePart part : cacheManager.getThumbnails()) {
            drawPart(canvas, part);
        }

        // Draws parts
        for (PagePart part : cacheManager.getPageParts()) {
            drawPart(canvas, part);
        }

        // Draws the user layer
        /*
        if (onDrawListener != null) {
            canvas.translate(toCurrentScale(currentFilteredPage * optimalPageWidth), 0);

            onDrawListener.onLayerDrawn(canvas, //
                    toCurrentScale(optimalPageWidth), //
                    toCurrentScale(optimalPageHeight),
                    currentPage);

            canvas.translate(-toCurrentScale(currentFilteredPage * optimalPageWidth), 0);
        }
        */

        // Restores the canvas position
        canvas.translate(-currentXOffset, -currentYOffset);
    }

    /**
     * Draw a given PagePart on the canvas
     */
    private void drawPart(Canvas canvas, PagePart part) {
        // Can seem strange, but avoid lot of calls
        RectF pageRelativeBounds = part.getPageRelativeBounds();
        Bitmap renderedBitmap = part.getRenderedBitmap();

        if (renderedBitmap.isRecycled()) {
            return;
        }

        // Move to the target page
        float localTranslationX = 0;
        float localTranslationY = 0;
        if (swipeVertical) {
            localTranslationY = toCurrentScale(getOptimalPageOffset(part.getUserPage()));
        } else {
            localTranslationX = toCurrentScale(getOptimalPageOffset(part.getUserPage()));
        }
        canvas.translate(localTranslationX, localTranslationY);

        Rect srcRect = new Rect(0, 0, renderedBitmap.getWidth(), renderedBitmap.getHeight());

        float offsetX = toCurrentScale(pageRelativeBounds.left * getOptimalPageWidth(part.getUserPage()));
        float offsetY = toCurrentScale(pageRelativeBounds.top * getOptimalPageHeight(part.getUserPage()));
        float width = toCurrentScale(pageRelativeBounds.width() * getOptimalPageWidth(part.getUserPage()));
        float height = toCurrentScale(pageRelativeBounds.height() * getOptimalPageHeight(part.getUserPage()));

        // If we use float values for this rectangle, there will be
        // a possible gap between page parts, especially when
        // the zoom level is high.
        RectF dstRect = new RectF((int) offsetX, (int) offsetY,
                (int) (offsetX + width),
                (int) (offsetY + height));

        // Check if bitmap is in the screen
        float translationX = currentXOffset + localTranslationX;
        float translationY = currentYOffset + localTranslationY;
        if (translationX + dstRect.left >= getWidth()
                || translationX + dstRect.right <= 0
                || translationY + dstRect.top >= getHeight()
                || translationY + dstRect.bottom <= 0) {
            canvas.translate(-localTranslationX, -localTranslationY);
            return;
        }

        canvas.drawBitmap(renderedBitmap, srcRect, dstRect, paint);

        if (Constants.DEBUG_MODE) {
            debugPaint.setColor(part.getUserPage() % 2 == 0 ? Color.RED : Color.BLUE);
            canvas.drawRect(dstRect, debugPaint);

            debugPaint.setTextSize(dstRect.height() / 5);
            canvas.drawText(Integer.toString(part.getUserPage()),
                    dstRect.left + 5, dstRect.bottom - 5,
                    debugPaint);
        }

        // Restore the canvas position
        canvas.translate(-localTranslationX, -localTranslationY);
    }

    /**
     * Load all the parts around the center of the screen,
     * taking into account X and Y offsets, zoom level, and
     * the current page displayed
     */
    public void loadPages() {
        if (optimalPageFixSize == 0) {
            return;
        }

        // Cancel all current tasks
        renderingAsyncTask.removeAllTasks();
        cacheManager.makeANewSet();

        pagesLoader.loadPages();
        redraw();
    }

    /**
     * Called when the PDF is loaded
     */
    public void loadComplete(PdfDocument pdfDocument) {
        this.state = State.LOADED;
        this.documentPageCount = pdfiumCore.getPageCount(pdfDocument);
        this.pdfDocument = pdfDocument;

        // 计算PDF的实际大小
        pageDynamicSizes = new Pair[this.documentPageCount];
        for (int pageNumber = 0; pageNumber < this.documentPageCount; pageNumber++) {
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int pageWidth = pdfiumCore.getPageWidth(pdfDocument, pageNumber);
            int pageHeight = pdfiumCore.getPageHeight(pdfDocument, pageNumber);
            pageDynamicSizes[pageNumber] = new Pair<>(pageWidth, pageHeight);
        }

        calculateOptimalWidthAndHeight();

        pagesLoader = new PagesLoader(this);

        renderingAsyncTask = new RenderingAsyncTask(this, pdfiumCore, pdfDocument);
        renderingAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (scrollHandle != null) {
            scrollHandle.setupLayout(this);
            isScrollHandleInit = true;
        }

        // Notify the listener
        jumpTo(defaultPage, false);
        if (onLoadCompleteListener != null) {
            onLoadCompleteListener.loadComplete(documentPageCount);
        }
    }

    public void loadError(Throwable t) {
        state = State.ERROR;
        recycle();
        invalidate();
        if (this.onErrorListener != null) {
            this.onErrorListener.onError(t);
        } else {
            Log.e(TAG, "load pdf error", t);
        }
    }

    void redraw() {
        invalidate();
    }

    /**
     * Called when a rendering task is over and
     * a PagePart has been freshly created.
     *
     * @param part The created PagePart.
     */
    public void onBitmapRendered(PagePart part) {
        if (part.isThumbnail()) {
            cacheManager.cacheThumbnail(part);
        } else {
            cacheManager.cachePart(part);
        }
        redraw();
    }

    /**
     * Given the UserPage number, this method restrict it
     * to be sure it's an existing page. It takes care of
     * using the user defined pages if any.
     *
     * @param userPage A page number.
     * @return A restricted valid page number (example : -2 => 0)
     */
    private int determineValidPageNumberFrom(int userPage) {
        if (userPage <= 0) {
            return 0;
        }
        if (originalUserPages != null) {
            if (userPage >= originalUserPages.length) {
                return originalUserPages.length - 1;
            }
        } else {
            if (userPage >= documentPageCount) {
                return documentPageCount - 1;
            }
        }
        return userPage;
    }

    /**
     * Calculate the x/y-offset needed to have the given
     * page centered on the screen. It doesn't take into
     * account the zoom level.
     *
     * @param pageNumber The page number.
     * @return The x/y-offset to use to have the pageNb centered.
     */
    private float calculateCenterOffsetForPage(int pageNumber) {
        if (swipeVertical) {
            float imageY = -getOptimalPageOffset(pageNumber);
            imageY += getHeight() / 2 - getPageActualHeight(getCurrentPage()) / 2;
            return imageY;
        } else {
            float imageX = -getOptimalPageOffset(pageNumber);
            imageX += getWidth() / 2 - getPageActualHeight(getCurrentPage()) / 2;
            return imageX;
        }
    }

    /**
     * Calculate the optimal width and height of a page
     * considering the area width and height
     */
    private void calculateOptimalWidthAndHeight() {
        if (state == State.DEFAULT || getWidth() == 0) {
            return;
        }

        // 计算每页的大小
        if (pageDynamicSizes != null) {
            optimalPageDynamicSizes = new float[pageDynamicSizes.length];
            if (swipeVertical) {
                // vertical scroll. Get height
                optimalPageFixSize = getWidth();
                for (int pageNumber = 0; pageNumber < pageDynamicSizes.length; pageNumber++) {
                    optimalPageDynamicSizes[pageNumber] = 1.0f * pageDynamicSizes[pageNumber].second * optimalPageFixSize / pageDynamicSizes[pageNumber].first;
                }
            } else {
                // horizontal scroll. Get width
                optimalPageFixSize = getHeight();
                for (int pageNumber = 0; pageNumber < pageDynamicSizes.length; pageNumber++) {
                    optimalPageDynamicSizes[pageNumber] = 1.0f * pageDynamicSizes[pageNumber].first * optimalPageFixSize / pageDynamicSizes[pageNumber].second;
                }
            }
        }
    }

    public void moveTo(float offsetX, float offsetY) {
        moveTo(offsetX, offsetY, true);
    }

    /**
     * Move to the given X and Y offsets, but check them ahead of time
     * to be sure not to go outside the the big strip.
     *
     * @param offsetX    The big strip X offset to use as the left border of the screen.
     * @param offsetY    The big strip Y offset to use as the right border of the screen.
     * @param moveHandle whether to move scroll handle or not
     */
    public void moveTo(float offsetX, float offsetY, boolean moveHandle) {
        if (swipeVertical) {
            // Check X offset
            if (toCurrentScale(optimalPageFixSize) < getWidth()) {
                offsetX = getWidth() / 2 - toCurrentScale(optimalPageFixSize) / 2;
            } else {
                if (offsetX > 0) {
                    offsetX = 0;
                } else if (offsetX + toCurrentScale(optimalPageFixSize) < getWidth()) {
                    offsetX = getWidth() - toCurrentScale(optimalPageFixSize);
                }
            }

            // Check Y offset
            float optimalPageFixTotalHeight = toCurrentScale(getOptimalPageTotalOffset());
            if (optimalPageFixTotalHeight < getHeight()) { // whole document height visible on screen
                offsetY = 0;
            } else {
                if (offsetY > 0) { // top visible
                    offsetY = 0;
                } else if (offsetY + optimalPageFixTotalHeight < getHeight()) { // bottom visible
                    offsetY = -optimalPageFixTotalHeight + getHeight();
                }
            }

            if (offsetY < currentYOffset) {
                scrollDir = ScrollDir.END;
            } else if (offsetY > currentYOffset) {
                scrollDir = ScrollDir.START;
            } else {
                scrollDir = ScrollDir.NONE;
            }
        } else {
            // Check Y offset
            if (toCurrentScale(optimalPageFixSize) < getHeight()) {
                offsetY = 0;
            } else {
                if (offsetY > 0) {
                    offsetY = 0;
                } else if (offsetY + toCurrentScale(optimalPageFixSize) < getHeight()) {
                    offsetY = getHeight() - toCurrentScale(optimalPageFixSize);
                }
            }

            // Check X offset
            float optimalPageFixTotalWidth = toCurrentScale(getOptimalPageTotalOffset());
            if (optimalPageFixTotalWidth < getWidth()) { // whole document width visible on screen
                offsetX = (getWidth() - optimalPageFixTotalWidth) / 2;
            } else {
                if (offsetX > 0) { // left visible
                    offsetX = 0;
                } else if (offsetX + optimalPageFixTotalWidth < getWidth()) { // right visible
                    offsetX = -optimalPageFixTotalWidth + getWidth();
                }
            }

            if (offsetX < currentXOffset) {
                scrollDir = ScrollDir.END;
            } else if (offsetX > currentXOffset) {
                scrollDir = ScrollDir.START;
            } else {
                scrollDir = ScrollDir.NONE;
            }
        }

        currentXOffset = offsetX;
        currentYOffset = offsetY;
        float positionOffset = getPositionOffset();

        if (moveHandle && scrollHandle != null && !documentFitsView()) {
            scrollHandle.setScroll(positionOffset);
        }

        if (onPageScrollListener != null) {
            onPageScrollListener.onPageScrolled(getCurrentPage(), positionOffset);
        }

        redraw();
    }

    ScrollDir getScrollDir() {
        return scrollDir;
    }

    void loadPageByOffset() {
        int page = 0;
        float currentOffset = swipeVertical ? currentYOffset : currentXOffset;
        float totalOffset = 0;
        for (int index = 0; index < optimalPageDynamicSizes.length; index++) {
            page = index;
            totalOffset += optimalPageDynamicSizes[index];
            if (totalOffset + currentOffset > 0) {
                break;
            }
        }

        if (page >= 0 && page < getPageCount() && page != getCurrentPage()) {
            showPage(page);
        } else {
            loadPages();
        }
    }

    int[] getFilteredUserPages() {
        return filteredUserPages;
    }

    int getDocumentPageCount() {
        return documentPageCount;
    }

    /**
     * Move relatively to the current position.
     *
     * @param dx The X difference you want to apply.
     * @param dy The Y difference you want to apply.
     * @see #moveTo(float, float)
     */
    public void moveRelativeTo(float dx, float dy) {
        moveTo(currentXOffset + dx, currentYOffset + dy);
    }

    /**
     * Change the zoom level
     */
    public void zoomTo(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Change the zoom level, relatively to a pivot point.
     * It will call moveTo() to make sure the given point stays
     * in the middle of the screen.
     *
     * @param zoom  The zoom level.
     * @param pivot The point on the screen that should stays.
     */
    public void zoomCenteredTo(float zoom, PointF pivot) {
        float dzoom = zoom / this.zoom;
        zoomTo(zoom);
        float baseX = currentXOffset * dzoom;
        float baseY = currentYOffset * dzoom;
        baseX += (pivot.x - pivot.x * dzoom);
        baseY += (pivot.y - pivot.y * dzoom);
        moveTo(baseX, baseY);
    }

    /**
     * @see #zoomCenteredTo(float, PointF)
     */
    public void zoomCenteredRelativeTo(float dzoom, PointF pivot) {
        zoomCenteredTo(zoom * dzoom, pivot);
    }

    /**
     * Checks if whole document can be displayed on screen, doesn't include zoom
     *
     * @return true if whole document can displayed at once, false otherwise
     */
    public boolean documentFitsView() {
        if (swipeVertical) {
            return getOptimalPageTotalOffset() < getHeight();
        } else {
            return getOptimalPageTotalOffset() < getWidth();
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public float getCurrentXOffset() {
        return currentXOffset;
    }

    public float getCurrentYOffset() {
        return currentYOffset;
    }

    public float toRealScale(float size) {
        return size / zoom;
    }

    public float toCurrentScale(float size) {
        return size * zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public boolean isZooming() {
        return zoom != minZoom;
    }

    private void setDefaultPage(int defaultPage) {
        this.defaultPage = defaultPage;
    }

    public void resetZoom() {
        zoomTo(minZoom);
    }

    public void resetZoomWithAnimation() {
        zoomWithAnimation(minZoom);
    }

    public void zoomWithAnimation(float centerX, float centerY, float scale) {
        animationManager.startZoomAnimation(centerX, centerY, zoom, scale);
    }

    public void zoomWithAnimation(float scale) {
        animationManager.startZoomAnimation(getWidth() / 2, getHeight() / 2, zoom, scale);
    }

    private void setScrollHandle(ScrollHandle scrollHandle) {
        this.scrollHandle = scrollHandle;
    }

    /**
     * Get page number at given offset
     *
     * @param positionOffset scroll offset between 0 and 1
     * @return page number at given offset, starting from 0
     */
    public int getPageAtPositionOffset(float positionOffset) {
        int page = 0;
        float currentOffset = positionOffset * getOptimalPageTotalOffset();
        float totalOffset = 0;
        for (int index = 0; index < optimalPageDynamicSizes.length; index++) {
            page = index;
            totalOffset += optimalPageDynamicSizes[index];
            if (totalOffset > currentOffset) {
                break;
            }
        }
        return page;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }

    public float getMidZoom() {
        return midZoom;
    }

    public void setMidZoom(float midZoom) {
        this.midZoom = midZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public void useBestQuality(boolean bestQuality) {
        this.bestQuality = bestQuality;
    }

    public boolean isBestQuality() {
        return bestQuality;
    }

    public boolean isSwipeVertical() {
        return swipeVertical;
    }

    public void setSwipeVertical(boolean swipeVertical) {
        this.swipeVertical = swipeVertical;
    }

    public void enableAnnotationRendering(boolean annotationRendering) {
        this.annotationRendering = annotationRendering;
    }

    public boolean isAnnotationRendering() {
        return annotationRendering;
    }

    public PdfDocument.Meta getDocumentMeta() {
        if (pdfDocument == null) {
            return null;
        }
        return pdfiumCore.getDocumentMeta(pdfDocument);
    }

    public List<PdfDocument.Bookmark> getTableOfContents() {
        if (pdfDocument == null) {
            return new ArrayList<>();
        }
        return pdfiumCore.getTableOfContents(pdfDocument);
    }

    /**
     * Use an asset file as the pdf source
     */
    public Configurator fromAsset(String assetName) {
        InputStream stream = null;
        try {
            stream = getContext().getAssets().open(assetName);
            return new Configurator(assetName, true);
        } catch (IOException e) {
            throw new FileNotFoundException(assetName + " does not exist.", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * Use a file as the pdf source
     */
    public Configurator fromFile(File file) {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " does not exist.");
        }
        return new Configurator(file.getAbsolutePath(), false);
    }

    /**
     * Use Uri as the pdf source, for use with content provider
     */
    public Configurator fromUri(Uri uri) {
        return new Configurator(uri.toString(), false);
    }

    private enum State {DEFAULT, LOADED, SHOWN, ERROR}

    public class Configurator {

        private final String path;

        private final boolean isAsset;

        private int[] pageNumbers = null;

        private boolean enableSwipe = true;

        private boolean enableDoubletap = true;

        private OnDrawListener onDrawListener;

        private OnLoadCompleteListener onLoadCompleteListener;

        private OnErrorListener onErrorListener;

        private OnPageChangeListener onPageChangeListener;

        private OnPageScrollListener onPageScrollListener;

        private int defaultPage = 0;

        private boolean swipeHorizontal = false;

        private boolean annotationRendering = false;

        private String password = null;

        private ScrollHandle scrollHandle = null;

        private Configurator(String path, boolean isAsset) {
            this.path = path;
            this.isAsset = isAsset;
        }

        public Configurator pages(int... pageNumbers) {
            this.pageNumbers = pageNumbers;
            return this;
        }

        public Configurator enableSwipe(boolean enableSwipe) {
            this.enableSwipe = enableSwipe;
            return this;
        }

        public Configurator enableDoubletap(boolean enableDoubletap) {
            this.enableDoubletap = enableDoubletap;
            return this;
        }

        public Configurator enableAnnotationRendering(boolean annotationRendering) {
            this.annotationRendering = annotationRendering;
            return this;
        }

        public Configurator onDraw(OnDrawListener onDrawListener) {
            this.onDrawListener = onDrawListener;
            return this;
        }

        public Configurator onLoad(OnLoadCompleteListener onLoadCompleteListener) {
            this.onLoadCompleteListener = onLoadCompleteListener;
            return this;
        }

        public Configurator onPageScroll(OnPageScrollListener onPageScrollListener) {
            this.onPageScrollListener = onPageScrollListener;
            return this;
        }

        public Configurator onError(OnErrorListener onErrorListener) {
            this.onErrorListener = onErrorListener;
            return this;
        }

        public Configurator onPageChange(OnPageChangeListener onPageChangeListener) {
            this.onPageChangeListener = onPageChangeListener;
            return this;
        }

        public Configurator defaultPage(int defaultPage) {
            this.defaultPage = defaultPage;
            return this;
        }

        public Configurator swipeHorizontal(boolean swipeHorizontal) {
            this.swipeHorizontal = swipeHorizontal;
            return this;
        }

        public Configurator password(String password) {
            this.password = password;
            return this;
        }

        public Configurator scrollHandle(ScrollHandle scrollHandle) {
            this.scrollHandle = scrollHandle;
            return this;
        }

        public void load() {
            PDFView.this.recycle();
            PDFView.this.setOnDrawListener(onDrawListener);
            PDFView.this.setOnPageChangeListener(onPageChangeListener);
            PDFView.this.setOnPageScrollListener(onPageScrollListener);
            PDFView.this.enableSwipe(enableSwipe);
            PDFView.this.enableDoubletap(enableDoubletap);
            PDFView.this.setDefaultPage(defaultPage);
            PDFView.this.setSwipeVertical(!swipeHorizontal);
            PDFView.this.enableAnnotationRendering(annotationRendering);
            PDFView.this.setScrollHandle(scrollHandle);
            PDFView.this.dragPinchManager.setSwipeVertical(swipeVertical);
            if (pageNumbers != null) {
                PDFView.this.load(path, isAsset, password, onLoadCompleteListener, onErrorListener, pageNumbers);
            } else {
                PDFView.this.load(path, isAsset, password, onLoadCompleteListener, onErrorListener);
            }
        }
    }
}
