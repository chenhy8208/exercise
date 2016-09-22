package com.hongru.exercise;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by leochen on 16/9/20.
 */
public class MicroBlogNode {
    private final Lock lock = new ReentrantLock();

    private final String ident;
    public MicroBlogNode(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    public void propagateUpdate(Update upd_, MicroBlogNode bakcup_) {
        lock.lock();

        try {
            System.out.println(upd_.getUpdateText() + "_ to" + bakcup_.getIdent());
            bakcup_.confirmUpdate(this, upd_);
        } finally {
            lock.unlock();
        }
    }


    public void confirmUpdate(MicroBlogNode other_, Update upd_) {
        lock.lock();


        try {
            System.out.println(upd_.getUpdateText() + "_ from _" + other_.getIdent());
        } finally {
            lock.unlock();
        }
    }




}
