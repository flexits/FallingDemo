package com.flexits.fallingdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowInsets;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final int LIST_UPDATE_INTERVAL = 1; //pause duration between storage deletion cycles, seconds
    private boolean allowExecution;             //control flag for threads
    private Rect displBounds;                   //screen dimensions excluding top and bottom toolbars

    private MainViewModel mvmodel;              //moving entities' storage
    private DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displBounds = new Rect();
        mvmodel = new MainViewModel();
        drawView = new DrawView(this, mvmodel.getEntities().getValue());
        setContentView(drawView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //get display size

        getWindowManager().getDefaultDisplay().getRectSize(displBounds);
        //get actionbar height
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        //adjust top offset
        WindowInsets wii = getWindow().getDecorView().getRootWindowInsets();
        displBounds.top = actionBarHeight + wii.getSystemWindowInsetTop();
        displBounds.bottom -= displBounds.top;
    }

    @Override
    protected void onResume(){
        super.onResume();
        //set flag to allow background threads
        allowExecution = true;
        //the thread triggers UI update in a fixed interval
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (allowExecution) {
                    try {
                        //delete the entities who had ended the movement
                        mvmodel.getEntities().getValue().removeIf(f -> f.getIsOutworn());
                        TimeUnit.SECONDS.sleep(LIST_UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //enable entities movement
        for (FallingEntity fe : mvmodel.getEntities().getValue()){
            fe.startMovement(displBounds);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop all threads
        allowExecution = false;
        for (FallingEntity fe : mvmodel.getEntities().getValue()){
            fe.stopMovement();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP){
            PointF coordinates = new PointF(event.getX(), event.getY() - displBounds.top);
            FallingEntity fe = EntitiesFactory.GenerateAt(coordinates);
            mvmodel.getEntities().getValue().add(fe);
            fe.startMovement(displBounds);
        }
        return false;
    }
}