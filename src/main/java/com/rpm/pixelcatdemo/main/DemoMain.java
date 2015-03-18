package com.rpm.pixelcatdemo.main;

import com.google.common.collect.ImmutableMap;
import com.rpm.pixelcat.common.Printer;
import com.rpm.pixelcat.kernel.Kernel;
import com.rpm.pixelcat.kernel.KernelInjection;
import com.rpm.pixelcat.kernel.KernelInjectionEventEnum;
import java.util.Map;

public class DemoMain {
    // utilities
    private static final Printer PRINTER = new Printer(DemoMain.class);

    public static void main(String arg[]) {
        try {
            // instantiate kernel
            Kernel kernel = new Kernel();

            // initialize
            kernel.init();

            // define kernel injections
            Map<KernelInjectionEventEnum, KernelInjection> kernelInjectionMap = ImmutableMap.<KernelInjectionEventEnum, KernelInjection>of(
                KernelInjectionEventEnum.PRE_PROCESSING, new GenericPreProcessingKernelInjection()
            );

            // run the game kernel
            kernel.kernelMain(kernelInjectionMap);
        } catch (Exception e) {
            PRINTER.printError(e);
        }

        // exit
        System.exit(0);
    }
}
