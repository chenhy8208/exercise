package com.hongru.exercise;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by leochen on 16/9/20.
 */
public class Update {

    public String getUpdateText() {
        return updateText;
    }

    private final String updateText;

    public Update(String updateText) {
        this.updateText = updateText;
    }



}
