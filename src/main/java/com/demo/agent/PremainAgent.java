package com.demo.agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class PremainAgent {
    /**
     * 实现这个premain方法，该方法会在main方法前被调用。
     * @param agentArgs 随同“–javaagent”一起传入的程序参数，
     *                  如果它代表了多个参数，需要自己解析。
     * @param inst 用于添加自己定义的ClassFileTransformer，来改变class文件。
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("AgentArgs : " + agentArgs);
        inst.addTransformer(new MyClassTransformer());
    }

    static class MyClassTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader,
                                String className,
                                Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain,
                                byte[] classfileBuffer)
                throws IllegalClassFormatException {
            System.out.println("[Agent] Premain load class: " + className);

            //注意：这里的transform的className是xxx/xxx/xxx的形式，而javassist是xxx.xxx.xxx的形式
            String modifyTransformClass = "com/demo/agent/Test";
            String modifyJavassistClass = modifyTransformClass.replace('/', '.');
            String modifyMethod = "sayHi";
            byte[] byteCode = classfileBuffer;
            //表示找到了要修改的类
            if (modifyTransformClass.equals(className)) {
                System.out.println("[Agent] Found matched class: " + modifyTransformClass);
                try {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass cc = pool.get(modifyJavassistClass);
                    CtMethod method = cc.getDeclaredMethod(modifyMethod);
                    System.out.println("[Agent] Found matched method: " + modifyMethod);

                    //方法开始时，增加startTime的定义
                    method.addLocalVariable("startTime", CtClass.longType);
                    method.insertBefore("startTime = System.currentTimeMillis();");

                    //方法结束时，增加endTime和opTime的定义，并输出opTime
                    method.addLocalVariable("endTime", CtClass.longType);
                    method.addLocalVariable("opTime", CtClass.longType);

                    StringBuilder endBlock = new StringBuilder();
                    endBlock.append("endTime = System.currentTimeMillis();");
                    endBlock.append("opTime = endTime - startTime;");
                    endBlock.append("System.out.println(\"[Agent] OpTime=\" + opTime +\"ms\");");
                    method.insertAfter(endBlock.toString());

                    //可以通过以下方法直接修改原方法体。注意方法体要有{}包裹
                    //method.setBody("{System.out.println(\"Body has been changed.\");}");

                    //将修改后的Class转换成byteCode
                    byteCode = cc.toBytecode();
                    cc.detach();
                } catch (NotFoundException | CannotCompileException | IOException e) {
                    System.err.println("Exception occurs: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return byteCode;
        }
    }
}
