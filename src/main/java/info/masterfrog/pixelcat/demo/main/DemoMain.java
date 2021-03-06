package info.masterfrog.pixelcat.demo.main;

import com.google.common.collect.ImmutableMap;
import info.masterfrog.pixelcat.engine.common.printer.Printer;
import info.masterfrog.pixelcat.engine.common.printer.PrinterFactory;
import info.masterfrog.pixelcat.engine.exception.TerminalErrorException;
import info.masterfrog.pixelcat.engine.hid.HIDEventEnum;
import info.masterfrog.pixelcat.demo.gameobject.GameObjectsHandler;
import info.masterfrog.pixelcat.demo.level.LevelHandler;
import info.masterfrog.pixelcat.demo.main.kernel.PreProcessingKernelInjection;
import info.masterfrog.pixelcat.demo.enumeration.LevelHandle;
import info.masterfrog.pixelcat.engine.kernel.*;

import java.util.HashMap;
import java.util.Map;

public class DemoMain {
    private static final Printer PRINTER = PrinterFactory.getInstance().createPrinter(DemoMain.class);

    public static void main(String arg[]) {
        try {
            // create kernel
            Kernel kernel = KernelFactory.getInstance().createKernel();

            // set up general kernel state initialization properties
            HashMap<KernelStatePropertyEnum, Object> kernelStateInitProperties = new HashMap<>();
            kernelStateInitProperties.put(KernelStatePropertyEnum.LOG_LVL, Printer.getLogLevelInfo());

            // initialize
            kernel.init(kernelStateInitProperties);
            KernelState kernelState = kernel.getKernelState();

            // set up general kernel action mappings
            KernelActionBinder kernelActionBinder = (KernelActionBinder) kernelState.getProperty(KernelStatePropertyEnum.KERNEL_ACTION_BINDER);
            kernelActionBinder.bind(HIDEventEnum.Q, KernelActionEnum.EXIT);
            kernelActionBinder.unbind(HIDEventEnum.F);
            kernelActionBinder.bind(HIDEventEnum.D, KernelActionEnum.FONT_DEBUG_TOGGLE);

            // init game objects
            LevelHandle startingLevel = LevelHandle.START_SCREEN;
            LevelHandler levelHandler = new LevelHandler(startingLevel);
            GameObjectsHandler gameObjectsHandler = new GameObjectsHandler(kernelState);
            gameObjectsHandler.init();
            kernel.registerGameObjectManagers(gameObjectsHandler.getGameObjectManagerList(startingLevel));

            // define kernel injections
            Map<KernelInjectionEventEnum, KernelInjection> kernelInjectionMap = ImmutableMap.<KernelInjectionEventEnum, KernelInjection>of(
                KernelInjectionEventEnum.PRE_PROCESSING, new PreProcessingKernelInjection(levelHandler, gameObjectsHandler)
            );

            // run the game kernel
            kernel.kernelMain(kernelInjectionMap);
        } catch (TerminalErrorException e) {
            PRINTER.printError(e);
        }

        // exit
        System.exit(0);
    }
}
