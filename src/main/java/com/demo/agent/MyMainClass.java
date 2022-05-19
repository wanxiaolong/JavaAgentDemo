package com.demo.agent;

public class MyMainClass {

    //动态加载Agent的时候要用到这个static块，
    //静态加载Agent的时候要注释掉
//    static {
//        MyJavaAgent.initialize();
//    }

    public static void main(String args[]) {
        MyUser test = new MyUser();
        String result = test.sayHi("Doris");
        System.out.println("[Main ] " + result);
    }
}
