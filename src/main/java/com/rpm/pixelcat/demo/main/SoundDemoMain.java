package com.rpm.pixelcat.demo.main;

import com.rpm.pixelcat.engine.sound.SoundEngine;
import com.rpm.pixelcat.engine.sound.AudioPlayer;

public class SoundDemoMain {
    public static void main(String[] args) {
        String fileName = "zelda-boss-battle.ogg";
        try {
            new Thread(() -> {
                try {
                    SoundEngine.getInstance().init();
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(1);
                }
            });
            AudioPlayer.getInstance().SoundEngine.getInstance().init().playSound(fileName);
            Thread.sleep(3000);
            SoundEngine.getInstance().init().(fileName);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
