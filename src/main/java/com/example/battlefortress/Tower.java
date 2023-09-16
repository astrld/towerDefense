package com.example.battlefortress;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.EventListener;

public class Tower {
    private static Image level1Construction = new Image("file:src/main/Images/Misc/level1Construction.png");
    private static Image level2Construction = new Image("file:src/main/Images/Misc/level2Construction.png");
    private static Image level3Construction = new Image("file:src/main/Images/Misc/level3Construction.png");
    private static Image transistion = new Image("file:src/main/Images/Misc/transistion.png");
    private int row;
    private int col;
    private int level;
    private String towerName;
    private String towerDescription;
    private Image towerImage = null;
    private Image towerAttack = null;
    private Image attackImpact = null;

    private double range = 0;
    private ImageView weaponImageView;

    private Runnable attack;

    private boolean rotates = false;


    public Tower(int row, int col, int level, String towerName, String towerDescription, double range, boolean rotates) {
        this.row = row;
        this.col = col;
        this.level = level;
        this.towerName = towerName;
        this.towerDescription = towerDescription;
        this.range = range;
        this.rotates = rotates;
    }

    public void setTowerImages(Image towerImage, ImageView weaponIV, Image towerAttack, Image attackImpact, int towerWeaponX, int towerWeaponY) {
        this.towerImage = towerImage;
        this.towerAttack = towerAttack;
        this.attackImpact = attackImpact;
        updateTowerInMap(towerWeaponX, towerWeaponY, weaponIV);
    }

    public void setTowerLevel(int level) {
        this.level = level;
    }

    public void setRange(double range) {
        this.range = range;
    }


    public void updateTowerInMap(int towerWeaponX, int towerWeaponY, ImageView weaponIV) {
        if(startController.anchorPaneStatic.getChildren().contains(weaponImageView)) {
            startController.anchorPaneStatic.getChildren().remove(weaponImageView);
        }
        constructionAnimation(row, col, level);
        weaponImageView = weaponIV;
        new Thread(() -> {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {

                transitionEffect(row, col);

                PixelReader pixelReader = towerImage.getPixelReader();

                WritableImage bottomTower = new WritableImage(pixelReader, 0, 64, 64, 64);
                WritableImage topTower = new WritableImage(pixelReader, 0, 0, 64, 64);
                startController.tiles2DArrayTD[row][col].setBuildingImageView(bottomTower);
                startController.tiles2DArrayTD[row - 1][col].setBuildingImageView(topTower);
                startController.anchorPaneStatic.getChildren().add(weaponImageView);
                weaponImageView.setLayoutX(col * 64 + towerWeaponX);
                weaponImageView.setLayoutY((row - 1) * 64 + towerWeaponY);
            });
        }).start();
    }

    public void transitionEffect(int row, int col) {
        final ImageView[] transitionIV = {new ImageView()};
        startController.anchorPaneStatic.getChildren().add(transitionIV[0]);
        AnimationTimer animationTimer = new AnimationTimer() {
            long lastUpdate = 0;
            int i = 0;

            @Override
            public void handle(long now) {
                transitionIV[0].toFront();
                if (now - lastUpdate >= 50_000_000) {
                    if (i == 5) {
                        stop();
                        startController.anchorPaneStatic.getChildren().remove(transitionIV[0]);
                        transitionIV[0].setImage(null);
                        transitionIV[0] = null;
                        return;
                    }
                    WritableImage transitionImage = new WritableImage(transistion.getPixelReader(), i * 192, 0, 192, 256);
                    transitionIV[0].setImage(transitionImage);
                    transitionIV[0].setLayoutX((col - 1) * 64);
                    transitionIV[0].setLayoutY((row - 2) * 64);
                    lastUpdate = now;
                    i++;
                }
            }
        };
        animationTimer.start();
    }

    public void constructionAnimation(int row, int col, int level) { // 64 x 128
        Image constructionImage = null;
        switch (level) {
            case 1:
                constructionImage = level1Construction;
                break;
            case 2:
                constructionImage = level2Construction;
                break;
            case 3:
                constructionImage = level3Construction;
                break;
        }
        final ImageView[] constructionIV = {new ImageView()};
        startController.anchorPaneStatic.getChildren().add(constructionIV[0]);
        Image finalConstructionImage = constructionImage;
        AnimationTimer animationTimer = new AnimationTimer() {
            long lastUpdate = 0;
            int i = 0;
            int j = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 250_000_000) {
                    if (i == 5) {
                        i = 0;
                        j++;
                    }
                    if (j == 2) {
                        stop();
                        startController.anchorPaneStatic.getChildren().remove(constructionIV[0]);
                        constructionIV[0].setImage(null);
                        constructionIV[0] = null;
                        return;
                    }
                    WritableImage constructionImageWritable = new WritableImage(finalConstructionImage.getPixelReader(), i * 64, 0, 64, 128);
                    constructionIV[0].setImage(constructionImageWritable);
                    constructionIV[0].setLayoutX(col * 64);
                    constructionIV[0].setLayoutY((row - 1) * 64);
                    lastUpdate = now;
                    i++;
                }
            }
        };
        animationTimer.start();
        // delay till animation is done
    }

    public void setUpAttack(Runnable attack) {
        this.attack = attack;
    }
    public Runnable setUpAttackReturn(){
        return this.attack;
    }
    private monster tempMonster = null;
    private ArrayList<monster> monstersInRange = new ArrayList<>();
    private AnimationTimer rotate;
    public void attackMonster(monster monster) {
        if(attack != null) {
            attack.run();
            if (tempMonster == null) {
                tempMonster = monster;
                if(rotates && rotate == null){
                    rotate = new AnimationTimer() {
                        long lastUpdate = 0;
                        @Override
                        public void handle(long l) {
                            if(l - lastUpdate >= 5_000_000){
                                if(tempMonster == null){
                                    this.stop();
                                    rotate = null;
                                    return;
                                }
                                double x1 = tempMonster.getMonsterIV().getLayoutX();
                                double y1 = tempMonster.getMonsterIV().getLayoutY();
                                double x2 = weaponImageView.getLayoutX();
                                double y2 = weaponImageView.getLayoutY();
                                double angle = Math.atan2(y2 - y1, x2 - x1);
                                weaponImageView.setRotate(Math.toDegrees(angle) - 90);
                                double range = Tower.this.range * 64;
                                if(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > range){
                                    tempMonster = null;
                                    this.stop();
                                    rotate = null;
                                    RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), weaponImageView);
                                    rotateTransition.setToAngle(0);
                                    rotateTransition.play();
                                }
                            }
                        }
                    };
                    rotate.start();
                }
            }
        }
    }
    public void inRangeMonster(monster monster){
        if(!monstersInRange.contains(monster)) {
            monstersInRange.add(monster);
        }
    }
    public void outOfRangeMonster(monster monster) {
        if(monstersInRange.contains(monster)){
            monstersInRange.remove(monster);
        }
    }

    public void setTempMonsterNull(){
        this.tempMonster = null;
    }

    public void stopRotate(){
        if(rotate != null) {
            rotate.stop();
            rotate = null;
        }

    }

    public AnimationTimer getRotate(){
        return this.rotate;
    }

    public ArrayList<monster> getMonstersInRange(){
        return this.monstersInRange;
    }
    public monster getTempMonster(){
        return this.tempMonster;
    }

    public String toString() {
        return towerName;
    }

    public double getRange() {
        return range;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getWeaponX() {
        return weaponImageView.getLayoutX();
    }

    public double getWeaponY() {
        return weaponImageView.getLayoutY();
    }

    public ImageView getWeaponImageView(){
        return this.weaponImageView;
    }

    public Image getTowerAttackImage(){
        return this.towerAttack;
    }

    public Image getTowerImpactImage(){
        return this.attackImpact;
    }

    public Tower returnTower(){
        return this;
    }

    public int getLevel(){
        return this.level;
    }
}
