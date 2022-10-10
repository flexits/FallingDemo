package com.flexits.fallingdemo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CopyOnWriteArrayList;

public class MainViewModel extends ViewModel {
    MutableLiveData<CopyOnWriteArrayList<FallingEntity>> entities;

    public MutableLiveData<CopyOnWriteArrayList<FallingEntity>> getEntities() {
        if (entities == null) entities = new MutableLiveData<>(new CopyOnWriteArrayList<>());
        return entities;
    }
}
