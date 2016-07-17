package info.masterfrog.pixelcat.demo.gameobject.builder;

import info.masterfrog.pixelcat.engine.exception.TransientGameException;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObject;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObjectManager;

import java.awt.*;

public class GameObjectBuilder {
    private static GameObjectBuilder instance = null;

    private GameObjectManager gameObjectManager;
    private Rectangle screenBounds;

    private GameObjectBuilder(GameObjectManager gameObjectManager, Rectangle screenBounds) {
        this.gameObjectManager = gameObjectManager;
        this.screenBounds = screenBounds;
    }

    public static GameObjectBuilder getInstance(GameObjectManager gameObjectManager, Rectangle screenBounds) {
        if (instance == null) {
            instance = new GameObjectBuilder(gameObjectManager, screenBounds);
        }

        return instance;
    }

    public GameObject buildCatHostScene() throws TransientGameException {
        return new CatHostSceneGenerator(gameObjectManager, screenBounds).generate().getGameObject();
    }

    public GameObject buildCatHost() throws TransientGameException {
        return new CatHostGenerator(gameObjectManager, screenBounds).generate().getGameObject();
    }
}
