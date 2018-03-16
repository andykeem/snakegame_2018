package com.example.foo.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by foo on 3/13/18.
 */

public class GameView extends SurfaceView implements Runnable, View.OnTouchListener {

    protected static final String TAG = GameView.class.getSimpleName();
    protected static final int UNIT_SIZE = 48;
    protected static final int SNAKE_INCREASE_UNIT = 3;

    protected Context mContext;
    protected SurfaceHolder mHolder;
    protected Thread mTask;
    protected boolean mRunning;
    protected double mRandomNum;
    protected Paint mApplePaint;
    protected Paint mSnakePaint;
    protected Point mOutSize = new Point();
    protected int mDeviceWidth;
    protected int mDeviceHeight;
    protected float mSnakeLeft;
    protected float mSnakeTop;
    protected float mSnakeRight;
    protected float mSnakeBottom;
    protected float mAppleLeft;
    protected float mAppleTop;
    protected float mAppleRight;
    protected float mAppleBottom;
    protected PointF mSnakePoint;
    protected PointF mApplePoint;
    protected PointF mTouchDownPoint;
    protected boolean mMoveBottom;
    protected boolean mMoveLeft;
    protected boolean mMoveRight;
    protected boolean mMoveTop;

    public GameView(Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
        this.init();
    }

    @Override
    public void run() {
        while (mRunning) {
            this.updateView();
            this.drawView();
            this.controlFPS();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMoveBottom = false;
                mMoveLeft = false;
                mMoveRight = false;
                mMoveTop = false;
                mTouchDownPoint = new PointF(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = (x - mTouchDownPoint.x);
                float dy = (y - mTouchDownPoint.y);
                if (dx > 0) {
                    mMoveRight = true;
                } else if (dx < 0) {
                    mMoveLeft = true;
                }
                if (dy > 0) {
                    mMoveBottom = true;
                } else if (dy < 0) {
                    mMoveTop = true;
                }
                mSnakePoint.set(x, y);
                break;
            case MotionEvent.ACTION_UP:
//                mMoveBottom = false;
//                mMoveLeft = false;
//                mMoveRight = false;
//                mMoveTop = false;
                break;
        }
        return true;
    }

    protected void init() {
        mHolder = this.getHolder();
        WindowManager winMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        winMgr.getDefaultDisplay().getSize(mOutSize);
        mDeviceWidth = mOutSize.x;
        mDeviceHeight = mOutSize.y;
        mRandomNum = Math.random();

        mSnakePaint = new Paint();
        mSnakePaint.setColor(Color.GREEN);
        mApplePaint = new Paint();
        mApplePaint.setColor(Color.RED);

        // init snake position
        float snakeX = 96;
        float snakeY = 96;
        mSnakePoint = new PointF(snakeX, snakeY);

        // init apple position
        // @TODO - check if apple collides with snake
        float appleX = (float) ((mDeviceWidth - UNIT_SIZE) * mRandomNum);
        float appleY = (float) ((mDeviceHeight - UNIT_SIZE) * mRandomNum);
        mApplePoint = new PointF(appleX, appleY);

        mAppleLeft = mApplePoint.x;
        mAppleTop = mApplePoint.y;
        mAppleRight = (mAppleLeft + UNIT_SIZE);
        mAppleBottom = (mAppleTop + UNIT_SIZE);

    }

    protected void updateView() {
        if (mMoveBottom) {
            mSnakeTop += 5;
        } else if (mMoveLeft) {
            mSnakeLeft -= 5;
        } else if (mMoveRight) {
            mSnakeLeft += 5;
        } else if (mMoveTop) {
            mSnakeTop -= 5;
        }
//        mSnakeLeft = mSnakePoint.x;
//        mSnakeTop = mSnakePoint.y;
        mSnakeRight = (mSnakeLeft + UNIT_SIZE);
        mSnakeBottom = (mSnakeTop + UNIT_SIZE);





    }

    protected void drawView() {
        if (mHolder == null) {
            return;
        }
        if (!mHolder.getSurface().isValid()) {
            return;
        }
        Canvas canvas = mHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.BLACK);

        // draw apple
        canvas.drawRect(mAppleLeft, mAppleTop, mAppleRight, mAppleBottom, mApplePaint);

        // draw snake
        canvas.drawRect(mSnakeLeft, mSnakeTop, mSnakeRight, mSnakeBottom, mSnakePaint);


        mHolder.unlockCanvasAndPost(canvas);
    }

    protected void controlFPS() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ie) {
            Log.e(TAG, ie.getMessage(), ie);
        }
    }

    public void onStart() {
        mRunning = true;
        mTask = new Thread(this);
        mTask.start();
    }

    public void onResume() {
        mRunning = true;
    }

    public void onPause() {
        mRunning = false;
    }

    public void onStop() {
        if (mTask != null) {
            try {
                mTask.join();
            } catch (InterruptedException ie) {
                Log.e(TAG, ie.getMessage(), ie);
            } finally {
                mTask = null;
            }
        }
    }
}
