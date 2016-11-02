package com.project_maga_salakuna.magasalakuna.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by lakshan on 10/30/16.
 */
public class CircleView extends View{

    private static final int MODE_PINCH = 0;
    private static final int MODE_DONT_CARE = 1;

    ShapeDrawable mCircleDrawable;
    int mTouchMode = MODE_DONT_CARE;

    int top, right, bottom, left;
    int maxX;
    int maxY;

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCircleDrawable = new ShapeDrawable(new OvalShape());
        mCircleDrawable.getPaint().setColor(0x502eb82e);
        Display mdisp = ((WindowManager)(getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;


        //makeCircleBounds();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        maxX = MeasureSpec.getSize(widthMeasureSpec);
        maxY = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(maxX, maxY);
        this.setLayoutParams(new CoordinatorLayout.LayoutParams(maxX,maxY));
        left = 0;
        top = (maxY - maxX)/2;
        right = maxX;
        bottom = top + maxX;
        mCircleDrawable.setBounds(left, top, right, bottom);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context) {
        this(context, null, 0);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getActionMasked()) {
////            case MotionEvent.ACTION_DOWN:
////                mCircleDrawable.setBounds(0, 0, 0, 0);
////                invalidate();
////                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                prepareCircleDrawing(event);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mTouchMode == MODE_PINCH) {
//                    prepareCircleDrawing(event);
//                }
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                if (event.getActionIndex() <= 1) {
//                    mTouchMode = MODE_DONT_CARE;
//                }
//                break;
//            default:
//                super.onTouchEvent(event);
//        }
//
//        return true;
//    }

    private void prepareCircleDrawing(MotionEvent event) {

        int index = event.getActionIndex();

        if (index > 1) {
            return;
        }
        mTouchMode = MODE_PINCH;
        if (event.getX(0) < event.getX(1)) {
            left = (int) event.getX(0);
            right = (int) event.getX(1);
        } else {
            left = (int) event.getX(1);
            right = (int) event.getX(0);
        }

        if (event.getY(0) < event.getY(1)) {
            top = (int) event.getY(0);
            bottom = (int) event.getY(1);
        } else {
            top = (int) event.getY(1);
            bottom = (int) event.getY(0);
        }
        makeCircleBounds();
        mCircleDrawable.setBounds(left, top, right, bottom);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCircleDrawable.draw(canvas);
    }

    public void resizeCircle(int maxX){
        left = (this.maxX - maxX)/2;
        top = (maxY - maxX)/2;
        right = left + maxX;
        bottom = top + maxX;
        //makeCircleBounds();
        mCircleDrawable.setBounds(left, top, right, bottom);
        invalidate();
    }
    public int getBounds(){
        return maxX;
    }
    public void makeCircleBounds(){

        int height = bottom - top;
        int width = right - left;

        if (height > width) {
            int delta = height - width;
            //top += delta / 2;
            bottom -= delta;
        } else {
            int delta = width - height;
            left += delta / 2;
            right -= delta / 2;
        }
    }

}
