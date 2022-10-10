package com.flexits.fallingdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DrawView extends SurfaceView {

    private DrawThread drawThread;

    public DrawView(Context context, List<FallingEntity> entities) {
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //allow surface redraw
                setWillNotDraw(false);
                //create a drawing thread and start it
                drawThread = new DrawThread(holder, entities);
                drawThread.allowExecution(true);
                drawThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //finish drawing
                drawThread.allowExecution(false);
                //wait for the thread to terminate
                boolean retry = true;
                while (retry) {
                    try {
                        drawThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace(); }
                }
            }
        });
    }

    private static class DrawThread extends Thread{
        private final int UI_UPDATE_INTERVAL = 50;  //pause duration between UI updates, milliseconds

        private final SurfaceHolder holder;
        List<FallingEntity> entities;

        private boolean isRunning = false;

        public DrawThread(SurfaceHolder holder, List<FallingEntity> entities){
            this.holder = holder;
            this.entities = entities;
        }

        public void allowExecution(boolean isAllowed) {
            this.isRunning = isAllowed;
        }

        @Override
        public void run() {
            while (isRunning){
                //obtain canvas and perform drawing
                if (!holder.getSurface().isValid()) return;
                Canvas canvas = null;
                try {
                    //try to lock the resource to avoid conflicts
                    canvas = holder.lockCanvas();
                    synchronized (holder) {
                        //update the screen upon lock acquisition
                        if (canvas != null) {
                            //draw the game objects
                            canvas.drawColor(Color.YELLOW);

                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setColor(Color.BLACK);
                            paint.setTextSize(56);
                            paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
                            paint.setTextAlign(Paint.Align.CENTER);

                            for (FallingEntity fe : entities){
                                if (fe.getIsOutworn()) continue; //skip not moving entities
                                PointF coord = fe.getCoordinates();
                                canvas.drawText(fe.getName(), coord.x, coord.y, paint);
                            }
                        }
                    }
                } finally {
                    //unlock the resource if locked
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                //pause to limit FPS
                try {
                    TimeUnit.MILLISECONDS.sleep(UI_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
