package com.proj.observer;

public class Test {
    public static void main(String[] args) {
        Publisher observable = new Publisher();
        Subscriber observer = new Subscriber();

        observable.addObserver(observer);
        observable.setNews("news");
        System.out.println(observer.getNews());
    }
}
