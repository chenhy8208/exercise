package com.hongru.exercise.work;

/**
 * Created by leochen on 16/9/21.
 */
public class WorkUnit <T>{
    public T getWorkUnit() {
        return workUnit;
    }

    private final T workUnit;

    public WorkUnit(T workUnit) {
        this.workUnit = workUnit;
    }

}
