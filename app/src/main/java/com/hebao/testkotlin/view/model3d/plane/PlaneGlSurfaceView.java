package com.hebao.testkotlin.view.model3d.plane;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 平面GLSurfaceView
 */
public class PlaneGlSurfaceView extends GLSurfaceView {

    private float mSceneWidth, mSceneHeight;
    private OnEventListener touchListener;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private boolean hadCheckOrientation;
    private boolean isOrientationX;
    private float mScale = 1;

    public PlaneGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public PlaneGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                hadCheckOrientation = false;
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!hadCheckOrientation) {
                    isOrientationX = Math.abs(distanceX) >= Math.abs(distanceY);
                    hadCheckOrientation = true;
                }
                if (touchListener != null) {
                    touchListener.onTouchEvent(isOrientationX ? distanceX : 0, isOrientationX ? 0 : distanceY);
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (touchListener != null) {
                    touchListener.onScaleEvent(mScale * detector.getScaleFactor());
                }
                Log.e("Plane", String.valueOf(detector.getScaleFactor()));
                return super.onScale(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                mScale *= detector.getScaleFactor();
            }
        });
    }

    public void setOnTouchListener(OnEventListener listener) {
        touchListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setSceneWidthAndHeight(this.getMeasuredWidth(), this.getMeasuredHeight());
    }

    public void setSceneWidthAndHeight(float mSceneWidth, float mSceneHeight) {
        this.mSceneWidth = mSceneWidth;
        this.mSceneHeight = mSceneHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        mScaleGestureDetector.onTouchEvent(e);
        return true;
    }

    /**
     * 触摸监听接口
     */
    public interface OnEventListener {
        void onTouchEvent(float dx, float dy);

        void onScaleEvent(float scale);
    }
}
