package com.flexits.fallingdemo;

import android.graphics.PointF;
import android.graphics.Rect;

import java.util.concurrent.TimeUnit;

//This class represents a falling object

public class FallingEntity {

    private final PointF coordinates;
    private final String name;
    private boolean movementenabled;
    private Rect display;
    private boolean isoutworn;

    public FallingEntity(String name, PointF coordinates){
        this.name = name;
        this.coordinates = coordinates;
        movementenabled = false;
        isoutworn = false;
    }

    public PointF getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public boolean getIsOutworn(){
        return isoutworn;
    }

    public void startMovement(Rect display){
        if (display == null) return;
        this.display = display;
        movementenabled = true;
        new Thread(runnable).start();
    }

    public void stopMovement(){
        movementenabled = false;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //S = v0*t + a*(t^2)/2
            //for a single cycle t = 1; S = v0 + a/2
            //v = v0 + a*t
            //for a single cycle t = 1; v = v0 + a
            float acceleration = 2f;    //acceleration
            float speed = 0;            //speed after each cycle
            float restitution = 0.75f;  //restitution coefficient
            int direction = 1;          //1 when going up, -1 when going down
            float x_offset = 5;         //number of pixels object moves along X axis each cycle
            final float SPEED_LOWER_THRESHOLD = 2; //minimum speed at which an object is considered still
            final float X_LOWER_OFFSET = 0.25f;      //minimum offset at which an object is considered still
            final int CYCLE_DURATION = 25; //pause between cycles, milliseconds
            while(movementenabled){
                //accelerated movement
                coordinates.x += x_offset;
                coordinates.y += (speed + acceleration/2) * direction;
                speed += acceleration;
                //bottom reached - bounce
                if (coordinates.y >= display.bottom){
                    coordinates.y = display.bottom;
                    acceleration *= -1;
                    direction = -1;
                    speed *= restitution;
                    x_offset *= restitution;
                    //if the object isn't moving anymore, set the flag and exit
                    if (speed <= SPEED_LOWER_THRESHOLD || x_offset <= X_LOWER_OFFSET){
                        isoutworn = true;
                        break;
                    }
                }
                //top reached - fall
                if (speed <= 0){
                    speed = 0;
                    acceleration *= -1;
                    direction = 1;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(CYCLE_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
