package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class MyView extends View {

    Paint mPaint;
    Context mContext;

    public MyView(Context context) {
        super(context);
        mContext = context;
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        mContext = context;
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec)
    {
        mPaint = new Paint();
        mPaint.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawRect(0, 0, 100, 100, mPaint);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent ev)
    {
        ev.getText().add("MY VIEW!");
        return true;
    }
}
