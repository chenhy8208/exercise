package com.hongru.exercise.work;

/**
 * Created by leochen on 16/9/21.
 */
public class Dog {
    private String name = null;
    private int age = 0;

    private Dog(){}

    private Dog(Builder b) {
        this.name = b.name;
        this.age = b.age;
    }

    public void descr() {
        System.out.println("name = "+ name +",age = " + age);
    }

    public static class Builder {
        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public String name = null;
        public int age = 0;

        public Dog build() {
            return new Dog(this);
        }
    }


}
