package home.pocetak;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;


public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    MySurfaceThread thread;
    Paint paint1;
    float x, y, dx, dy, angle, c;
    int zeroX, zeroY, radius;
    private Bitmap ball, pozadina;

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        thread = new MySurfaceThread(getHolder(),this);
        getHolder().addCallback(this);
        paint1 = new Paint();
        paint1.setTextSize(40);
        paint1.setColor(Color.rgb(255, 0, 0));
        x = y = dx = dy = 0;
        radius = 212;
        ball = BitmapFactory.decodeResource(getResources(), R.mipmap.ball);
        pozadina = BitmapFactory.decodeResource(getResources(), R.mipmap.pozadina);


    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.cancel();
    }


    public void draw(Canvas canvas, float localX, float localY, int zx, int zy) {
        super.draw(canvas);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(pozadina,
                this.getWidth(),
                this.getHeight(),
                true);
        canvas.drawRGB(255, 255, 255);
        canvas.drawBitmap(scaledBitmap, canvas.getWidth()/2-scaledBitmap.getWidth()/2, canvas.getHeight()/2-scaledBitmap.getHeight()/2, null);
//        canvas.drawLine(0, 0, canvas.getWidth(), canvas.getHeight(), paint1);
        canvas.drawText(String.valueOf(localX), 20, 40, paint1);
        canvas.drawText(String.valueOf(localY), 20, 100, paint1);
        if (localX == 0 && localY == 0) {
            canvas.drawBitmap(ball, canvas.getWidth() / 2 - ball.getWidth() / 2, canvas.getHeight() / 2 - ball.getHeight() / 2, null);
        }
        else {
            canvas.drawBitmap(ball, x - ball.getWidth()/2, y - ball.getHeight()/2, null);
        }
    }

    public class MySurfaceThread extends Thread{

        SurfaceHolder mSurfaceHolder;
        MySurfaceView mySurfaceView;
        boolean running = true;


        public MySurfaceThread(SurfaceHolder surfaceHolder, MySurfaceView surfaceView){

            mSurfaceHolder = surfaceHolder;
            mySurfaceView = surfaceView;

            mySurfaceView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //x = event.getX();
                    //y = event.getY();
                    calculateValues(x, y);

                    switch(event.getAction() & MotionEvent.ACTION_MASK){

                        case MotionEvent.ACTION_UP:
                            x = y = 0;
                            dx = dy = 0;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            x = event.getX();
                            y = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            x = event.getX();
                            y = event.getY();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                    }

                    return true;
                }

                private void calculateValues(float xx, float yy) {

                    dx = xx - zeroX;
                    dy = yy - zeroY;
                    angle = (float)Math.atan(Math.abs(dy/dx));
                    c = (float)Math.sqrt(dx*dx+dy*dy);
                    if(c > radius){
                        if(dx>0 && dy>0){ // bot right
                            xx = (float)(zeroX + (radius*Math.cos(angle)));
                            yy = (float)(zeroY + (radius*Math.sin(angle)));
                        }
                        else if(dx>0 && dy<0){ // top right
                            xx = (float)(zeroX + (radius*Math.cos(angle)));
                            yy = (float)(zeroY - (radius*Math.sin(angle)));
                        }
                        else if(dx<0 && dy <0){ //top left
                            xx = (float)(zeroX - (radius*Math.cos(angle)));
                            yy = (float)(zeroY - (radius*Math.sin(angle)));
                        }
                        else if(dx<0 && dy>0){
                            xx = (float)(zeroX - (radius*Math.cos(angle)));
                            yy = (float)(zeroY + (radius*Math.sin(angle)));
                        }

                    }
                    else{
                        xx = zeroX + dx;
                        yy = zeroY + dy;
                    }
                    x = xx;
                    y = yy;


                }

            });


        }

        public void run() {

            while (running) {


                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        zeroX = canvas.getWidth()/2;
                        zeroY = canvas.getHeight()/2;
                        mySurfaceView.draw(canvas,x, y, zeroX, zeroY);
                    }

                    Thread.sleep(50);

                } catch (InterruptedException e) {
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }

        }
        public void cancel() {
            try {
                mySurfaceView.setBackgroundResource(R.mipmap.pozadina);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}