package com.example.battlefortress.monsters;

import com.example.battlefortress.monster;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class voidButterfly extends monster {
    private static int health = 120;
    private static int speed = 60;
    private static int magicDefense = 40;
    private static int physicalDefense = 50;

    private static Image downDeathAnimation = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflytDownDeathAnimation.png");
    private static Image downSpeed1 = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflyDownSpeed1.png");
    private static Image downSpeed2 = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflyDownSpeed2.png");

    private static Image upDeathAnimation = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflyUpDeathAnimation.png");
    private static Image upSpeed1 = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflyUpSpeed1.png");
    private static Image upSpeed2 = new Image("file:src/main/Images/Monsters/Void Butterfly/voidButterflyUpSpeed2.png");

    public voidButterfly() {
        super(health, speed, magicDefense, physicalDefense,upDeathAnimation,upSpeed1, upSpeed2, downDeathAnimation, downSpeed1, downSpeed2,64,64,3,12);
    }

    public int getSpeed() {
        return speed;
    }
}
