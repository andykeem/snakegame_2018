package com.example.foo.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by foo on 3/13/18.
 */

public class GameView extends SurfaceView implements Runnable {

    protected static final String TAG = GameView.class.getSimpleName();
    protected static final int UNIT_SIZE = 50;
    protected static final int SNAKE_INCREASE_UNIT = 3;

    protected Context mContext;
    protected SurfaceHolder mHolder;
    protected boolean mRunning;
    protected Thread mTask;
    protected double mRandomNum;
    protected Paint mApplePaint;
    protected Paint mSnakePaint;
    protected Point mOutSize = new Point();
    protected int mDeviceWidth;
    protected int mDeviceHeight;
    protected float mAppleLeft;
    protected float mAppleTop;
    protected float mAppleRight;
    protected float mAppleBottom;

    public GameView(Context context) {
        super(context);
        mContext = context;
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

    protected void init() {
        mHolder = this.getHolder();
        mRandomNum = Math.random();
        mApplePaint = new Paint();
        mApplePaint.setColor(Color.RED);
        mSnakePaint = new Paint();
        mSnakePaint.setColor(Color.GREEN);
        WindowManager winMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        winMgr.getDefaultDisplay().getSize(mOutSize);
        mDeviceWidth = mOutSize.x;
        mDeviceHeight = mOutSize.y;

        mAppleLeft = (float) ((mDeviceWidth - UNIT_SIZE) * mRandomNum);
        mAppleTop = (float) ((mDeviceHeight - UNIT_SIZE) * mRandomNum);
        mAppleRight = mAppleLeft + UNIT_SIZE;
        mAppleBottom = mAppleTop + UNIT_SIZE;

    }

    protected void updateView() {

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


        mHolder.unlockCanvasAndPost(canvas);
    }

    protected void controlFPS() {

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
