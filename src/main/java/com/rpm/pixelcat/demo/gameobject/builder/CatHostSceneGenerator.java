package com.rpm.pixelcat.demo.gameobject.builder;

import com.google.common.collect.ImmutableList;
import com.rpm.pixelcat.engine.common.printer.Printer;
import com.rpm.pixelcat.engine.common.printer.PrinterFactory;
import com.rpm.pixelcat.engine.exception.TransientGameException;
import com.rpm.pixelcat.engine.logic.gameobject.*;
import com.rpm.pixelcat.engine.logic.gameobject.feature.*;
import com.rpm.pixelcat.engine.logic.resource.Resource;
import com.rpm.pixelcat.engine.logic.resource.ResourceFactory;
import com.rpm.pixelcat.engine.logic.resource.SpriteSheet;

import java.awt.*;
import java.util.List;

class CatHostSceneGenerator {
    private GameObject gameObject;
    private Rectangle screenBounds;

    private static ResourceFactory resourceFactory = ResourceFactory.getInstance();
    private static Printer PRINTER = PrinterFactory.getInstance().createPrinter(CatHostSceneGenerator.class);

    CatHostSceneGenerator(GameObjectManager gameObjectManager, Rectangle screenBounds) throws TransientGameException {
        this.gameObject = gameObjectManager.createGameObject();
        this.screenBounds = screenBounds;
    }

    CatHostSceneGenerator generate() throws TransientGameException {
        // generate sprite sheet
        SpriteSheet pixelCatSceneSpriteSheet = resourceFactory.createSpriteSheet(
            "pixel-cat-background-sprite-sheet-32px-by-32px-cells.png",
            32, 32,
            0, 0, 0, 0
        );

        // register rendering properties
        gameObject.registerFeature(
            Renderable.create(new Point(screenBounds.width / 2 - 95, screenBounds.height / 2 - 278), 0, 4.0)
        );

        // define resources
        List<Resource> pixelCatScene = ImmutableList.of(
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 0, pixelCatSceneSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 1, pixelCatSceneSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 2, pixelCatSceneSpriteSheet))
        );

        // register resources
        gameObject.registerFeature(
            ResourceLibrary.create().add(
                pixelCatScene.get(0)
            ).add(
                pixelCatScene.get(1)
            ).add(
                pixelCatScene.get(2)
            )
        );

        return this;
    }

    GameObject getGameObject() {
        return gameObject;
    }
}
