package com.hebao.testkotlin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.hebao.testkotlin.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created by chasing on 2022/8/25.
 */
public class TriangleView extends View {

    private Paint mPaint;
    private Path mPath;

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);


        mPath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPath.reset();
        mPath.moveTo(0, getHeight());
        mPath.lineTo(getWidth() / 2f, 0);
        mPath.lineTo(getWidth(), getHeight());
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint.getShader() == null) {
            LinearGradient backGradient = new LinearGradient(0, 0, 0, getHeight(),
                    new int[]{ContextCompat.getColor(getContext(), R.color.black), ContextCompat.getColor(getContext(), R.color.purple_200)},
                    null, Shader.TileMode.CLAMP);
            mPaint.setShader(backGradient);
        }
        canvas.drawPath(mPath, mPaint);
    }
}
