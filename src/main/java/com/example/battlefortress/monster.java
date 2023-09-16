package com.example.battlefortress;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class monster {
    // Would be the super class of all troops which would have all the basic info each troop would have ie (health, speed, dmg etc)

    private int row;
    private int col;
    private double health;

    private double magicDefense;
    private double physicalDefense;
    private double speed;

    private Image downDeathAnimation;
    private Image downSpeed1;
    private Image downSpeed2;
    private Image upDeathAnimation;
    private Image upSpeed1;
    private Image upSpeed2;
    private ImageView monsterIV = new ImageView();
    private AnimationTimer animationTimer;

    private int height = 0;
    private int width = 0;

    private int maxFrame = 0;
    private int maxFrameDeath = 0;

    private double maxHealth;
    private AnimationTimer healthBarTimer;

    public monster(double health, double speed, double magicDefense, double physicalDefense, Image downDeathAnimation, Image downSpeed1, Image downSpeed2, Image upDeathAnimation, Image upSpeed1, Image upSpeed2, int width, int height, int maxFrame, int maxFrameDeath) {
        this.row = row;
        this.col = col;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.magicDefense = magicDefense;
        this.physicalDefense = physicalDefense;
        this.downDeathAnimation = downDeathAnimation;
        this.downSpeed1 = downSpeed1;
        this.downSpeed2 = downSpeed2;
        this.upDeathAnimation = upDeathAnimation;
        this.upSpeed1 = upSpeed1;
        this.upSpeed2 = upSpeed2;
        healthBar.setProgress(health / maxHealth);
        healthBar.setLayoutX(monsterIV.getLayoutX());
        healthBar.setLayoutY(monsterIV.getLayoutY() - 10);
        healthBar.setPrefWidth(64);
        healthBar.setPrefHeight(10);
        this.width = width;
        this.height = height;
        this.maxFrame = maxFrame;
        this.maxFrameDeath = maxFrameDeath;
        healthBar.setStyle("-fx-accent: lightgreen;");
        new Thread(() -> {
            healthBarTimer = new AnimationTimer() {
                long lastupdate = 0;
                @Override
                public void handle(long now) {
                    if(now - lastupdate >= 5_000_000){
                        healthBar.setLayoutX(monsterIV.getLayoutX());
                        healthBar.setLayoutY(monsterIV.getLayoutY() - 10);
                    }
                }
            };
            healthBarTimer.start();
        }).start();
        Platform.runLater(() -> {
            startController.anchorPaneStatic.getChildren().add(healthBar);
        });
    }

    private ProgressBar healthBar = new ProgressBar();

    private AnimationTimer flashBack;
    public void takeDamage(double damage) {
        if (health != Integer.MIN_VALUE) {
            if (damage <= health) {
                health -= damage;
                healthBar.setStyle("-fx-accent: red;");
                healthBar.setProgress(health / maxHealth);
                if(flashBack == null){
                    flashBack = new AnimationTimer() {
                        long lastupdate = 0;
                        @Override
                        public void handle(long now) {
                            if(now - lastupdate >= 250_000_000){
                                if(healthBar != null){
                                    healthBar.setStyle("-fx-accent: lightgreen;");
                                }
                                lastupdate = now;
                                this.stop();
                                flashBack = null;
                            }
                        }
                    };
                    flashBack.start();
                }
            } else {
                health = Integer.MIN_VALUE;
                animateDeath();
            }
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getHealth() {
        return health;
    }

    private int frame = 0;

    public int getFrame(){
        return frame;
    }

    public void setFrame() {
        frame = 0;
    }

    public void animate(){
       if(animationTimer != null){
           animationTimer.stop();
           animationTimer = null;
       }
       Image img = downSpeed2;
        Image finalImg = img;
        animationTimer = new AnimationTimer() {
            long lastupdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastupdate >= 100_000_000) {
                    if (frame == maxFrame) {
                        frame = 0;
                    }
                    WritableImage writableImage = new WritableImage(finalImg.getPixelReader(), frame * width, 0, width, height);
                    monsterIV.setImage(writableImage);
                    frame++;
                    lastupdate = now;
                }
            }
        };
        animationTimer.start();
    }

    private boolean isDead = false;

    public boolean isDead(){
        return isDead;
    }
    public void animateDeath(){
        isDead = true;
        healthBarTimer.stop();
        startController.anchorPaneStatic.getChildren().remove(healthBar);
        startController.monsterDeath(this);
        startController.gold += 5;
        healthBar = null;
        if(flashBack != null){
            flashBack.stop();
            flashBack = null;
        }
        if(animationTimer != null){
            animationTimer.stop();
            animationTimer = null;
        }
        Image img = downDeathAnimation;
        animationTimer = new AnimationTimer() {
            long lastupdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastupdate >= 150_000_000) {
                    if (frame == maxFrameDeath) {
                        this.stop();
                        startController.anchorPaneStatic.getChildren().remove(monsterIV);
                        monsterIV = null;
                        return;
                    }
                    WritableImage writableImage = new WritableImage(img.getPixelReader(), frame * width, 0, width, height);
                    monsterIV.setImage(writableImage);
                    frame++;
                    lastupdate = now;
                }
            }
        };
        animationTimer.start();
    }

    public ImageView getMonsterIV() {
        return monsterIV;
    }
    public void alertTowers(){
        for (Tower t : startController.towerArrayList) {
            double range = t.getRange() * 64;
            double towerLayoutX = t.getWeaponX();
            double towerLayoutY = t.getWeaponY();
            double monsterLayoutX = monsterIV.getLayoutX();
            double monsterLayoutY = monsterIV.getLayoutY();
            if (Math.sqrt(Math.pow(towerLayoutX - monsterLayoutX, 2) + Math.pow(towerLayoutY - monsterLayoutY, 2)) <= range) {
                t.attackMonster(this);
                t.inRangeMonster(this);
            } else {
                t.outOfRangeMonster(this);
            }
        }
    }
    private boolean isStun = false;
    private boolean isPoison = false;
    public boolean isStun(){
        return this.isStun;
    }

    public void setStun(boolean stun){
        this.isStun = stun;
    }

    public boolean isPoison(){
        return this.isPoison;
    }

    public void setPoison(boolean poison){
        this.isPoison = poison;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth() {
        return width;
    }


}
