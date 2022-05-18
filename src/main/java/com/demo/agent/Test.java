package com.demo.agent;

public class Test {
    public static void main(String args[]) {
        Test test = new Test();
        String result = test.sayHi("Doris");
        System.out.println("[Main ] " + result);
    }

    public String sayHi(String name) {
        String result = "Hi, " + name + "!";
        return result;
    }
}
