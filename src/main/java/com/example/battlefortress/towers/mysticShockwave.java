package com.example.battlefortress.towers;

import com.example.battlefortress.Tower;
import com.example.battlefortress.monster;
import com.example.battlefortress.startController;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;

public class mysticShockwave extends Tower {

    private static Image towerImage = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwave.png");
    private static PixelReader towerImagePixelreader = towerImage.getPixelReader();

    private static Image weaponLevel1 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveWeapon1.png");
    private static Image weaponLevel2 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveWeapon2.png");
    private static Image weaponLevel3 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveWeapon3.png");

    private static Image projectileLevel1 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveProjectile1.png");
    private static Image projectileLevel2 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveProjectile2.png");
    private static Image projectileLevel3 = new Image("file:src/main/Images/Towers/Mystic Shockwave/mysticShockwaveProjectile3.png");

    private static String name = "Mystic Shockwave";
    private static String description = "This tower emits a pulse of mana that does area-of-effect damage to enemy troops. The size and damage of the pulse could increase with each level of the tower.";

    private static int shockwaveDamage = 2;
    private double range = 0;
    private int level = 0;

    private ImageView weaponIV = new ImageView();
    private int frame = 0;

    public mysticShockwave(int row, int col, int level) {
        super(row, col, level, name, description,level == 1 ? 2 : 2.5, false);
        this.level = level;
        updateTowerImages();
        range = level == 1 ? 2 : 2.5;
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
                towerWeapon = new WritableImage(weaponLevel1.getPixelReader(), 0, 0, 48, 48);
                towerWeaponAttacking = weaponLevel1;
                layoutX = 8;
                layoutY = 24;
                break;
            case 2:
                towerAttack = projectileLevel2;
                towerWeapon = new WritableImage(weaponLevel2.getPixelReader(), 0, 0, 48, 48);
                towerWeaponAttacking = weaponLevel2;
                layoutX = 8;
                layoutY = 8;
                break;
            case 3:
                towerAttack = projectileLevel3;
                towerWeapon = new WritableImage(weaponLevel3.getPixelReader(), 0, 0, 64, 64);
                towerWeaponAttacking = weaponLevel3;
                layoutX = 0;
                layoutY = -12;
                break;
        }
//        towerWeaponAnimation(towerWeaponAttacking);
        weaponIV.setImage(towerWeapon);
        final Image finalTowerWeaponAttacking = towerWeaponAttacking;
        super.setUpAttack(() -> {
            towerWeaponAnimation(finalTowerWeaponAttacking);
        });
        super.setTowerImages(towerImage, weaponIV, towerAttack, null, layoutX, layoutY);
    }

    public void towerWeaponAnimation(Image towerWeapon) {
        super.setUpAttack(null);
        int size = level <= 2 ? 48 : 64;
        AnimationTimer weaponAnimation = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    if(frame == 7){
                        attackAni();
                    }
                    if (frame == 10) {
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
        range = level == 1 ? 2 : 2.5;
        super.setRange(range);
        updateTowerImages();
    }

    public void attackAni() {
        ImageView attackIV = new ImageView();
        Image attackImage = super.getTowerAttackImage();
        int size = level == 1 ? 256 : 320;
        int weaponSize = level <= 2 ? 48 : 64;
        attackIV.setImage(new WritableImage(attackImage.getPixelReader(), 0, 0, size, size));
        startController.anchorPaneStatic.getChildren().add(attackIV);
        attackIV.setLayoutX(super.getWeaponX() + (weaponSize/2) - (size/2));
        attackIV.setLayoutY(super.getWeaponY() + (weaponSize/2) - (size/2));
        AnimationTimer impact = new AnimationTimer() {
            int frame2 = 0;
            long lastUpdate = 0;
            @Override
            public void handle(long l) {
                if(l - lastUpdate >= 75_000_000){
                    lastUpdate = l;
                    attackIV.setImage(new WritableImage(attackImage.getPixelReader(), frame2*size, 0, size, size));
                    if(frame2 == 6 + (level - 1)*5){
                        startController.anchorPaneStatic.getChildren().remove(attackIV);
                        this.stop();
                    }
                    frame2++;
                    ArrayList<monster> monsters = mysticShockwave.super.getMonstersInRange();
                    if(monsters != null && monsters.size() > 0){
                        for(int i = 0; i < monsters.size(); i++) {
                            monster m = monsters.get(i);
                            if (m != null && m.getMonsterIV() != null) {
                                if (attackIV.getBoundsInParent().intersects(m.getMonsterIV().getBoundsInParent())) {
                                    m.takeDamage(shockwaveDamage);
                                }
                            }
                        }
                    }

                }
            }
        };
        impact.start();
    }

}
