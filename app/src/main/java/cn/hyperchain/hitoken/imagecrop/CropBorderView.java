package cn.hyperchain.hitoken.imagecrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


public class CropBorderView extends View {

    public static CropBorderOption borderOption;

    private Paint mPaint;

    private int mHorizontalPadding;//default

    private int mVerticalPadding;

    private int mBoderWidth = 1;

    private int mCropWidth;

    private int mCropHeight;

    public CropBorderView(Context context) {
        this(context, null);
    }

    public CropBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("CropBorderView", "mHorizontalPadding=" + mHorizontalPadding);
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
        mBoderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBoderWidth, getResources().getDisplayMetrics());
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (borderOption != null) {
            switch (borderOption) {
                case TWO2THREE:
                    mCropWidth = (getWidth() - 2 * mHorizontalPadding);
                    int newWidth = mCropWidth;
                    mCropHeight = (newWidth*3)/2;
                    break;
                case THREE2TWO:
                    mCropWidth = (getWidth() - 2 * mHorizontalPadding);
                    mCropHeight = (mCropWidth*2)/3;
                    break;
                case FIVE2EIGHT:
                    mCropWidth = (getWidth() - 2 * mHorizontalPadding);
                    mCropHeight = (mCropWidth*8)/15;
                    break;
                case ONE2ONE:
                default:
                    mCropWidth = (getWidth() - 2 * mHorizontalPadding);
                    mCropHeight = mCropWidth;
                    break;
            }
        }else {
            mCropWidth = (getWidth() - 2 * mHorizontalPadding);
            mCropHeight = mCropWidth;
        }


        mVerticalPadding = (getHeight() - mCropHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
        canvas.drawRect(mHorizontalPadding, 0, mHorizontalPadding + mCropWidth, mVerticalPadding, mPaint);
        canvas.drawRect(mHorizontalPadding + mCropWidth, 0, getWidth(), getHeight(), mPaint);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding + mCropHeight, mHorizontalPadding + mCropWidth, getHeight(), mPaint);
        mPaint.setColor(Color.parseColor("#F39300"));
        mPaint.setStrokeWidth(mBoderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding, mHorizontalPadding + mCropWidth, mVerticalPadding + mCropHeight, mPaint);
    }


    public void setHorizontalPadding(int padding) {
        this.mHorizontalPadding = padding;
    }

}
