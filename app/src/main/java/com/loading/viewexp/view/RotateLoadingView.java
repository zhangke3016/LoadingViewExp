package com.loading.viewexp.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.loading.viewexp.R;


/**
 * Created by zhangke on 2018/4/8.
 */
public class RotateLoadingView extends View {

    private static final int DEFAULT_WIDTH = 8;
    private static final int DEFAULT_SHADOW_POSITION = 2;
    private static final int DEFAULT_SPEED_OF_DEGREE = 10;

    private Paint mPaint;
    private RectF mLoadingRectF;
    private RectF mShadowRectF;
    //顶部开始弧度
    private int mTopDegree = 10;
    //地步结束弧度
    private int mBottomDegree = 180;
    //弧度
    private float mArc;
    //旋转弧度宽度
    private int mLoadingWidth;
    //弧度切换变量
    private boolean mChangeBigger = true;
    //阴影效果偏移量
    private int mShadowPosition;
    //中间显示图片id
    private int mDrawableId;
    //是否已经开始
    private boolean isStart = false;
    //loading 颜色
    private int mColor;
    //滑动速度
    private int mSpeedOfDegree;
    //每次滑过的弧度
    private float mSpeedOfArc;
    //绘制icon画笔
    private Paint mBitmapPaint;
    //中间loading图片
    private Bitmap mLoadingBitmap;
    //缩放矩阵
    private Matrix mMatrix;
    //icon 缩放系数
    private float mMatrixScale = 0.8f;

    private static final int WAIT_DEGREE = 80;
    public RotateLoadingView(Context context) {
        super(context);
        initView(context, null);
    }

    public RotateLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotateLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setIconDrawable(@DrawableRes int resId){
        this.mDrawableId = resId;
    }

    public void setLoadingColor(int color) {
        this.mColor = color;
    }

    public int getLoadingColor() {
        return mColor;
    }

    public void start() {
        isStart = true;
        invalidate();
    }

    public void stop() {
        isStart = false;
        invalidate();
    }

    public boolean isStart() {
        return isStart;
    }

    private void initView(Context context, AttributeSet attrs) {
        mColor = Color.parseColor("#E8E8E8");
        mLoadingWidth = dpToPx(context, DEFAULT_WIDTH);
        mShadowPosition = dpToPx(getContext(), DEFAULT_SHADOW_POSITION);
        mSpeedOfDegree = DEFAULT_SPEED_OF_DEGREE;

        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RotateLoadingView);
            mColor = typedArray.getColor(R.styleable.RotateLoadingView_loading_color, mColor);
            mLoadingWidth = typedArray.getDimensionPixelSize(R.styleable.RotateLoadingView_loading_width, dpToPx(context, DEFAULT_WIDTH));
            mShadowPosition = typedArray.getInt(R.styleable.RotateLoadingView_shadow_position, DEFAULT_SHADOW_POSITION);
            mSpeedOfDegree = typedArray.getInt(R.styleable.RotateLoadingView_loading_speed, DEFAULT_SPEED_OF_DEGREE);
            mDrawableId = typedArray.getResourceId(R.styleable.RotateLoadingView_loading_icon, R.drawable.loading_icon);
            typedArray.recycle();
        }
        mSpeedOfArc = mSpeedOfDegree / 4;
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLoadingWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setColor(Color.BLACK);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);

        mMatrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mArc = 10;

        mLoadingRectF = new RectF(2 * mLoadingWidth, 2 * mLoadingWidth, w - 2 * mLoadingWidth, h - 2 * mLoadingWidth);
        mShadowRectF = new RectF(2 * mLoadingWidth + mShadowPosition, 2 * mLoadingWidth + mShadowPosition, w - 2 * mLoadingWidth + mShadowPosition, h - 2 * mLoadingWidth + mShadowPosition);
        if(mLoadingBitmap == null){
            mLoadingBitmap = getBitmap(mDrawableId,(int)(mShadowRectF.width()*0.5f),(int)(mShadowRectF.height()*0.5f));
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isStart) {
            return;
        }

        mPaint.setColor(Color.parseColor("#1a000000"));
        canvas.drawArc(mShadowRectF, mTopDegree, mArc, false, mPaint);
        canvas.drawArc(mShadowRectF, mBottomDegree, mArc, false, mPaint);

        float scale = mMatrixScale;
        if (mArc <= WAIT_DEGREE){
            scale = mMatrixScale * (1 - (WAIT_DEGREE - mArc)/ WAIT_DEGREE);
        }
        mMatrix.reset();
        mMatrix.preTranslate(mShadowRectF.centerX() - mLoadingBitmap.getWidth()/2 * scale, mShadowRectF.centerY() - mLoadingBitmap.getHeight()/2* scale);
        mMatrix.preScale(scale,scale);
        canvas.drawBitmap(mLoadingBitmap, mMatrix,mBitmapPaint);

        mPaint.setColor(mColor);
        canvas.drawArc(mLoadingRectF, mTopDegree, mArc, false, mPaint);
        canvas.drawArc(mLoadingRectF, mBottomDegree, mArc, false, mPaint);

        mTopDegree += mSpeedOfDegree;
        mBottomDegree += mSpeedOfDegree;
        if (mTopDegree > 360) {
            mTopDegree = mTopDegree - 360;
        }
        if (mBottomDegree > 360) {
            mBottomDegree = mBottomDegree - 360;
        }

        if (mChangeBigger) {
            if (mArc < 160) {
                mArc += mSpeedOfArc;
                invalidate();
            }
        } else {
            if (mArc > 10) {
                mArc -= 2 * mSpeedOfArc;
                invalidate();
            }
        }
        if (mArc >= 160 || mArc <= 10) {
            mChangeBigger = !mChangeBigger;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isStart = false;
        if (mLoadingBitmap != null){
            mLoadingBitmap.recycle();
            mLoadingBitmap = null;
        }
    }

    /**
     *  dp 转 px
     * @param context  上下文
     * @param dpVal dp值
     * @return
     */
    private int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * Return bitmap.
     *
     * @param resId     The resource id.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    private Bitmap getBitmap(@DrawableRes final int resId,
                                   final int maxWidth,
                                   final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = getContext().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * Return the sample size.
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    private int calculateInSampleSize(final BitmapFactory.Options options,
                                             final int maxWidth,
                                             final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }
}
