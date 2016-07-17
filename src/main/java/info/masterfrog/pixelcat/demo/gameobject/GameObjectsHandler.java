package info.masterfrog.pixelcat.demo.gameobject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.masterfrog.pixelcat.demo.enumeration.GameObjectHandle;
import info.masterfrog.pixelcat.demo.enumeration.GameObjectManagerHandle;
import info.masterfrog.pixelcat.demo.enumeration.LevelHandle;
import info.masterfrog.pixelcat.demo.gameobject.builder.GameObjectBuilder;
import info.masterfrog.pixelcat.engine.common.printer.Printer;
import info.masterfrog.pixelcat.engine.common.printer.PrinterFactory;
import info.masterfrog.pixelcat.engine.exception.GameErrorCode;
import info.masterfrog.pixelcat.engine.exception.TerminalErrorException;
import info.masterfrog.pixelcat.engine.exception.TerminalGameException;
import info.masterfrog.pixelcat.engine.exception.TransientGameException;
import info.masterfrog.pixelcat.engine.hid.HIDEventEnum;
import info.masterfrog.pixelcat.engine.kernel.KernelState;
import info.masterfrog.pixelcat.engine.kernel.KernelStatePropertyEnum;
import info.masterfrog.pixelcat.engine.logic.animation.AnimationFactory;
import info.masterfrog.pixelcat.engine.logic.animation.AnimationSequence;
import info.masterfrog.pixelcat.engine.logic.camera.Camera;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObject;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObjectManager;
import info.masterfrog.pixelcat.engine.logic.gameobject.behavior.*;
import info.masterfrog.pixelcat.engine.logic.gameobject.feature.*;
import info.masterfrog.pixelcat.engine.logic.physics.screen.ScreenBoundsHandlingTypeEnum;
import info.masterfrog.pixelcat.engine.logic.resource.Resource;
import info.masterfrog.pixelcat.engine.logic.resource.ResourceFactory;
import info.masterfrog.pixelcat.engine.logic.resource.SpriteSheet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameObjectsHandler {
    private KernelState kernelState;
    private Map<GameObjectHandle, GameObjectIdentifier> gameObjects;
    private Map<GameObjectManagerHandle, String> gameObjectManagerHandles;
    private Map<String, GameObjectManager> gameObjectManagers;
    private Map<LevelHandle, List<String>> levels;

    private static ResourceFactory resourceFactory = ResourceFactory.getInstance();
    private static AnimationFactory animationFactory = AnimationFactory.getInstance();
    private static Random randomGenerator = new Random();

    private static Printer PRINTER = PrinterFactory.getInstance().createPrinter(GameObjectsHandler.class);

    public GameObjectsHandler(KernelState kernelState) {
        this.kernelState = kernelState;
        this.gameObjects = new HashMap<>();
        this.gameObjectManagerHandles = new HashMap<>();
        this.gameObjectManagers = new HashMap<>();
        this.levels = new HashMap<>();
    }

    private GameObjectsHandler registerGameObject(GameObjectHandle handle, String objectId, String managerId) {
        // generate identifier
        GameObjectIdentifier gameObjectIdentifier = new GameObjectIdentifier(objectId, managerId);

        // store identifier against handle
        gameObjects.put(handle, gameObjectIdentifier);

        return this;
    }

    public GameObject getGameObject(GameObjectHandle gameObjectHandle) throws TransientGameException {
        // fetch identifier
        GameObjectIdentifier gameObjectIdentifier = gameObjects.get(gameObjectHandle);

        // fetch game object manager
        GameObjectManager gameObjectManager = getGameObjectManager(gameObjectIdentifier.getParentId());

        // fetch game object from game manager
        GameObject gameObject = gameObjectManager.get(gameObjectIdentifier.id);

        return gameObject;
    }

    public GameObjectsHandler registerGameObjectManager(GameObjectManagerHandle handle, GameObjectManager manager) {
        // store id against handle
        gameObjectManagerHandles.put(handle, manager.getId());

        // store manager against id
        gameObjectManagers.put(manager.getId(), manager);

        return this;
    }

    public GameObjectManager getGameObjectManager(String id) throws TransientGameException {
        // validate
        if (!gameObjectManagers.containsKey(id)) {
            throw new TransientGameException(GameErrorCode.LOGIC_ERROR);
        }

        // fetch manager from id
        GameObjectManager gameObjectManager = gameObjectManagers.get(id);

        return gameObjectManager;
    }

    public GameObjectManager getGameObjectManager(GameObjectManagerHandle handle) throws TransientGameException {
        // validate
        if (!gameObjectManagerHandles.containsKey(handle)) {
            throw new TransientGameException(GameErrorCode.LOGIC_ERROR);
        }

        // fetch id from handle
        String id = gameObjectManagerHandles.get(handle);

        // fetch manager from id
        GameObjectManager manager = getGameObjectManager(id);

        return manager;
    }

    private GameObjectsHandler registerGameObjectManagerList(LevelHandle handle, List<GameObjectManager> managers) {
        // setup
        List<String> managerIds = new ArrayList<>();

        // iterate through and build list of IDs from managers
        for (GameObjectManager manager : managers) {
            managerIds.add(manager.getId());
        }

        // store list of manager IDs
        levels.put(handle, managerIds);

        return this;
    }

    public List<GameObjectManager> getGameObjectManagerList(LevelHandle handle) throws TerminalErrorException {
        // setup
        List<GameObjectManager> managers = new ArrayList<>();

        try {
            // validate
            if (!levels.containsKey(handle)) {
                throw new TerminalGameException(GameErrorCode.LOGIC_ERROR);
            }

            // fetch list of manager IDs
            List<String> managerIds = levels.get(handle);

            // iterate through and build list of managers from IDs
            for (String managerId : managerIds) {
                try {
                    managers.add(getGameObjectManager(managerId));
                } catch (TransientGameException e) {
                    throw new TerminalGameException(e);
                }
            }
        } catch (TerminalGameException e) {
            throw new TerminalErrorException(ImmutableSet.of(e));
        }

        return managers;
    }

    public void init() throws TerminalErrorException {
        try {
            // common elements
            registerGameObjectManager(GameObjectManagerHandle.COMMON, generateCommonElements());

            // start screen elements
            registerGameObjectManager(GameObjectManagerHandle.START_SCREEN, generateStartScreenElements());

            // level one elements
            registerGameObjectManager(GameObjectManagerHandle.L1, generateLevelOneElements());

            // start screen
            registerGameObjectManagerList(
                LevelHandle.START_SCREEN,
                ImmutableList.of(
                    getGameObjectManager(GameObjectManagerHandle.COMMON),
                    getGameObjectManager(GameObjectManagerHandle.START_SCREEN)
                )
            );

            // level one
            registerGameObjectManagerList(
                LevelHandle.L1,
                ImmutableList.of(
                    getGameObjectManager(GameObjectManagerHandle.COMMON),
                    getGameObjectManager(GameObjectManagerHandle.L1)
                )
            );
        } catch (TransientGameException e) {
            throw new TerminalErrorException(ImmutableSet.of(e));
        }
    }

    private GameObjectManager generateCommonElements() throws TransientGameException {
        // init game object manager
        GameObjectManager gameObjectManager = GameObjectManager.create(5);

        // cat character
        GameObject catCharacter = generateCatCharacterGameObject(gameObjectManager);

        // add object to game objects manager
        gameObjectManager.add(catCharacter);

        // store game object ID for game processing
        registerGameObject(GameObjectHandle.CAT_CHARACTER, catCharacter.getId(), gameObjectManager.getId());

        return gameObjectManager;
    }

    private GameObject generateCatCharacterGameObject(GameObjectManager gameObjectManager) throws TransientGameException {
        // generate sprite sheet
        SpriteSheet nyanCatSpriteSheet = resourceFactory.createSpriteSheet(
            "cat_sprite_sheet.png",
            60, 30,
            0, 25, 0, 10
        );

        // create game object
        GameObject gameObject = gameObjectManager.createGameObject();

        // register rendering properties
        gameObject.registerFeature(
            Renderable.create(new Point(50, 50), 2)
        );

        // define resources
        List<Resource> catResourcesRight = ImmutableList.of(
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 0, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(1, 0, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(2, 0, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(3, 0, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(4, 0, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(5, 0, nyanCatSpriteSheet))
        );
        List<Resource> catResourcesLeft = ImmutableList.of(
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 1, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(1, 1, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(2, 1, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(3, 1, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(4, 1, nyanCatSpriteSheet)),
            resourceFactory.createImageResource(resourceFactory.createSpriteResource(5, 1, nyanCatSpriteSheet))
        );

        // register resources
        gameObject.registerFeature(
            ResourceLibrary.create().add(
                catResourcesRight.get(0)
            ).add(
                catResourcesRight.get(1)
            ).add(
                catResourcesRight.get(2)
            ).add(
                catResourcesRight.get(3)
            ).add(
                catResourcesRight.get(4)
            ).add(
                catResourcesRight.get(5)
            ).add(
                catResourcesLeft.get(0)
            ).add(
                catResourcesLeft.get(1)
            ).add(
                catResourcesLeft.get(2)
            ).add(
                catResourcesLeft.get(3)
            ).add(
                catResourcesLeft.get(4)
            ).add(
                catResourcesLeft.get(5)
            ).setCurrent(catResourcesLeft.get(0).getId())
        );

        // define animations
        AnimationSequence catAnimationRight = animationFactory.createAnimationSequence(
            100L
        ).addCel(
            catResourcesRight.get(0).getId()
        ).addCel(
            catResourcesRight.get(1).getId()
        ).addCel(
            catResourcesRight.get(2).getId()
        ).addCel(
            catResourcesRight.get(3).getId()
        ).addCel(
            catResourcesRight.get(4).getId()
        ).addCel(
            catResourcesRight.get(5).getId()
        );
        AnimationSequence catAnimationLeft = animationFactory.createAnimationSequence(
            100L
        ).addCel(
            catResourcesLeft.get(0).getId()
        ).addCel(
            catResourcesLeft.get(1).getId()
        ).addCel(
            catResourcesLeft.get(2).getId()
        ).addCel(
            catResourcesLeft.get(3).getId()
        ).addCel(
            catResourcesLeft.get(4).getId()
        ).addCel(
            catResourcesLeft.get(5).getId()
        );

        // register animations
        gameObject.registerFeature(
            AnimationSequenceLibrary.create().add(
                catAnimationRight
            ).add(
                catAnimationLeft
            ).setCurrent(catAnimationLeft.getId())
        );

        // define cameras
        Camera catCameraRight = Camera.create(catAnimationRight.getId(), catAnimationRight.getClass());
        Camera catCameraLeft = Camera.create(catAnimationLeft.getId(), catAnimationLeft.getClass());

        // register cameras
        gameObject.registerFeature(
            CameraLibrary.create().add(
                catCameraRight
            ).add(
                catCameraLeft
            ).setCurrent(catCameraLeft.getId())
        );

        // register physics
        gameObject.registerFeature(
            PhysicsBindingSet.create().add(
                ScreenBoundsHandlingTypeEnum.CENTER_RESOURCE
            )
        );

        // register hid behaviors
        BehaviorParameter catMovementMagnitude = BehaviorParameterFactory.getInstance().createMagnitudeParameter(2.0);
        gameObject.registerFeature(
            BehaviorBindingLibrary.create().add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.MOVE_UP,
                        ImmutableSet.of(catMovementMagnitude)
                    ), HIDEventEnum.PRIMARY_UP
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(BehaviorEnum.ANIMATION_PLAY), HIDEventEnum.PRIMARY_UP
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.MOVE_DOWN,
                        ImmutableSet.of(catMovementMagnitude)
                    ), HIDEventEnum.PRIMARY_DOWN
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(BehaviorEnum.ANIMATION_PLAY), HIDEventEnum.PRIMARY_DOWN
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.MOVE_LEFT,
                        ImmutableSet.of(catMovementMagnitude)
                    ), HIDEventEnum.PRIMARY_LEFT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(BehaviorEnum.ANIMATION_PLAY), HIDEventEnum.PRIMARY_LEFT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.CAMERA_SWITCH,
                        ImmutableSet.of(BehaviorParameterFactory.getInstance().createIdParameter(catCameraLeft.getId()))
                    ), HIDEventEnum.PRIMARY_LEFT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.MOVE_RIGHT,
                        ImmutableSet.of(catMovementMagnitude)
                    ), HIDEventEnum.PRIMARY_RIGHT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(BehaviorEnum.ANIMATION_PLAY), HIDEventEnum.PRIMARY_RIGHT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(
                        BehaviorEnum.CAMERA_SWITCH,
                        ImmutableSet.of(BehaviorParameterFactory.getInstance().createIdParameter(catCameraRight.getId()))
                    ), HIDEventEnum.PRIMARY_RIGHT
                )
            ).add(
                new BehaviorBindingImpl(
                    new Behavior(BehaviorEnum.ANIMATION_STOP), HIDEventEnum.PRIMARY_NO_DIRECTION
                )
            )
        );

        return gameObject;
    }

    private GameObjectManager generateLevelOneElements() throws TransientGameException {
        // init game object manager
        GameObjectManager gameObjectManager = GameObjectManager.create(((Rectangle) kernelState.getProperty(KernelStatePropertyEnum.SCREEN_BOUNDS)).height);

        // dynamically generate grass background
        generateGrassBGGameObjects(gameObjectManager);

        // dynamically generate bushes
        generateBushGameObjects(gameObjectManager);

        return gameObjectManager;
    }

    private void generateGrassBGGameObjects(GameObjectManager gameObjectManager) throws TransientGameException {
        // generate sprite sheet
        SpriteSheet grassBGSpriteSheet = resourceFactory.createSpriteSheet("grass_bg.png", 1950, 2);

        // generate resources
        Resource grassBGResourceGreen = resourceFactory.createSpriteResource(0, 0, grassBGSpriteSheet);
        Resource grassBGResourceBrown = resourceFactory.createSpriteResource(0, 1, grassBGSpriteSheet);

        // generate game objects
        for (Integer i = 0; i < ((Rectangle) kernelState.getProperty(KernelStatePropertyEnum.SCREEN_BOUNDS)).height / 2; i++) {
            // generate game object
            GameObject gameObject = generateGrassBGGameObject(
                gameObjectManager,
                i % 2 == 0 ? grassBGResourceGreen : grassBGResourceBrown,
                i
            );

            // add game object
            gameObjectManager.add(gameObject);
        }
    }

    private GameObject generateGrassBGGameObject(GameObjectManager gameObjectManager, Resource resource, Integer yIndex) throws TransientGameException {
        // generate game object
        GameObject gameObject = gameObjectManager.createGameObject();

        // register render properties
        gameObject.registerFeature(Renderable.create(new Point(0, yIndex * 2), 0));

        // register resources
        gameObject.registerFeature(ResourceLibrary.create().add(resource));

        return gameObject;
    }

    private void generateBushGameObjects(GameObjectManager gameObjectManager) throws TransientGameException {
        // generate sprite sheet
        SpriteSheet spriteSheet = resourceFactory.createSpriteSheet("bush_sprite_sheet.png", 18, 19, 1);

        // generate game objects
        for (Integer i = 0; i < 1000; i++) {
            // generate game object
            GameObject gameObject = generateBushObject(gameObjectManager, spriteSheet);

            // add game object
            gameObjectManager.add(gameObject);
        }
    }

    private GameObject generateBushObject(GameObjectManager gameObjectManager, SpriteSheet bushSpriteSheet)
            throws TransientGameException {
        // setup
        Rectangle screenBounds = (Rectangle) kernelState.getProperty(KernelStatePropertyEnum.SCREEN_BOUNDS);
        Integer x = randomGenerator.nextInt(screenBounds.width);
        Integer y = randomGenerator.nextInt(screenBounds.height);
        Integer animationKey = randomGenerator.nextInt(3);

        // generate game object
        GameObject bush = gameObjectManager.createGameObject();

        // register render properties
        bush.registerFeature(
            Renderable.create(new Point(x, y), y)
        );

        // generate resources
        Resource bushLeftResource = resourceFactory.createSpriteResource(0, 0, bushSpriteSheet);
        Resource bushCenterResource = resourceFactory.createSpriteResource(1, 0, bushSpriteSheet);
        Resource bushRightResource = resourceFactory.createSpriteResource(2, 0, bushSpriteSheet);

        // generate current resource + animation cel
        String currentCel;
        switch (animationKey) {
            case 0:
                currentCel = bushLeftResource.getId();
                break;
            case 1:
                currentCel = bushRightResource.getId();
                break;
            case 2:
            default:
                PRINTER.printWarning("Animation key miss on random generation... [" + animationKey + "]");
                currentCel = bushCenterResource.getId();
                break;
        }

        // register resources
        bush.registerFeature(
            ResourceLibrary.create().add(
                bushLeftResource
            ).add(
                bushCenterResource
            ).add(
                bushRightResource
            ).setCurrent(
                currentCel
            )
        );

        // generate animation
        AnimationSequence animation = AnimationFactory.getInstance().createAnimationSequence(
            (randomGenerator.nextInt(3) + 2) * 100L
        ).addCel(
            bushLeftResource.getId()
        ).addCel(
            bushCenterResource.getId()
        ).addCel(
            bushRightResource.getId()
        ).setCurrentCel(
            currentCel
        ).play();

        // register animation
        bush.registerFeature(
            AnimationSequenceLibrary.create().add(animation)
        );

        return bush;
    }

    private GameObjectManager generateStartScreenElements() throws TransientGameException {
        // setup
        Rectangle screenBounds = ((Rectangle) kernelState.getProperty(KernelStatePropertyEnum.SCREEN_BOUNDS));
        Font instructionsFont = new Font("Courier New", Font.BOLD, 12);
        String instructions1Text = "- Have fun moving the nyan cat with the arrow keys! -";
        Point instructions1Position = new Point(-220, 50);
        String instructions2Text = "Press enter to continue, escape to exit...";
        Point instructions2Position = new Point(-140, 65);

        // init game object manager
        GameObjectManager gameObjectManager = GameObjectManager.create(2);

        // init game object builder
        GameObjectBuilder gameObjectBuilder = GameObjectBuilder.getInstance(gameObjectManager, screenBounds);

        // cat host scene
        GameObject catHostScene = gameObjectBuilder.buildCatHostScene();

        // cat host
        GameObject catHost = gameObjectBuilder.buildCatHost();

        // title
        GameObject title = generateTitleGameObject(gameObjectManager, screenBounds);

        // subtitle
        Font subtitleFont = new Font("Courier New", Font.BOLD, 20);
        String subtitleText = "The 2D Sprite Base Video Game Engine";
        Point subtitlePosition = new Point(-220, -30);
        GameObject subtitle = generateTextGameObject(
            gameObjectManager,
            screenBounds,
            subtitleFont,
            subtitleText,
            subtitlePosition
        );

        // instructions1
        GameObject instructions1 = generateTextGameObject(
            gameObjectManager,
            screenBounds,
            instructionsFont,
            instructions1Text,
            instructions1Position
        );

        // instructions2
        GameObject instructions2 = generateTextGameObject(
            gameObjectManager,
            screenBounds,
            instructionsFont,
            instructions2Text,
            instructions2Position
        );

        // add game objects
        gameObjectManager.add(
            catHostScene
        ).add(
            catHost
        ).add(
            title
        ).add(
            subtitle
        ).add(
            instructions1
        ).add(
            instructions2
        );

        // store game object IDs for game processing
        registerGameObject(
            GameObjectHandle.CAT_HOST_SCENE, catHostScene.getId(), gameObjectManager.getId()
        ).registerGameObject(
            GameObjectHandle.CAT_HOST, catHost.getId(), gameObjectManager.getId()
        ).registerGameObject(
            GameObjectHandle.START_SCREEN_TITLE, title.getId(), gameObjectManager.getId()
        ).registerGameObject(
            GameObjectHandle.START_SCREEN_SUBTITLE, subtitle.getId(), gameObjectManager.getId()
        ).registerGameObject(
            GameObjectHandle.START_SCREEN_INS1, instructions1.getId(), gameObjectManager.getId()
        ).registerGameObject(
            GameObjectHandle.START_SCREEN_INS2, instructions2.getId(), gameObjectManager.getId()
        );

        return gameObjectManager;
    }

    private GameObject generateTitleGameObject(GameObjectManager gameObjectManager, Rectangle screenBounds) throws TransientGameException {
        // generate game object
        GameObject title = gameObjectManager.createGameObject();

        // register render properties
        title.registerFeature(
            Renderable.create(new Point(screenBounds.width / 2 - 95, screenBounds.height / 2 - 150), 1)
        );

        // generate sprite sheet
        SpriteSheet spriteSheet = resourceFactory.createSpriteSheet("pixelcat_title_sprite_sheet.png", 190, 80, 0, 0, 0, 5);

        // generate resources
        List<Resource> resources = ImmutableList.of(
            resourceFactory.createSpriteResource(0, 0, spriteSheet),
            resourceFactory.createSpriteResource(0, 1, spriteSheet),
            resourceFactory.createSpriteResource(0, 2, spriteSheet),
            resourceFactory.createSpriteResource(0, 3, spriteSheet),
            resourceFactory.createSpriteResource(0, 4, spriteSheet),
            resourceFactory.createSpriteResource(0, 5, spriteSheet),
            resourceFactory.createSpriteResource(0, 6, spriteSheet),
            resourceFactory.createSpriteResource(0, 7, spriteSheet)
        );

        // register resources
        title.registerFeature(
            ResourceLibrary.create().add(
                resources.get(0)
            ).add(
                resources.get(1)
            ).add(
                resources.get(2)
            ).add(
                resources.get(3)
            ).add(
                resources.get(4)
            ).add(
                resources.get(5)
            ).add(
                resources.get(6)
            ).add(
                resources.get(7)
            )
        );

        // generate animation
        AnimationSequence animation = animationFactory.createAnimationSequence(
            50L
        ).addCel(
            resources.get(0).getId()
        ).addCel(
            resources.get(1).getId()
        ).addCel(
            resources.get(2).getId()
        ).addCel(
            resources.get(3).getId()
        ).addCel(
            resources.get(4).getId()
        ).addCel(
            resources.get(5).getId()
        ).addCel(
            resources.get(6).getId()
        ).addCel(
            resources.get(7).getId()
        ).play();

        // register animation
        title.registerFeature(
            AnimationSequenceLibrary.create().add(
                animation
            )
        );

        return title;
    }

    private GameObject generateTextGameObject(GameObjectManager gameObjectManager,
                                              Rectangle screenBounds,
                                              Font font,
                                              String text,
                                              Point positionOffset)
                       throws TransientGameException {
        // generate game object
        GameObject textObject = gameObjectManager.createGameObject().registerFeature(
            Renderable.create(
                new Point(screenBounds.width / 2 + positionOffset.x, screenBounds.height / 2 + positionOffset.y),
                1
            )
        ).registerFeature(
            ResourceLibrary.create().add(
                resourceFactory.createTextResource(text, font)
            )
        );

        return textObject;
    }

    protected class GameObjectIdentifier {
        private String id;
        private String parentId;

        protected GameObjectIdentifier(String id, String parentId) {
            this.id = id;
            this.parentId = parentId;
        }

        public String getId() {
            return id;
        }

        public String getParentId() {
            return parentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GameObjectIdentifier)) {
                return false;
            }

            GameObjectIdentifier that = (GameObjectIdentifier) o;

            if (!id.equals(that.id)) {
                return false;
            }

            return parentId.equals(that.parentId);
        }

        @Override
        public String toString() {
            return "GameObjectIdentifier{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
        }
    }

}
