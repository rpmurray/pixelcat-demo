package com.rpm.pixelcat.demo.main;

import com.rpm.pixelcat.engine.sound.SoundPlayerImpl;

public class SoundDemoMain {
    public static void main(String[] args) {
        String fileName = "zelda-boss-battle.ogg";
        try {
            new Thread(() -> {
                try {
                    SoundPlayerImpl.getInstance().init();
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(1);
                }
            });
            SoundPlayerImpl.getInstance().init().playSound(fileName);
            Thread.sleep(3000);
            SoundPlayerImpl.getInstance().init().playSound(fileName);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
