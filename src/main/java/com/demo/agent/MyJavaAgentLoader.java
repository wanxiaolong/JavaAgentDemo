package com.demo.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class MyJavaAgentLoader {
    //agent的jar包的全路径。即包含了MANIFEST.MF中Agent-Class指向的类的jar包，
    //这里不需要包含第三方的依赖
    private static final String agentFilePath =
            "/Users/wanxiaolong/Workspace/JavaAgentDemo/target/" +
            "JavaAgentDemo-1.0-SNAPSHOT.jar";

    //期望修改的VM
    private static final String expectedVM = MyMainClass.class.getCanonicalName();

    public static void loadAgent() {
        //列出所有的JVM进程，因为当前机器上可能运行有其他的JVM进程，所以要过滤
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor vmDescriptor : vms) {
            //如果是我们期望的这个进程
            if (expectedVM.equals(vmDescriptor.displayName())) {
                System.out.println("Found target VM.");
                try {
                    //与目标JVM建立连接
                    VirtualMachine vm = VirtualMachine.attach(vmDescriptor.id());
                    System.out.println("Attached to target VM");
                    //加载agent
                    vm.loadAgent(agentFilePath);
                    System.out.println("Agent loaded for target VM");
                    vm.detach();
                } catch (Exception e) {
                    throw new RuntimeException("Load agent failed with exception: " + e.getMessage());
                }
            }
        }
    }
}
