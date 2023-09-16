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

public class arcaneCannon extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannon.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannonWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannonWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannonWeapon3.png");

    private static Image projectileImage = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannonProjectile.png");

    private static Image attackImpact = new Image("file:src/main/Images/Towers/Arcane Cannon/arcaneCannonImpact.png");

    private static String name = "Arcane Cannon";
    private static String description = "This tower shoots cannonballs of mana, with the number and direction of the cannons increasing as the tower is upgraded.";

    private int cannonBallDamage = 5;
    private int range = 0;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public arcaneCannon(int row, int col, int level) {
        super(row, col, level, name, description,2 + (level - 1), true);
        this.level = level;
        updateTowerImages();
        range = 2 + (level - 1);
        cannonBallDamage += 5*level;
    }

    public static String getDescription() {
        return description;
    }

    public static Image getTowerDisplayImage() {
        return towerImage;
    }

    private void updateTowerImages() {
        WritableImage tempTowerImage = new WritableImage(towerImagePixelreader, (level - 1) * 64, 0, 64, 128);
        Image towerImage = tempTowerImage;
        Image towerWeapon = null;
        Image towerWeaponAttacking = null;
        int layoutY = 0;
        switch (level) {
            case 1:
                towerWeapon = new WritableImage(weaponLevel1.getPixelReader(), 0, 0, 64, 64);
                towerWeaponAttacking = weaponLevel1;
                layoutY = 24;
                break;
            case 2:
                towerWeapon = new WritableImage(weaponLevel2.getPixelReader(), 0, 0, 64, 64);
                towerWeaponAttacking = weaponLevel2;
                layoutY = 14;
                break;
            case 3:
                towerWeapon = new WritableImage(weaponLevel3.getPixelReader(), 0, 0, 64, 64);
                towerWeaponAttacking = weaponLevel3;
                layoutY = 6;
                break;
        }
        weaponIV.setImage(towerWeapon);
        Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, projectileImage, attackImpact, 0, layoutY);
    }

    public void towerWeaponAnimation(Image towerWeapon) {
        super.setUpAttack(null);
        int size = 64;
        int maxFrame = level <= 2 ? 5 + level : 9;
        AnimationTimer weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    if(frame == 4 && (level == 1 || level == 2 || level == 3)){
                        attackAni(1);
                    }
                    if(frame == 5 && (level == 2 || level == 3)){
                        attackAni(2);
                    }
                    if(frame == 6 && level == 3){
                        attackAni(3);
                    }
                    if(frame == 7 && level == 3){
                        attackAni(4);
                    }
                    if (frame == maxFrame) {
                        this.stop();
                        setUpAttack(() -> {
                            towerWeaponAnimation(towerWeapon);
                        });
                        weaponIV.setImage(new WritableImage(towerWeapon.getPixelReader(), 0, 0, size, size));
                        frame = 0;
                        return;
                    }
                    WritableImage tempWeaponImage = new WritableImage(towerWeapon.getPixelReader(), frame * size, 0, size, size);
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
        cannonBallDamage += 5*level;
        updateTowerImages();
    }

    public void attackAni(int direction){
        ImageView cannonball = new ImageView();
        double weaponAngle = Math.toRadians(weaponIV.getRotate() + 90);
        if (direction == 2) {
            weaponAngle += Math.PI; // rotate by 180 degrees
        } else if (direction == 3){
            weaponAngle += Math.PI * 1.5; // rotate by 270 degrees
        } else if (direction == 4){
            weaponAngle += Math.PI * 0.5; // rotate by 90 degrees
        }
        double x = (Math.cos(weaponAngle) * 32);
        double y = (Math.sin(weaponAngle) * 32);
        startController.anchorPaneStatic.getChildren().add(cannonball);
        cannonball.setLayoutX(weaponIV.getLayoutX() + 32 - x - 16);
        cannonball.setLayoutY(weaponIV.getLayoutY() + 32 - y - 16);
        cannonball.setRotate(Math.toDegrees(weaponAngle) - 90);
        cannonball.setImage(new WritableImage(projectileImage.getPixelReader(), 0, 0, 32, 32));
        double finalWeaponAngle = weaponAngle;
        AnimationTimer shoot = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 50_000_000){
                    cannonball.setImage(new WritableImage(projectileImage.getPixelReader(), frame * 32, 0, 32, 32));
                    frame++;
                    lastUpdate = l;
                    if(frame == 7){
                        frame = 0;
                    }
                    int distance = 15;
                    totalDistance += distance;
                    double x =  (Math.cos(finalWeaponAngle) * distance);
                    double y =  (Math.sin(finalWeaponAngle) * distance);
                    ArrayList<monster> monsters = arcaneCannon.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                if (monster.getMonsterIV() != null) {
                                    if (cannonball.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(cannonBallDamage);
                                        this.stop();
                                        double finalLayoutX = cannonball.getLayoutX() - 16;
                                        double finalLayoutY = cannonball.getLayoutY() - 16;
                                        cannonBallAnimation(finalLayoutX, finalLayoutY, cannonball);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        double finalLayoutX = cannonball.getLayoutX() - 16;
                        double finalLayoutY = cannonball.getLayoutY() - 16;
                        cannonBallAnimation(finalLayoutX, finalLayoutY, cannonball);
                    }
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), arcaneCannon.super.getWeaponImageView());
                    rotateTransition.setToAngle(0);
                    rotateTransition.play();
                    cannonball.setLayoutX(cannonball.getLayoutX() - x);
                    cannonball.setLayoutY(cannonball.getLayoutY() - y);
                }
            }
        };
        shoot.start();
    }

    private void cannonBallAnimation(double finalLayoutX, double finalLayoutY, ImageView cannonball){
        AnimationTimer impact = new AnimationTimer() {
            long lastUpdate2 = 0;
            int frame2 = 0;
            @Override
            public void handle(long l2) {
                if(l2 - lastUpdate2 >= 100_000_000){
                    cannonball.setImage(new WritableImage(attackImpact.getPixelReader(), frame2 * 64, 0, 64, 64));
                    cannonball.setLayoutX(finalLayoutX); cannonball.setLayoutY(finalLayoutY);
                    frame2++;
                    lastUpdate2 = l2;
                    if(frame2 == 8){
                        startController.anchorPaneStatic.getChildren().remove(cannonball);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }
}
