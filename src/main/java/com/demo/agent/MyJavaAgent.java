package com.demo.agent;

import java.lang.instrument.Instrumentation;

public class MyJavaAgent {

    private static Instrumentation instrumentation;

    /**
     * 静态加载javaagent的JVM勾子函数。这个方法会在main函数之前被调用
     * @param agentArgs 随同“–javaagent”一起传入的程序参数，
     *                  如果它代表了多个参数，需要自己解析。
     * @param inst 用于添加自己定义的ClassFileTransformer，来改变class文件。
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
        System.out.println("Premain AgentArgs : " + agentArgs);
        inst.addTransformer(new MyClassTransformer());
    }

    /**
     * 动态加载javaagent的JVM勾子函数。当agent启动的时候，这个方法会被调用
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
        System.out.println("Agentmain AgentArgs : " + agentArgs);
        inst.addTransformer(new MyClassTransformer());
    }

    /**
     * 运行时动态加载javaagent的勾子函数，在代码中可以通过调用这个方法来动态加载agent。
     */
    public static void initialize() {
        if (instrumentation == null) {
            MyJavaAgentLoader.loadAgent();
        }
    }
}
