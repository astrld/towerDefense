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

public class thunderCatapult extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapult.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultWeapon3.png");

    private static Image projectileLevel1 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultProjectile1.png");
    private static Image projectileLevel2 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultProjectile2.png");
    private static Image projectileLevel3 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultProjectile3.png");

    private static Image attackImpact1 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultImpact1.png");
    private static Image attackImpact2 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultImpact2.png");
    private static Image attackImpact3 = new Image("file:src/main/Images/Towers/Thunder Catapult/thunderCatapultImpact3.png");

    private static String name = "Thunder Catapult";
    private static String description = "This tower launches electrified balls at enemy troops, with the number and size of the balls increasing as the tower is upgraded.";

    private int damage = 15;
    private int range = 1;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public thunderCatapult(int row, int col, int level) {
        super(row, col, level, name, description, 3 + (level - 1),true);
        this.level = level;
        updateTowerImages();
        range =  3 + (level - 1);
        damage = 15 + level;
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
        int layoutY = 0;
        switch (level) {
            case 1:
                towerAttack = projectileLevel1;
                towerWeapon = new WritableImage(weaponLevel1.getPixelReader(), 0, 0, 128, 128);
                towerWeaponAttacking = weaponLevel1;
                attackImpact = attackImpact1;
                layoutY = -12;
                break;
            case 2:
                towerAttack = projectileLevel2;
                towerWeapon = new WritableImage(weaponLevel2.getPixelReader(), 0, 0, 128, 128);
                towerWeaponAttacking = weaponLevel2;
                attackImpact = attackImpact2;
                layoutY = -22;
                break;
            case 3:
                towerAttack = projectileLevel3;
                towerWeapon = new WritableImage(weaponLevel3.getPixelReader(), 0, 0, 128, 128);
                towerWeaponAttacking = weaponLevel3;
                attackImpact = attackImpact3;
                layoutY = -30;
                break;
        }
        weaponIV.setImage(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, attackImpact, -32, layoutY);
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
                    if (frame == 16) {
                        this.stop();
                        setUpAttack(() -> {
                            towerWeaponAnimation(towerWeapon);
                        });
                        weaponIV.setImage(new WritableImage(towerWeapon.getPixelReader(), 0, 0, 128, 128));
                        frame = 0;
                        return;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * 128, 0, 128, 128);
                    weaponIV.setImage(tempWeaponImage);
                    frame++;
                    lastUpdate = now;
                }
            }
        };
        weaponAnimation.start();
    }

    public void attackAni(){
        ImageView attackIV = new ImageView();
        Image projectileImage = super.getTowerAttackImage();
        int width = level <= 2 ? 8 + (level-1)*7 : 10;
        int height = level <= 2 ? 8 + (level-1)*4 : 10;
        int maxFrame = level <= 2 ? 6 : 8;
        double weaponAngle = Math.toRadians(weaponIV.getRotate() + 90);
        double x = (Math.cos(weaponAngle) * (width/2));
        double y = (Math.sin(weaponAngle) * (height/2));
        startController.anchorPaneStatic.getChildren().add(attackIV);
        attackIV.setLayoutX(weaponIV.getLayoutX() + 64 - x - (width/2));
        attackIV.setLayoutY(weaponIV.getLayoutY() + 64 - y - (height/2));
        attackIV.setRotate(Math.toDegrees(weaponAngle) - 90);
        attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), 0, 0, width, height));
        AnimationTimer shoot = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 50_000_000){
                    attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), frame * width, 0, width, height));
                    frame++;
                    lastUpdate = l;
                    if(frame == maxFrame){
                        frame = 0;
                    }
                    int distance = 25;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = thunderCatapult.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                if (monster.getMonsterIV() != null) {
                                    if (attackIV.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(damage);
                                        this.stop();
                                        impactAnimation(attackIV, monster);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        impactAnimation(attackIV, null);
                    }
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), thunderCatapult.super.getWeaponImageView());
                    rotateTransition.setToAngle(0);
                    rotateTransition.play();
                    attackIV.setLayoutX(attackIV.getLayoutX() - x);
                    attackIV.setLayoutY(attackIV.getLayoutY() - y);
                }
            }
        };
        shoot.start();
    }
    private void impactAnimation(ImageView impactIV, monster monster){
        int width = level <= 2 ? 8 + (level-1)*7 : 10;
        int height = level <= 2 ? 8 + (level-1)*4 : 10;
        Image attackImpact = super.getTowerImpactImage();
        if(monster == null){
            startController.anchorPaneStatic.getChildren().remove(impactIV);
            return;
        }
        ImageView tempMonsterIV = monster.getMonsterIV();
        double layoutX = tempMonsterIV.getLayoutX();
        double layoutY = tempMonsterIV.getLayoutY();
        double finalLayoutY = layoutY;
        double finalLayoutX = layoutX;
        AnimationTimer impact = new AnimationTimer() {
            long lastUpdate2 = 0;
            int frame2 = 0;
            @Override
            public void handle(long l2) {
                if(l2 - lastUpdate2 >= 100_000_000){
                    impactIV.setImage(new WritableImage(attackImpact.getPixelReader(), frame2 * 64, 0, 64, 64));
                    impactIV.setLayoutX(finalLayoutX + 32 - (width/2));
                    impactIV.setLayoutY(finalLayoutY + 32 - (height/2));
                    frame2++;
                    lastUpdate2 = l2;
                    if(frame2 == 8){
                        startController.anchorPaneStatic.getChildren().remove(impactIV);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }
    public void upgradeTower() {
        level++;
        super.setTowerLevel(level);
        range =  3 + (level - 1);
        super.setRange(range);
        damage = 15 + level;
        updateTowerImages();
    }

}
