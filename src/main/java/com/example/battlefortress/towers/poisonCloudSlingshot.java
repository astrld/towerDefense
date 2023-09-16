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

public class poisonCloudSlingshot extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshot.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotWeapon3.png");

    private static Image projectileLevel1 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotProjectile1.png");
    private static Image projectileLevel2 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotProjectile2.png");
    private static Image projectileLevel3 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotProjectile3.png");

    private static Image attackImpact1 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotImpact1.png");
    private static Image attackImpact2 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotImpact2.png");
    private static Image attackImpact3 = new Image("file:src/main/Images/Towers/Poison Cloud Slingshot/poisonCloudSlingshotImpact3.png");

    private static String name = "Poison Cloud Slingshot";
    private static String description = " This tower shoots spheres of different colors that explode on impact, with the size and damage of the explosion increasing as the tower is upgraded.";

    private static int cloudDamage = 10;
    private int range = 1;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public poisonCloudSlingshot(int row, int col, int level) {
        super(row, col, level, name, description,2 + (level - 1), true);
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

    public void attack() {
    }

    private void updateTowerImages() {
        WritableImage tempTowerImage = new WritableImage(towerImagePixelreader, (level - 1) * 64, 0, 64, 128);
        Image towerAttack = null;
        Image towerImage = tempTowerImage;
        Image towerWeapon = null;
        Image towerWeaponAttacking = null;
        Image attackImpact = null;
        int layoutX = 0;
        int layoutY = 0;
        switch (level) {
            case 1:
                towerAttack = projectileLevel1;
                towerWeaponAttacking = weaponLevel1;
                attackImpact = attackImpact1;
                break;
            case 2:
                towerAttack = projectileLevel2;
                towerWeaponAttacking = weaponLevel2;
                attackImpact = attackImpact2;
                break;
            case 3:
                towerAttack = projectileLevel3;
                towerWeaponAttacking = weaponLevel3;
                attackImpact = attackImpact3;
                break;
        }
        towerWeapon = new WritableImage(towerWeaponAttacking.getPixelReader(), 0, 0, 96, 96);
//        towerWeaponAnimation(towerWeaponAttacking);
        weaponIV.setImage(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, attackImpact, -16, -8 * (level - 1));
    }

    public void towerWeaponAnimation(Image towerWeapon) {
        super.setUpAttack(null);
        AnimationTimer weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    if(frame == 5){
                        attackAni();
                    }
                    if (frame == 7) {
                        this.stop();
                        attack();
                        setUpAttack(() -> {
                            towerWeaponAnimation(towerWeapon);
                        });
                        weaponIV.setImage(new WritableImage(towerWeapon.getPixelReader(), 0, 0, 96, 96));
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

    public void attackAni(){
        ImageView attackIV = new ImageView();
        Image projectileImage = super.getTowerAttackImage();
        double weaponAngle = Math.toRadians(weaponIV.getRotate() + 90);
        double x = (Math.cos(weaponAngle) * 10);
        double y = (Math.sin(weaponAngle) * 10);
        startController.anchorPaneStatic.getChildren().add(attackIV);
        attackIV.setLayoutX(weaponIV.getLayoutX() + 48 - x - 5);
        attackIV.setLayoutY(weaponIV.getLayoutY() + 48 - y - 5);
        attackIV.setRotate(Math.toDegrees(weaponAngle) - 90);
        attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), 0, 0, 10, 10));
        AnimationTimer shoot = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 50_000_000){
                    attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), frame * 10, 0, 10, 10));
                    frame++;
                    lastUpdate = l;
                    if(frame == 5){
                        frame = 0;
                    }
                    int distance = 15;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = poisonCloudSlingshot.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                if (monster.getMonsterIV() != null) {
                                    if (attackIV.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(cloudDamage);
                                        this.stop();
                                        cloudAnimation(attackIV, monster);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        cloudAnimation(attackIV, null);
                    }
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), poisonCloudSlingshot.super.getWeaponImageView());
                    rotateTransition.setToAngle(0);
                    rotateTransition.play();
                    attackIV.setLayoutX(attackIV.getLayoutX() - x);
                    attackIV.setLayoutY(attackIV.getLayoutY() - y);
                }
            }
        };
        shoot.start();
    }

    private void cloudAnimation(ImageView impactIV, monster monster) {
        if (monster == null) {
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        Image impactImage = super.getTowerImpactImage();
        int maxFrame = 4;
        long poisonTime = level <= 2 ? 500_000_000 - (level-1)*250_000_000L : 100_000_000;
        int poisonDamage = level <= 2 ? 2 : 5;
        final com.example.battlefortress.monster[] tempMonster = {monster};
        ImageView[] tempMonsterIV = {monster.getMonsterIV()};

        boolean didPoison = false;
        if (tempMonster[0] != null) {
            if (tempMonsterIV[0] != null) {
                if (!tempMonster[0].isPoison()) {
                    tempMonster[0].setPoison(true);
                    didPoison = true;
                }
            }
        }
        if (!didPoison) {
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        AnimationTimer impact = new AnimationTimer() {
            long lastUpdate = 0;
            int frame = 0;

            @Override
            public void handle(long l) {
                tempMonster[0] = monster;
                tempMonsterIV[0] = monster.getMonsterIV();
                impactIV.setLayoutX(tempMonsterIV[0].getLayoutX());
                impactIV.setRotate(0);
                impactIV.setLayoutY(tempMonsterIV[0].getLayoutY() - 32 + 16);
                if (tempMonster[0] == null || tempMonsterIV[0] == null) {
                    this.stop();
                    startController.anchorPaneStatic.getChildren().remove(impactIV);
                    return;
                }
                if (l - lastUpdate >= poisonTime) {
                    impactIV.setImage(new WritableImage(impactImage.getPixelReader(), frame * 64, 0, 64, 64));
                    frame++;
                    lastUpdate = l;
                    monster.takeDamage(poisonDamage);
                    if (frame == maxFrame) {
                        startController.anchorPaneStatic.getChildren().remove(impactIV);
                        tempMonster[0].setPoison(false);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }
}
