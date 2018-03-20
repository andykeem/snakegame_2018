package com.example.foo.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.example.foo.snakegame.helper.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by foo on 3/13/18.
 */

public class GameView extends SurfaceView implements Runnable, View.OnTouchListener,
        SurfaceHolder.Callback {

    protected static final String TAG = GameView.class.getSimpleName();
    protected static final int UNIT_SIZE = 50;
    protected static final int SNAKE_INCREASE_UNIT = 3;

    protected Context mContext;
    protected SurfaceHolder mHolder;
    protected Thread mTask;
    protected boolean mRunning;
    protected Random mRand;
    protected Paint mApplePaint;
    protected Paint mSnakePaint;
    protected Paint mGridPaint;
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
    protected PointF mTouchPoint = new PointF();
    protected boolean mMoveBottom;
    protected boolean mMoveLeft;
    protected boolean mMoveRight;
    protected boolean mMoveTop;
    protected enum Move { BOTTOM, LEFT, TOP, RIGHT }
    protected Move mNextMove;

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
        L.d("before x: " + x + ", y: " + y);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = (x - mTouchPoint.x);
                float dy = (y - mTouchPoint.y);
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
//                if ((absDx > 300) || (absDy > 300)) {
                    if (absDx > absDy ) {
                        int mod = (((int) x) % UNIT_SIZE);
                        boolean canMove = (mod == 0);
                        if (!(mMoveLeft || mMoveRight)) {
                            if (dx > 0) {
                                if (canMove) {
                                    if (mNextMove == Move.RIGHT) {
                                        this.resetMove();
                                        mMoveRight = true;
                                        mNextMove = null;
                                    } else {
                                        this.resetMove();
                                        mMoveRight = true;
                                    }
                                } else {
                                    mNextMove = Move.RIGHT;
                                }
                            } else if (dx < 0) {
                                if (canMove) {
                                    if (mNextMove == Move.LEFT) {
                                        this.resetMove();
                                        mMoveLeft = true;
                                        mNextMove = null;
                                    } else {
                                        this.resetMove();
                                        mMoveLeft = true;
                                    }
                                } else {
                                    mNextMove = Move.LEFT;
                                }
                            }
                        }
                    } else if (absDx < absDy) {
                        int mod = (((int) y) % UNIT_SIZE);
                        boolean canMove = (mod == 0);
                        if (!(mMoveBottom || mMoveTop)) {
                            if (dy > 0) {
                                if (canMove) {
                                    if (mNextMove == Move.BOTTOM) {
                                        this.resetMove();
                                        mMoveBottom = true;
                                        mNextMove = null;
                                    }
                                    this.resetMove();
                                    mMoveBottom = true;
                                } else {
                                    mNextMove = Move.BOTTOM;
                                }
                            } else if (dy < 0) {
                                if (canMove) {
                                    if (mNextMove == Move.TOP) {
                                        this.resetMove();
                                        mMoveTop = true;
                                        mNextMove = null;
                                    } else {
                                        this.resetMove();
                                        mMoveTop = true;
                                    }
                                } else {
                                    mNextMove = Move.TOP;
                                }
                            }
                        }
                    }
//                }
                L.d("after x: " + x + ", y: " + y);
                mSnakePoint.set(x, y);
//                mTouchPoint.set(x, y);
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        this.drawGrid(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    protected void init() {
        mHolder = this.getHolder();
        WindowManager winMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        winMgr.getDefaultDisplay().getSize(mOutSize);
        mDeviceWidth = mOutSize.x;
        mDeviceHeight = mOutSize.y;
        mRand = new Random();

        mSnakePaint = new Paint();
        mSnakePaint.setColor(Color.GREEN);
        mApplePaint = new Paint();
        mApplePaint.setColor(Color.RED);
        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);

        // init snake position
        float snakeX = 100;
        float snakeY = 100;
        mSnakePoint = new PointF(snakeX, snakeY);

        // init apple position
        this.placeApple();

        mAppleLeft = mApplePoint.x;
        mAppleTop = mApplePoint.y;
        mAppleRight = (mAppleLeft + UNIT_SIZE);
        mAppleBottom = (mAppleTop + UNIT_SIZE);

        mHolder.addCallback(this);
    }

    protected void updateView() {

        mSnakeRight = (mSnakeLeft + UNIT_SIZE);
        mSnakeBottom = (mSnakeTop + UNIT_SIZE);

        // check collision with apple


        if (mMoveBottom) {
            mSnakeTop += 5;
        } else if (mMoveLeft) {
            mSnakeLeft -= 5;
        } else if (mMoveRight) {
            mSnakeLeft += 5;
        } else if (mMoveTop) {
            mSnakeTop -= 5;
        }
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

        // draw grid
        for (int i = 0; i < mDeviceHeight; i++) {
            float startX = 0;
            float startY = UNIT_SIZE * i;
            float stopX = mDeviceWidth;
            canvas.drawLine(startX, startY, stopX, startY, mGridPaint);
        }

        for (int i = 0; i < mDeviceWidth; i++) {
            float startX = UNIT_SIZE * i;
            float startY = 0;
            float stopY = mDeviceHeight;
            canvas.drawLine(startX, startY, startX, stopY, mGridPaint);
        }

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

    protected void resetMove() {
        mMoveBottom = false;
        mMoveLeft = false;
        mMoveRight = false;
        mMoveTop = false;
    }

    protected int getRandomAppleX() {
        List<Integer> list = new ArrayList<Integer>();
        int size = (mDeviceWidth - UNIT_SIZE);
        int n = 0;
        int i = 1;
        while (n < size) {
            n = i * UNIT_SIZE;
            list.add(n);
            i++;
        }
        int index = mRand.nextInt(list.size());
        return list.get(index);
    }

    protected int getRandomAppleY() {
        List<Integer> list = new ArrayList<>();
        int size = (mDeviceHeight - UNIT_SIZE);
        int n = 0;
        int i = 1;
        while (n < size) {
            n = i * UNIT_SIZE;
            list.add(n);
            i++;
        }
        int index = mRand.nextInt(list.size());
        return list.get(index);
    }

    protected void placeApple() {
//        int appleX = (int) ((mDeviceWidth - UNIT_SIZE) * mRandomNum);
//        int appleY = (int) ((mDeviceHeight - UNIT_SIZE) * mRandomNum);

        int x = this.getRandomAppleX();
        int y = this.getRandomAppleY();

        L.d("x: " + x + ", y: " + y);

        // @TODO - check if apple collides with snake
        /*while () {

        }*/

        mApplePoint = new PointF(x, y);
    }

    protected void drawGrid(Canvas canvas) {

    }
}
