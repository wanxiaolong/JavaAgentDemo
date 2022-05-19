package com.demo.agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MyClassTransformer implements ClassFileTransformer {

    //注意：这里的transform的className是xxx/xxx/xxx的形式，而javassist是xxx.xxx.xxx的形式
    private String modifyTransformClass = "com/demo/agent/MyUser";
    private String modifyJavassistClass = modifyTransformClass.replace('/', '.');
    private String modifyMethod = "sayHi";

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)
            throws IllegalClassFormatException {
        System.out.println("[Agent] load class: " + className);
        byte[] byteCode = classfileBuffer;
        //表示找到了要修改的类
        if (modifyTransformClass.equals(className)) {
            System.out.println("[Agent] Found matched class: " + modifyJavassistClass);
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
