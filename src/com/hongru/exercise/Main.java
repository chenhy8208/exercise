package com.hongru.exercise;

import com.hongru.exercise.work.Dog;
import com.hongru.exercise.work.WorkUnit;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private final CountDownLatch latch = new CountDownLatch(2);

    @Test
    public void testT() {
        Dog dog = new Dog.Builder().setName("Leo Chen").setAge(35).build();
        WorkUnit<Dog> workUnit = new WorkUnit<>(dog);

        workUnit.getWorkUnit().descr();
    }


    @Test
    public void exercise() {
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hehe latch = " + latch.getCount());
    }

    public static void main(String[] args) {
	    // write your code here
        final Update update = new Update("Leo Update ...");
        final MicroBlogNode blog = new MicroBlogNode("No.1");
        final MicroBlogNode blog2 = new MicroBlogNode("No.2");

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();


        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    blog.propagateUpdate(update, blog2);
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    blog2.propagateUpdate(update, blog);
                }
            }.start();
        }


    }

    @Test
    public void hello() {
        String filename = DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date());
        System.out.println(filename + "Hello everyone");
    }


}
