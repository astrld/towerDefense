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

public class manaBlaster extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlast.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon3.png");

    private static Image weaponLevel1Attacking = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon1Attacking.png");
    private static Image weaponLevel2Attacking = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon2Attacking.png");
    private static Image weaponLevel3Attacking = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastWeapon3Attacking.png");

    private static Image projectileLevel1 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastProjectile1.png");
    private static Image projectileLevel2 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastProjectile2.png");
    private static Image projectileLevel3 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastProjectile3.png");

    private static Image attackImpactLevel1 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastImpact1.png");
    private static Image attackImpactLevel2 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastImpact2.png");
    private static Image attackImpactLevel3 = new Image("file:src/main/Images/Towers/Mana Blaster/manaBlastImpact3.png");

    private static String name = "Mana Blaster";
    private static String description = "This tower shoots a blast of mana of a different color that explodes on impact. The damage and range of the explosion could increase with each level of the tower.";
    private static int blastDamage = 15;
    private int range = 1;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public manaBlaster(int row, int col, int level) {
        super(row, col, level, name, description,2 + (level - 1),false);
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
        switch (level) {
            case 1:
                towerAttack = projectileLevel1;
                towerWeapon = weaponLevel1;
                attackImpact = attackImpactLevel1;
                towerWeaponAttacking = weaponLevel1Attacking;
                break;
            case 2:
                towerAttack = projectileLevel2;
                towerWeapon = weaponLevel2;
                attackImpact = attackImpactLevel2;
                towerWeaponAttacking = weaponLevel2Attacking;
                break;
            case 3:
                towerAttack = projectileLevel3;
                towerWeapon = weaponLevel3;
                attackImpact = attackImpactLevel3;
                towerWeaponAttacking = weaponLevel3Attacking;
                break;
        }
        towerWeaponAnimation(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        Image finalTowerWeapon = towerWeapon;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking, finalTowerWeapon);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, attackImpact, -16, -8 * (level - 1));
    }
    private AnimationTimer weaponAnimation;
    public void towerWeaponAnimation(Image towerWeapon) {
        if (weaponAnimation != null) {
            weaponAnimation.stop();
            weaponAnimation = null;
        }
        weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 150_000_000) {
                    if (frame == 7) {
                        frame = 0;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * 96, 0, 96, 96);
                    weaponIV.setImage(tempWeaponImage);
                    frame++;
                    lastUpdate = now;
                }
            }
        };
        weaponAnimation.start();
    }
    public void towerWeaponAnimation(Image towerWeapon, Image nonAttack) {
        super.setUpAttack(null);
        frame = 0;
        if (weaponAnimation != null) {
            weaponAnimation.stop();
            weaponAnimation = null;
        }
        weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 50_000_000) {
                    if(frame == 25){
                        attackAni();
                    }
                    if (frame == 29) {
                        this.stop();
                        setUpAttack(() -> {
                            towerWeaponAnimation(towerWeapon, nonAttack);
                        });
                        towerWeaponAnimation(nonAttack);
                        frame = 0;
                        return;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * 96, 0, 96, 96);
                    weaponIV.setImage(tempWeaponImage);
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
    private monster tempMonster;
    private ImageView tempMonsterIV;
    private void attackAni(){
        Image attackImage = super.getTowerAttackImage();
        ImageView blastIV = new ImageView();
        tempMonster = super.getTempMonster();
        if(tempMonster == null) {
            return;
        }
        int attackSize = level * 32;
        double x1 = tempMonster.getMonsterIV().getLayoutX();
        double y1 = tempMonster.getMonsterIV().getLayoutY();
        double x2 = weaponIV.getLayoutX();
        double y2 = weaponIV.getLayoutY();
        double angle = Math.atan2(y2 - y1, x2 - x1);
        blastIV.setRotate(Math.toDegrees(angle) - 90);
        double weaponAngle = Math.toRadians(blastIV.getRotate() + 90);
        double x = (Math.cos(weaponAngle) * attackSize);
        double y = (Math.sin(weaponAngle) * attackSize);
        startController.anchorPaneStatic.getChildren().add(blastIV);
        blastIV.setLayoutX(weaponIV.getLayoutX() + 48 - x - (attackSize/2));
        blastIV.setLayoutY(weaponIV.getLayoutY() + 48 - y - (attackSize/2));
        blastIV.setRotate(Math.toDegrees(weaponAngle) - 90);
        blastIV.setImage(new WritableImage(attackImage.getPixelReader(), 0, 0, attackSize, attackSize));
        AnimationTimer attack = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 100_000_000){
                    blastIV.setImage(new WritableImage(attackImage.getPixelReader(), frame * attackSize, 0, attackSize, attackSize));
                    frame++;
                    lastUpdate = l;
                    if(frame == 12){
                        frame = 0;
                    }
                    int distance = level <= 2 ? (25 - level*5) : 10;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = manaBlaster.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                ImageView monsterIV = monster.getMonsterIV();
                                if (monsterIV != null) {
                                    if (blastIV.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(blastDamage);
                                        this.stop();
                                        blastAnimation(blastIV, monster);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        blastAnimation(blastIV, null);
                    }
                    blastIV.setLayoutX(blastIV.getLayoutX() - x);
                    blastIV.setLayoutY(blastIV.getLayoutY() - y);
                }
            }
        };
        attack.start();
    }
    private void blastAnimation(ImageView impactIV, monster monster){
        Image attackImpact = super.getTowerImpactImage();
        int size = level <= 2 ? 64 : 96;
        int maxFrame = level == 1 ? 10 : 12;
        if(monster == null){
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        tempMonster = monster;
        tempMonsterIV = monster.getMonsterIV();
        double layoutX = tempMonsterIV.getLayoutX();
        double layoutY = tempMonsterIV.getLayoutY();
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
                if(l - lastUpdate >= 50_000_000){
                    impactIV.setRotate(0);
                    impactIV.setImage(new WritableImage(attackImpact.getPixelReader(), frame * size, 0, size, size));
                    impactIV.setLayoutX(layoutX + 32- (size/2));
                    impactIV.setLayoutY(layoutY + 32 - (size/2) );
                    frame++;
                    lastUpdate = l;
                    if(frame == maxFrame){
                        startController.anchorPaneStatic.getChildren().remove(impactIV);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }

}
