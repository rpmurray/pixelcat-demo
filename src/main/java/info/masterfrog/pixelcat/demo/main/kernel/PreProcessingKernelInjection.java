package info.masterfrog.pixelcat.demo.main.kernel;

import info.masterfrog.pixelcat.demo.enumeration.GameObjectHandle;
import info.masterfrog.pixelcat.demo.enumeration.LevelHandle;
import info.masterfrog.pixelcat.demo.gameobject.GameObjectsHandler;
import info.masterfrog.pixelcat.demo.level.LevelHandler;
import info.masterfrog.pixelcat.engine.common.printer.Printer;
import info.masterfrog.pixelcat.engine.common.printer.PrinterFactory;
import info.masterfrog.pixelcat.engine.exception.TransientGameException;
import info.masterfrog.pixelcat.engine.hid.HIDEventEnum;
import info.masterfrog.pixelcat.engine.kernel.KernelActionEnum;
import info.masterfrog.pixelcat.engine.kernel.KernelInjection;
import info.masterfrog.pixelcat.engine.kernel.KernelState;
import info.masterfrog.pixelcat.engine.kernel.KernelStatePropertyEnum;
import info.masterfrog.pixelcat.engine.logic.gameobject.feature.Renderable;
import info.masterfrog.pixelcat.demo.enumeration.GameObjectManagerHandle;

import java.awt.*;

public class PreProcessingKernelInjection implements KernelInjection {
    private LevelHandler levelHandler;
    private GameObjectsHandler gameObjectsHandler;

    private static final Printer PRINTER = PrinterFactory.getInstance().createPrinter(PreProcessingKernelInjection.class);

    public PreProcessingKernelInjection(LevelHandler levelHandler, GameObjectsHandler gameObjectsHandler) {
        this.levelHandler = levelHandler;
        this.gameObjectsHandler = gameObjectsHandler;
    }

    public void run(KernelState kernelState) throws TransientGameException {
        PRINTER.printTrace("Test kernel-injected pre-processor started...");

        // handle exit trigger
        if (kernelState.hasHIDEvent(HIDEventEnum.ESC)) {
            kernelState.addKernelAction(KernelActionEnum.EXIT);
        }

        // level one specifics
        if (levelHandler.getCurrentLevel().equals(LevelHandle.L1)) {
            // handle nyan cat render level
            gameObjectsHandler.getGameObject(GameObjectHandle.CAT_CHARACTER).getFeature(Renderable.class).setLayer(
                gameObjectsHandler.getGameObject(GameObjectHandle.CAT_CHARACTER).getFeature(Renderable.class).getPosition().y
            );
        }

        // handle level transition
        if (kernelState.hasHIDEvent(HIDEventEnum.ENTER)) {
            kernelState.removeHIDEvent(HIDEventEnum.ENTER);
            LevelHandle currentLevel = levelHandler.getCurrentLevel();
            LevelHandle nextLevel = levelHandler.getNextLevel();
            if (nextLevel == null) {
                kernelState.addKernelAction(KernelActionEnum.EXIT);
            } else {
                PRINTER.printInfo("Level transition triggered [" + currentLevel + ">>>" + nextLevel + "]...");
                kernelState.setProperty(
                    KernelStatePropertyEnum.ACTIVE_GAME_OBJECT_MANAGERS,
                    gameObjectsHandler.getGameObjectManagerList(nextLevel)
                );
                gameObjectsHandler.getGameObjectManager(GameObjectManagerHandle.COMMON).getLayerManager().setLayerCount(
                    ((Rectangle) kernelState.getProperty(KernelStatePropertyEnum.SCREEN_BOUNDS)).height
                );
                gameObjectsHandler.getGameObject(GameObjectHandle.CAT_CHARACTER).getFeature(Renderable.class).setLayer(
                    gameObjectsHandler.getGameObject(GameObjectHandle.CAT_CHARACTER).getFeature(Renderable.class).getPosition().y
                );
            }
        }

        PRINTER.printTrace("Test kernel-injected pre-processor ended...");
    }

}
