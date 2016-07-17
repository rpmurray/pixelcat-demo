package info.masterfrog.pixelcat.demo.gameobject.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.masterfrog.pixelcat.engine.common.printer.Printer;
import info.masterfrog.pixelcat.engine.common.printer.PrinterFactory;
import info.masterfrog.pixelcat.engine.exception.TransientGameException;
import info.masterfrog.pixelcat.engine.hid.HIDEventEnum;
import info.masterfrog.pixelcat.engine.logic.animation.AnimationFactory;
import info.masterfrog.pixelcat.engine.logic.animation.AnimationSequence;
import info.masterfrog.pixelcat.engine.logic.camera.Camera;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObject;
import info.masterfrog.pixelcat.engine.logic.gameobject.GameObjectManager;
import info.masterfrog.pixelcat.engine.logic.gameobject.behavior.*;
import info.masterfrog.pixelcat.engine.logic.gameobject.feature.*;
import info.masterfrog.pixelcat.engine.logic.resource.Resource;
import info.masterfrog.pixelcat.engine.logic.resource.ResourceFactory;
import info.masterfrog.pixelcat.engine.logic.resource.SoundResource;
import info.masterfrog.pixelcat.engine.logic.resource.SpriteSheet;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CatHostGenerator {
    private GameObject gameObject;
    private Rectangle screenBounds;
    private Map<String, List<Resource>> resourceMap = new HashMap<>();
    private Map<String, AnimationSequence> animationMap = new HashMap<>();
    private Map<String, Camera> cameraMap = new HashMap<>();
    private Map<String, SoundResource> soundMap = new HashMap<>();
    private Map<String, BehaviorBinding> behaviorBindingMap = new HashMap<>();

    private static ResourceFactory resourceFactory = ResourceFactory.getInstance();
    private static AnimationFactory animationFactory = AnimationFactory.getInstance();

    private static Printer PRINTER = PrinterFactory.getInstance().createPrinter(CatHostGenerator.class);

    private static final String CAT_HOST_RESOURCES_BACK = "RESOURCE_BACK";
    private static final String CAT_HOST_RESOURCES_LEFT = "RESOURCE_LEFT";
    private static final String CAT_HOST_RESOURCES_RIGHT = "RESOURCE_RIGHT";
    private static final String CAT_HOST_ANIMATION_BACK = "ANIMATION_BACK";
    private static final String CAT_HOST_ANIMATION_LEFT = "ANIMATION_LEFT";
    private static final String CAT_HOST_ANIMATION_RIGHT = "ANIMATION_RIGHT";
    private static final String CAT_HOST_CAMERA_BACK = "CAMERA_BACK";
    private static final String CAT_HOST_CAMERA_LEFT = "CAMERA_LEFT";
    private static final String CAT_HOST_CAMERA_RIGHT = "CAMERA_RIGHT";
    private static final String CAT_HOST_SOUND_BOSS_BATTLE_MUSIC = "BOSS_BATTLE_MUSIC";
    private static final String CAT_HOST_BEHAVIOR_BINDING_ANIMATION_PLAY = "ANIMATION_PLAY";
    private static final String CAT_HOST_BEHAVIOR_BINDING_ANIMATION_STOP = "ANIMATION_STOP";
    private static final String CAT_HOST_BEHAVIOR_BINDING_CAMERA_STATE = "CAMERA_STATE";
    private static final String CAT_HOST_BEHAVIOR_BINDING_CAMERA_SWITCH = "CAMERA_SWITCH";
    private static final String CAT_HOST_BEHAVIOR_BINDING_PLAY_BOSS_BATTLE_MUSIC = "PLAY_BOSS_BATTLE_MUSIC";

    CatHostGenerator(GameObjectManager gameObjectManager, Rectangle screenBounds) throws TransientGameException {
        this.gameObject = gameObjectManager.createGameObject();
        this.screenBounds = screenBounds;
    }

    CatHostGenerator generate() throws TransientGameException {
        // register rendering properties
        gameObject.registerFeature(
            Renderable.create(new Point(screenBounds.width / 2 - 94, screenBounds.height / 2 - 258), 1, 4.0)
        );

        // define resources
        defineResources();

        // define animations
        defineAnimations();

        // define cameras
        defineCameras();

        // define sounds
        defineSounds();

        // register hid behaviors
        defineBehaviorBindings();

        // animate
        gameObject.getFeature(
            AnimationSequenceLibrary.class
        ).getCurrent().play();

        return this;
    }

    GameObject getGameObject() {
        return gameObject;
    }

    private Map<String, List<Resource>> defineResources() throws TransientGameException {
        // generate sprite sheet
        SpriteSheet pixelCatSpriteSheet = resourceFactory.createSpriteSheet(
            "pixel-cat-sprite-sheet-16px-by-14px-cells.png",
            16, 14,
            0, 0, 0, 0
        );

        // define resources
        resourceMap.put(
            CAT_HOST_RESOURCES_BACK,
            ImmutableList.of(
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(1, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(2, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(3, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(4, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(5, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(6, 0, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(7, 0, pixelCatSpriteSheet))
            )
        );
        resourceMap.put(
            CAT_HOST_RESOURCES_LEFT,
            ImmutableList.of(
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(1, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(2, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(3, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(4, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(5, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(6, 1, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(7, 1, pixelCatSpriteSheet))
            )
        );
        resourceMap.put(
            CAT_HOST_RESOURCES_RIGHT,
            ImmutableList.of(
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(0, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(1, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(2, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(3, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(4, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(5, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(6, 2, pixelCatSpriteSheet)),
                resourceFactory.createImageResource(resourceFactory.createSpriteResource(7, 2, pixelCatSpriteSheet))
            )
        );

        // register resources
        ResourceLibrary resourceLibrary = ResourceLibrary.create();
        for (int i = 0; i < 8; i++) {
            resourceLibrary.add(resourceMap.get(CAT_HOST_RESOURCES_BACK).get(i));
        }
        for (int i = 0; i < 8; i++) {
            resourceLibrary.add(resourceMap.get(CAT_HOST_RESOURCES_LEFT).get(i));
        }
        for (int i = 0; i < 8; i++) {
            resourceLibrary.add(resourceMap.get(CAT_HOST_RESOURCES_RIGHT).get(i));
        }
        gameObject.registerFeature(resourceLibrary);

        return resourceMap;
    }

    private Map<String, AnimationSequence> defineAnimations() throws TransientGameException {
        // define animation
        animationMap.put(CAT_HOST_ANIMATION_BACK, animationFactory.createAnimationSequence(100L));
        for (int i = 0; i < 8; i++) {
            animationMap.get(CAT_HOST_ANIMATION_BACK).addCel(resourceMap.get(CAT_HOST_RESOURCES_BACK).get(i).getId());
        }
        animationMap.put(CAT_HOST_ANIMATION_LEFT, animationFactory.createAnimationSequence(100L));
        for (int i = 0; i < 8; i++) {
            animationMap.get(CAT_HOST_ANIMATION_LEFT).addCel(resourceMap.get(CAT_HOST_RESOURCES_LEFT).get(i).getId());
        }
        animationMap.put(CAT_HOST_ANIMATION_RIGHT, animationFactory.createAnimationSequence(100L));
        for (int i = 0; i < 8; i++) {
            animationMap.get(CAT_HOST_ANIMATION_RIGHT).addCel(resourceMap.get(CAT_HOST_RESOURCES_RIGHT).get(i).getId());
        }

        // register animations
        gameObject.registerFeature(
            AnimationSequenceLibrary.create().add(
                animationMap.get(CAT_HOST_ANIMATION_BACK)
            ).add(
                animationMap.get(CAT_HOST_ANIMATION_LEFT)
            ).add(
                animationMap.get(CAT_HOST_ANIMATION_RIGHT)
            )
        );

        return animationMap;
    }

    private Map<String, Camera> defineCameras() throws TransientGameException {
        // define cameras
        cameraMap.put(CAT_HOST_CAMERA_BACK, Camera.create(animationMap.get(CAT_HOST_ANIMATION_BACK).getId(), AnimationSequence.class));
        cameraMap.put(CAT_HOST_CAMERA_LEFT, Camera.create(animationMap.get(CAT_HOST_ANIMATION_LEFT).getId(), AnimationSequence.class));
        cameraMap.put(CAT_HOST_CAMERA_RIGHT, Camera.create(animationMap.get(CAT_HOST_ANIMATION_RIGHT).getId(), AnimationSequence.class));

        // register cameras
        gameObject.registerFeature(
            CameraLibrary.create().add(
                cameraMap.get(CAT_HOST_CAMERA_BACK)
            ).add(
                cameraMap.get(CAT_HOST_CAMERA_LEFT)
            ).add(
                cameraMap.get(CAT_HOST_CAMERA_RIGHT)
            )
        );

        return cameraMap;
    }

    private Map<String, SoundResource> defineSounds() throws TransientGameException {
        // define sounds
        soundMap.put(CAT_HOST_SOUND_BOSS_BATTLE_MUSIC, resourceFactory.createSoundResource("zelda-boss-battle.ogg"));

        // register sounds
        gameObject.registerFeature(
            SoundLibrary.create().add(
                soundMap.get(CAT_HOST_SOUND_BOSS_BATTLE_MUSIC)
            )
        );

        return soundMap;
    }

    private Map<String, BehaviorBinding> defineBehaviorBindings() throws TransientGameException {
        // define logic-based behavior for camera switch
        ContainedValue orientation = new ContainedValue("back");
        BehaviorParameterGeneratorDefinition cameraSwitchTargetSelector = (inputs) -> {
            orientation.set(orientation.get().equals("back") ? "left" : orientation.get().equals("left") ? "right" : "back");
            String cameraId =
                orientation.get().equals("back") ?
                    cameraMap.get(CAT_HOST_CAMERA_LEFT).getId() :
                    orientation.get().equals("left") ?
                        cameraMap.get(CAT_HOST_CAMERA_RIGHT).getId() :
                        cameraMap.get(CAT_HOST_CAMERA_BACK).getId();
            BehaviorParameterId cameraParameter = BehaviorParameterFactory.getInstance().createIdParameter(cameraId);

            return cameraParameter;
        };
        BehaviorParameter cameraSwitchTarget = BehaviorParameterFactory.getInstance().createBehaviorParameterGenerator(
            cameraSwitchTargetSelector,
            ImmutableList.of()
        );

        // define hid event behavior bindings
        behaviorBindingMap.put(
            CAT_HOST_BEHAVIOR_BINDING_ANIMATION_PLAY,
            new BehaviorBindingImpl(
                new Behavior(BehaviorEnum.ANIMATION_PLAY),
                HIDEventEnum.P
            )
        );
        behaviorBindingMap.put(
            CAT_HOST_BEHAVIOR_BINDING_ANIMATION_STOP,
            new BehaviorBindingImpl(
                new Behavior(BehaviorEnum.ANIMATION_STOP),
                HIDEventEnum.S
            )
        );
        behaviorBindingMap.put(
            CAT_HOST_BEHAVIOR_BINDING_CAMERA_SWITCH,
            new BehaviorBindingImpl(
                new Behavior(
                    BehaviorEnum.CAMERA_SWITCH,
                    ImmutableSet.of(cameraSwitchTarget)
                ),
                HIDEventEnum.SPACE,
                1000L
            )
        );
        behaviorBindingMap.put(
            CAT_HOST_BEHAVIOR_BINDING_PLAY_BOSS_BATTLE_MUSIC,
            new BehaviorBindingImpl(
                new Behavior(
                    BehaviorEnum.SOUND_PLAY,
                    ImmutableSet.of(BehaviorParameterFactory.getInstance().createIdParameter(soundMap.get(CAT_HOST_SOUND_BOSS_BATTLE_MUSIC).getId()))
                ),
                HIDEventEnum.SPACE,
                1000L
            )
        );

        // register hid event behavior bindings
        gameObject.registerFeature(
            BehaviorBindingLibrary.create().add(
                (BehaviorBindingImpl) behaviorBindingMap.get(CAT_HOST_BEHAVIOR_BINDING_ANIMATION_PLAY)
            ).add(
                (BehaviorBindingImpl) behaviorBindingMap.get(CAT_HOST_BEHAVIOR_BINDING_ANIMATION_STOP)
            ).add(
                (BehaviorBindingImpl) behaviorBindingMap.get(CAT_HOST_BEHAVIOR_BINDING_CAMERA_SWITCH)
            )
        );

        return behaviorBindingMap;
    }

    private class ContainedValue {
        private Object value;

        ContainedValue(Object value) {
            this.value = value;
        }

        Object get() {
            return value;
        }

        void set(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }
}
