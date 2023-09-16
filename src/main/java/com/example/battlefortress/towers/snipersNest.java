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

public class snipersNest extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNest.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestWeapon3.png");

    private static Image projectileLevel1 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestArrow1.png");
    private static Image projectileLevel2 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestArrow2.png");
    private static Image projectileLevel3 = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestArrow3.png");

    private static Image attackImpact = new Image("file:src/main/Images/Towers/Sniper's Nest/sniperNestImpact.png");
    private static String name = "Sniper's Nest";
    private static String description = "This tower shoots arrows from long range, with the size and damage of the arrow increasing as the tower is upgraded.";

    private int arrowDamage = 30;
    private int range = 0;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public snipersNest(int row, int col, int level) {
        super(row, col, level, name, description,level <= 2 ? 3+level : 7, true);
        this.level = level;
        updateTowerImages();
        range = level <= 2 ? 3+level : 7;
        arrowDamage = 30 + (level-1)*10;
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
        int layoutX = 0;
        int layoutY = 0;
        switch (level) {
            case 1:
                towerAttack = projectileLevel1;
                towerWeapon = new WritableImage(weaponLevel1.getPixelReader(), 0, 0, 64, 64);
                towerWeaponAttacking = weaponLevel1;
                layoutX = 0;
                layoutY = 24;
                break;
            case 2:
                towerAttack = projectileLevel2;
                towerWeapon = new WritableImage(weaponLevel2.getPixelReader(), 0, 0, 96, 96);
                towerWeaponAttacking = weaponLevel2;
                layoutX = -16;
                layoutY = -4;
                break;
            case 3:
                towerAttack = projectileLevel3;
                towerWeapon = new WritableImage(weaponLevel3.getPixelReader(), 0, 0, 96, 96);
                towerWeaponAttacking = weaponLevel3;
                layoutX = -16;
                layoutY = -12;
                break;
        }
//        towerWeaponAnimation(towerWeaponAttacking);
        weaponIV.setImage(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, attackImpact, layoutX, layoutY);
    }

    public void towerWeaponAnimation(Image towerWeapon) {
        super.setUpAttack(null);
        int size = level == 1 ? 64 : 96;
        AnimationTimer weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 350_000_000) {
                    if(frame == 1){
                        attackAni();
                    }
                    if (frame == 6) {
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

    public void attackAni(){
        ImageView attackIV = new ImageView();
        Image projectileImage = super.getTowerAttackImage();
        double weaponAngle = Math.toRadians(weaponIV.getRotate() + 90);
        int width = 4 + level*2;
        int height = level <= 2 ? 26 + (level -1)*8 : 37;
        int weaponSize = level == 1 ? 64 : 96;
        int maxFrame = level == 1 ? 2 : 3;
        double x = (Math.cos(weaponAngle) * (width/2));
        double y = (Math.sin(weaponAngle) * (height/2));
        startController.anchorPaneStatic.getChildren().add(attackIV);
        attackIV.setLayoutX(weaponIV.getLayoutX() + (weaponSize/2) - x - (width/2));
        attackIV.setLayoutY(weaponIV.getLayoutY() + (weaponSize/2) - y - (height/2));
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
                    int distance = 50;
                    totalDistance += distance;
                    double x =  (Math.cos(weaponAngle) * distance);
                    double y =  (Math.sin(weaponAngle) * distance);
                    ArrayList<monster> monsters = snipersNest.super.getMonstersInRange();
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
                    RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), snipersNest.super.getWeaponImageView());
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
        int width = 4 + level*2;
        int height = level <= 2 ? 26 + (level -1)*8 : 37;
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
                    impactIV.setLayoutX(finalLayoutX + 32- (width/2));
                    impactIV.setLayoutY(finalLayoutY + 32 - (height/2) );
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
    public void upgradeTower() {
        level++;
        super.setTowerLevel(level);
        updateTowerImages();
        range = level <= 2 ? 3+level : 7;
        super.setRange(range);
        arrowDamage = 30 + (level-1)*10;
    }
}
