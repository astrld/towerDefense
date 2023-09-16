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

public class rapidCrossbow extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbow.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowWeapon3.png");

    private static Image attackLevel1 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowArrow1.png");
    private static Image attackLevel2 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowArrow2.png");
    private static Image attackLevel3 = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowArrow3.png");

    private static Image attackImpact = new Image("file:src/main/Images/Towers/Rapid Crossbow/rapidCrossbowImpact.png");

    private static String name = "Rapid CrossBow";
    private static String description = "This tower shoots arrows at enemy troops, with the number of arrows per level increasing as the tower is upgraded.";
    private static int arrowDamage = 25;
    private int range = 1;
    private int level = 0;

    private ImageView weaponIV = new ImageView();

    public rapidCrossbow(int row, int col, int level) {
        super(row, col, level, name, description,2 + (level - 1),true);
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
        switch (level) {
            case 1:
                towerAttack = attackLevel1;
                towerWeapon = new WritableImage(weaponLevel1.getPixelReader(), 0, 0, 96, 96);
                towerWeaponAttacking = weaponLevel1;
                break;
            case 2:
                towerAttack = attackLevel2;
                towerWeapon = new WritableImage(weaponLevel2.getPixelReader(), 0, 0, 96, 96);
                towerWeaponAttacking = weaponLevel2;
                break;
            case 3:
                towerAttack = attackLevel3;
                towerWeapon = new WritableImage(weaponLevel3.getPixelReader(), 0, 0, 96, 96);
                towerWeaponAttacking = weaponLevel3;
                break;
        }
        weaponIV.setImage(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, attackImpact, -16, -8 * (level - 1));
    }

    public void upgradeTower() {
        level++;
        super.setTowerLevel(level);
        updateTowerImages();
        range = 2 + (level - 1);
        super.setRange(range);
    }

    public void towerWeaponAnimation(Image towerWeapon) {
        super.setUpAttack(null);
        AnimationTimer weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;
            int frame = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 50_000_000) {
                    if (frame == 1) {
                        attackAni();
                    }
                    if (frame == 5) {
                        this.stop();
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

    public void attackAni(){
        ImageView attackIV = new ImageView();
        Image projectileImage = super.getTowerAttackImage();
        double weaponAngle = Math.toRadians(weaponIV.getRotate() + 90);
        int size = level <= 2 ? 8 + (level -1)*7 : 22;
        double x = (Math.cos(weaponAngle) * (size/2));
        double y = (Math.sin(weaponAngle) * 20);
        startController.anchorPaneStatic.getChildren().add(attackIV);
        attackIV.setLayoutX(weaponIV.getLayoutX() + 48 - x - (size/2));
        attackIV.setLayoutY(weaponIV.getLayoutY() + 48 - y - 20);
        attackIV.setRotate(Math.toDegrees(weaponAngle) - 90);
        attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), 0, 0, size, 40));
        AnimationTimer shoot = new AnimationTimer() {
            int frame = 0;
            long lastUpdate = 0;
            int totalDistance = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 50_000_000){
                    attackIV.setImage(new WritableImage(projectileImage.getPixelReader(), frame * size, 0, size, 40));
                    frame++;
                    lastUpdate = l;
                    if(frame == 2){
                        frame = 0;
                    }
                    int distance = 15;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = rapidCrossbow.super.getMonstersInRange();
                    if (monsters != null && monsters.size() > 0) {
                        for (monster monster : monsters) {
                            if (monster != null) {
                                if (monster.getMonsterIV() != null) {
                                    if (attackIV.getBoundsInParent().intersects(monster.getMonsterIV().getBoundsInParent())) {
                                        monster.takeDamage(arrowDamage);
                                        this.stop();
                                        arrowImpact(attackIV, monster);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if(totalDistance >= range*64){
                        this.stop();
                        arrowImpact(attackIV, null);
                    }
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), rapidCrossbow.super.getWeaponImageView());
                    rotateTransition.setToAngle(0);
                    rotateTransition.play();
                    attackIV.setLayoutX(attackIV.getLayoutX() - x);
                    attackIV.setLayoutY(attackIV.getLayoutY() - y);
                }
            }
        };
        shoot.start();
    }

    private void arrowImpact(ImageView impactIV, monster monster){
        int size = level <= 2 ? 8 + (level -1)*7 : 22;
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
                    impactIV.setLayoutX(finalLayoutX + 32- (size/2));
                    impactIV.setLayoutY(finalLayoutY + 32 - 20 );
                    frame2++;
                    lastUpdate2 = l2;
                    if(frame2 == 4){
                        startController.anchorPaneStatic.getChildren().remove(impactIV);
                        this.stop();
                    }
                }
            }
        };
        impact.start();
    }
}
