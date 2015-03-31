package eu.thedarken.rootvalidator.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by darken on 30.12.2014.
 */
public class ColorFramedCircleDrawable extends Drawable {
    private final Paint mPaint;
    private final int mColor;
    private final Drawable mDrawable;
    private final int mSize;

    public ColorFramedCircleDrawable(Drawable drawable, int size, int color) {
        mDrawable = drawable;
        mSize = size;
        mColor = color;
        mPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(mSize / 2, mSize / 2, mSize, mPaint);
        if (mDrawable != null) {
            float scaleFactor = 0.6f;
            float start = 1f / 2f - scaleFactor / 2f;
            float end = 1f / 2f + scaleFactor / 2f;
            mDrawable.setBounds((int) (mSize * start), (int) (mSize * start), (int) (mSize * end), (int) (mSize * end));
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // NOPE
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // NOPE
        throw new UnsupportedOperationException();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }
}
