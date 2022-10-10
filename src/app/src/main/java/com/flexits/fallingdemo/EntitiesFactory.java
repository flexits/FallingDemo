package com.flexits.fallingdemo;

//This is a factory of objects

import android.graphics.PointF;

public class EntitiesFactory {
    private static final String src = "KOROSTELIN";
    private static int index = 0;

    //Creates a new falling object
    //using consequential letter of the source string as the object's name
    public static FallingEntity GenerateAt(PointF coordinates){
        char c = src.charAt(index++);
        if (index >= src.length()) index = 0;
        return new FallingEntity(String.valueOf(c), coordinates);
    }
}
