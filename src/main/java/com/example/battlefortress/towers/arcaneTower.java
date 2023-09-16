package com.example.battlefortress.towers;

import com.example.battlefortress.Tower;
import com.example.battlefortress.monster;
import com.example.battlefortress.startController;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import java.util.ArrayList;

public class arcaneTower extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTower.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon1.png");
    private static Image weaponLevel1Attacking = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon1Attacking.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon2.png");
    private static Image weaponLevel2Attacking = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon2Attacking.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon3.png");
    private static Image weaponLevel3Attacking = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerWeapon3Attacking.png");

    private static Image attackLevel1 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerLightning1.png");
    private static Image attackLevel2 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerLightning2_3.png");
    private static Image attackLevel3 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerLightning2_3.png");

    private static Image attackImpact1 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerImpact1.png");
    private static Image attackImpact2 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerImpact2.png");
    private static Image attackImpact3 = new Image("file:src/main/Images/Towers/Arcane Tower/arcaneTowerImpact3.png");

    private static String name = "Arcane Tower";
    private static String description = "This tower uses crystals to do electric damage to enemy troops. The amount of damage could increase with each level of the tower.";

    private static int lightningDamage = 30;
    private int range = 0;
    private int level = 0;

    private ImageView weaponImageView = new ImageView();
    private int frame = 0;

    public arcaneTower(int row, int col, int level) {
        super(row, col, level, name, description, 2 + (level - 1), false);
        this.level = level;
        updateTowerImages();
        range = 2 + (level - 1);
    }

    public static String getDescription() {
        return description;
    }

    public static Image getTowerDisplayImage() {
        return towerImage;
    }

    private void updateTowerImages() {
        WritableImage tempTowerImage = new WritableImage(towerImagePixelreader, (level - 1) * 64, 0, 64, 128);
        Image towerAttack = null;
        Image towerImage = tempTowerImage;
        Image towerWeapon = null;
        Image towerWeaponAttacking = null;
        Image attackImpact = null;
        int towerWeaponX = 0;
        int towerWeaponY = 0;
        switch (level) {
            case 1:
                towerAttack = attackLevel1;
                towerWeapon = weaponLevel1;
                attackImpact = attackImpact1;
                towerWeaponAttacking = weaponLevel1Attacking;
                towerWeaponX = 8;
                towerWeaponY = 24;
                break;
            case 2:
                towerAttack = attackLevel2;
                towerWeapon = weaponLevel2;
                attackImpact = attackImpact2;
                towerWeaponAttacking = weaponLevel2Attacking;
                towerWeaponX = 0;
                towerWeaponY = 0;
                break;
            case 3:
                towerAttack = attackLevel3;
                towerWeapon = weaponLevel3;
                attackImpact = attackImpact3;
                towerWeaponAttacking = weaponLevel3Attacking;
                towerWeaponX = 0;
                towerWeaponY = -8;
                break;
        }
        towerWeaponAnimation(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        Image finalTowerWeapon = towerWeapon;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking, finalTowerWeapon);
        });
        super.setTowerImages(towerImage, weaponImageView, towerAttack, attackImpact, towerWeaponX, towerWeaponY);
    }
    private AnimationTimer weaponAnimation;
    public void towerWeaponAnimation(Image towerWeapon) {
        int maxFrame = level <= 2 ? 4 + (level * 6) : 20;
        int size = level == 1 ? 48 : 64;
        if (weaponAnimation != null) {
            weaponAnimation.stop();
            weaponAnimation = null;
        }
        weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    if (frame == maxFrame) {
                        frame = 0;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * size, 0, size, size);
                    weaponImageView.setImage(tempWeaponImage);
                    frame++;
                    lastUpdate = now;
                }
            }
        };
        weaponAnimation.start();
    }
    public void towerWeaponAnimation(Image towerWeapon, Image nonAttack) {
        frame = 0;
        super.setUpAttack(null);
        int maxFrame = level <= 2 ? 15+ level : 19;
        int size = level == 1 ? 48 : 64;
        if (weaponAnimation != null) {
            weaponAnimation.stop();
            weaponAnimation = null;
        }
        weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    if(frame == (8 + level)){
                        attackAni();
                    }
                    if (frame == maxFrame) {
                        this.stop();
                        setUpAttack(() -> {
                            towerWeaponAnimation(towerWeapon,nonAttack);
                        });
                        towerWeaponAnimation(nonAttack);
                        frame = 0;
                        return;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * size, 0, size, size);
                    weaponImageView.setImage(tempWeaponImage);
                    frame++;
                    lastUpdate = now;
                }
            }
        };
        weaponAnimation.start();
    }

    public void upgradeTower() {
        level++;
        super.setTowerLevel(level);
        range = 2 + (level - 1);
        super.setRange(range);
        updateTowerImages();
    }

    public void attackAni(){
        Image lightning = super.getTowerAttackImage();
        ImageView lightningIV = new ImageView();
        monster tempMonster = super.getTempMonster();
        if(tempMonster == null) {
            return;
        }
        double x1 = tempMonster.getMonsterIV().getLayoutX();
        double y1 = tempMonster.getMonsterIV().getLayoutY();
        double x2 = weaponImageView.getLayoutX();
        double y2 = weaponImageView.getLayoutY();
        double angle = Math.atan2(y2 - y1, x2 - x1);
        lightningIV.setRotate(Math.toDegrees(angle) - 90);
        double weaponAngle = Math.toRadians(lightningIV.getRotate() + 90);
        double x = (Math.cos(weaponAngle) * 32);
        double y = (Math.sin(weaponAngle) * 32);
        startController.anchorPaneStatic.getChildren().add(lightningIV);
        int size = level == 1 ? 24 : 32;
        lightningIV.setLayoutX(weaponImageView.getLayoutX() + size - x - 16);
        lightningIV.setLayoutY(weaponImageView.getLayoutY() + size - y - 16);
        lightningIV.setRotate(Math.toDegrees(weaponAngle) - 90);
        lightningIV.setImage(new WritableImage(lightning.getPixelReader(), 0, 0, 32, 32));
        AnimationTimer attack = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 50_000_000){
                    lightningIV.setImage(new WritableImage(lightning.getPixelReader(), frame * 32, 0, 32, 32));
                    frame++;
                    lastUpdate = l;
                    if(frame == 4){
                        frame = 0;
                    }
                    int distance = 15;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = arcaneTower.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                ImageView monsterIV = monster.getMonsterIV();
                                if (monsterIV != null) {
                                    if (lightningIV.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(lightningDamage);
                                        this.stop();
                                        lightningImpact(lightningIV, monster);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        lightningImpact(lightningIV, null);
                    }
                    lightningIV.setLayoutX(lightningIV.getLayoutX() - x);
                    lightningIV.setLayoutY(lightningIV.getLayoutY() - y);
                }
            }
        };
        attack.start();
    }
    private monster tempMonster = null;
    private ImageView tempMonsterIV = null;
    private void lightningImpact(ImageView impactIV, monster monster){
        if(monster == null){
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        Image impactImage = super.getTowerImpactImage();
        int maxFrame = level <= 2 ? 3 + level : 7;
        long stunTime = level <= 2 ? 100_000_000L * level : 500_000_000;
        tempMonster = monster;
        tempMonsterIV = monster.getMonsterIV();
        boolean didStun = false;
        if(tempMonster != null){
            if(tempMonsterIV != null){
                if(!tempMonster.isStun()) {
                    tempMonster.setStun(true);
                    didStun = true;
                }
            }
        }
        if(!didStun){
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        AnimationTimer impact = new AnimationTimer() {
            long lastUpdate = 0;
            int frame = 0;
            @Override
            public void handle(long l) {
                tempMonster = monster;
                tempMonsterIV = monster.getMonsterIV();
                if(tempMonster == null || tempMonsterIV == null){
                    this.stop();
                    startController.anchorPaneStatic.getChildren().remove(impactIV);
                    return;
                }
                if(l - lastUpdate >= stunTime){
                    impactIV.setLayoutX(tempMonsterIV.getLayoutX());
                    impactIV.setRotate(0);
                    impactIV.setLayoutY(tempMonsterIV.getLayoutY() - 32 + 16);
                    impactIV.setImage(new WritableImage(impactImage.getPixelReader(), frame * 64, 0, 64, 64));
                    frame++;
                    lastUpdate = l;
                    if(frame == maxFrame){
                        startController.anchorPaneStatic.getChildren().remove(impactIV);
                        tempMonster.setStun(false);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }
}
