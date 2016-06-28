package com.rpm.pixelcat.demo.main.kernel;

import com.rpm.pixelcat.engine.common.memory.MemoryMonitorFactory;
import com.rpm.pixelcat.engine.common.printer.Printer;
import com.rpm.pixelcat.engine.common.printer.PrinterFactory;
import com.rpm.pixelcat.engine.exception.TransientGameException;
import com.rpm.pixelcat.engine.kernel.KernelInjection;
import com.rpm.pixelcat.engine.kernel.KernelState;

public class PostRenderingKernelInjection implements KernelInjection {
    private static final Printer PRINTER = PrinterFactory.getInstance().createPrinter(PostRenderingKernelInjection.class);

    public void run(KernelState kernelState) throws TransientGameException {
        // debug
        PRINTER.printTrace("Test kernel-injected post-processor started...");

        // memory pulse
        PRINTER.printInfo("\n" + MemoryMonitorFactory.getInstance().getMemoryMonitorUtil().getMemoryPulseString());

        // debug
        PRINTER.printTrace("Test kernel-injected post-processor ended...");
    }
}
