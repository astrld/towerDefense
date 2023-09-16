package com.example.battlefortress.monsters;

import com.example.battlefortress.monster;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class fireWasp extends monster {
    private static int health = 120;
    private static int speed = 60;
    private static int magicDefense = 40;
    private static int physicalDefense = 50;

    private static Image downDeathAnimation = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspDownDeathAnimation.png");
    private static Image downSpeed1 = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspDownSpeed1.png");
    private static Image downSpeed2 = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspDownSpeed2.png");

    private static Image upDeathAnimation = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspUpDeathAnimation.png");
    private static Image upSpeed1 = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspUpSpeed1.pn");
    private static Image upSpeed2 = new Image("file:src/main/Images/Monsters/Firewasp/fireWaspUpSpeed2.png");

    public fireWasp() {
        super(health, speed, magicDefense, physicalDefense,downDeathAnimation,downSpeed1, downSpeed2, upDeathAnimation, upSpeed1, upSpeed2,96,96,7,11);
    }

    public int getSpeed() {
        return speed;
    }
}
