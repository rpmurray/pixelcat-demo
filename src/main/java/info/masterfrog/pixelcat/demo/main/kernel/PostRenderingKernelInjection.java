package info.masterfrog.pixelcat.demo.main.kernel;

import info.masterfrog.pixelcat.engine.common.memory.MemoryMonitorFactory;
import info.masterfrog.pixelcat.engine.common.printer.Printer;
import info.masterfrog.pixelcat.engine.common.printer.PrinterFactory;
import info.masterfrog.pixelcat.engine.exception.TransientGameException;
import info.masterfrog.pixelcat.engine.kernel.KernelInjection;
import info.masterfrog.pixelcat.engine.kernel.KernelState;

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
