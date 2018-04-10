package com.loading.viewexp.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.loading.viewexp.R;
import com.loading.viewexp.utils.BallUtil;

import java.util.Random;

/**
 * Created by zhangke on 2018/4/8.
 */
public class LoadingBallView extends View{

    private static final int MAX_RANDOM_NUM = 6;

    //绘制旋转的阴影
    private Paint mPaint;

    private int maxBallsCount = 5;
    //小球递减量
    private int maxBallReduceNum = 3;

    private Path mPath;

    private PathMeasure mPathMeasure;
    private float[] mPos = new float[2];
    private RectF mRectF;

    private float mv = 0.8f;
    private float mHandleLenRate = 2f;
    //圆球默认间隔
    private float mDistances = 75;
    //记录圆球运动过的长度
    private float mDisLen = 0;
    //最大圆球的半径
    private float maxBallsRadius = 30;
    //旋转的最大距离
    private float maxLen;

    private boolean mChangeBall = false;
    private int mColor;

    private boolean isStart = true;
    public LoadingBallView(Context context) {
        this(context,null);
    }

    public LoadingBallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingBallView);
            mColor = typedArray.getColor(R.styleable.LoadingBallView_ball_color, Color.parseColor("#FFA200"));
            maxBallsRadius = typedArray.getDimension(R.styleable.LoadingBallView_max_ball_radius,maxBallsRadius);
            maxBallsCount = typedArray.getInt(R.styleable.LoadingBallView_max_ball_count, maxBallsCount);
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);

        mPath = new Path();
        mPathMeasure = new PathMeasure();

        maxBallReduceNum = (int) ((maxBallsRadius - maxBallsRadius/maxBallsCount)/ maxBallsCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF == null){
            mRectF = new RectF(0,0,getMeasuredWidth(),getMeasuredHeight());
            mPath.addCircle(mRectF.centerX(),mRectF.centerY(),mRectF.width()/2- maxBallsRadius - MAX_RANDOM_NUM, Path.Direction.CW);
            mPathMeasure.setPath(mPath,false);
            mDistances = mPathMeasure.getLength() / maxBallsCount / 5;
        }

        canvas.save();
        canvas.rotate(-90,mRectF.centerX(),mRectF.centerY());
        for (int i = 0; i < maxBallsCount; i++) {
            float dis = mDisLen - i * mDistances;
            if (dis < 0){
                continue;
            }else if (dis > mPathMeasure.getLength()){
                drawPathToCanvas(canvas,mPathMeasure.getLength(),i);
                continue;
            }
            drawPathToCanvas(canvas,dis,i);
        }
        canvas.restore();
        if (isStart){
            isStart = !isStart;
            start();
        }
    }

    /**
     * 画布上绘制进度
     * @param canvas 画布
     * @param distances 距离
     * @param i 圆球下标
     */
    private void drawPathToCanvas(Canvas canvas,float distances,int i){
        mPathMeasure.getPosTan(distances, mPos,null);
//        if (i == maxBallsCount -1){
//            //后两个一样大
//            i = maxBallsCount - 2;
//        }

        float radius = maxBallsRadius - maxBallReduceNum * i;

        canvas.drawCircle(mPos[0], mPos[1], radius, mPaint);
        float[] pos = new float[2];
        if (distances < mDistances && distances>0 && distances < maxBallsRadius && i > 0){

            mPathMeasure.getPosTan(0, pos,null);
            mPathMeasure.getPosTan(mDistances/2, mPos,null);

            BallUtil.metaball(canvas,new BallUtil.Circle(pos,radius- maxBallReduceNum),
                    new BallUtil.Circle(mPos,radius),mPaint,
                    mv, mHandleLenRate,mDistances/2);
        }else if (distances == mPathMeasure.getLength()){
            //当前滑过的长度 - 整个圆长度  %  每段距离
            float dis = (mDisLen - distances) % mDistances;
            if (dis>0 && dis < maxBallsRadius){
                int count = (int) ((maxLen - mDisLen) / mDistances);
                int index = maxBallsCount - 1 - count;
                if (index == maxBallsCount -1){
                    return;
                }
                mPathMeasure.getPosTan(0, pos,null);
                mPathMeasure.getPosTan(mPathMeasure.getLength() - mDistances/2, mPos,null);

                BallUtil.metaball(canvas,new BallUtil.Circle(pos, maxBallsRadius - index * maxBallReduceNum),
                        new BallUtil.Circle(mPos, maxBallsRadius - (index+1) * maxBallReduceNum),mPaint,
                        mv, mHandleLenRate,mDistances/2);
            }
            mChangeBall = !mChangeBall;
            if (mChangeBall) {
                mPathMeasure.getPosTan(0, pos,null);
                canvas.drawCircle(pos[0], pos[1], maxBallsRadius +new Random().nextInt(MAX_RANDOM_NUM), mPaint);
            }
        }
    }

    /**
     * 一圈动画执行时间
     */
    public void start(){
        maxLen = mPathMeasure.getLength() + mDistances * maxBallsCount;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,maxLen);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDisLen = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }
}