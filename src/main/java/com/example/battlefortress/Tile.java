package com.example.battlefortress;

import com.example.battlefortress.towers.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Tile {
    private static Image mapImage = new Image("file:src/main/Images/Misc/gameMap.png"); // Image of the map
    private static Image collapseImage = new Image("file:src/main/Images/Misc/collapse.png"); // Image of collapse
    private static PixelReader pixelReader = mapImage.getPixelReader(); // Pixel reader for the map image
    private int row;
    private int col;
    private String type; // Type of tile (Grass, path.txt, etc.)
    private StackPane stackPane = new StackPane(); // StackPane for the tile
    private ImageView backgroundImageView = new ImageView(); // Image of the background of the tile (Grass, path.txt, etc.)
    private ImageView entityImageView = new ImageView(); // Image of the entity on the tile (Tower, enemy, etc.)
    private ImageView buildingImageView = new ImageView(); // Image of the building on the tile (Tower, enemy, etc.)
    private ImageView tempImageView = new ImageView(); // Temporary image view for the tile

    private Tower tower;
    private AnimationTimer goldAnimation;
    private AnimationTimer goldLabelAnimation;
    private AnimationTimer bankReduce;
    private static Image goldSpriteSheet = new Image("file:src/main/Images/Misc/coinSpriteSheet.png");
    private ImageView coinIV = new ImageView();

    public Tile(int row, int col, String type) {
        this.row = row;
        this.col = col;
        this.type = type;
        stackPane.getChildren().addAll(backgroundImageView, entityImageView, buildingImageView, tempImageView);
        WritableImage tileImage = new WritableImage(pixelReader, col * 64, row * 64, 64, 64);
        backgroundImageView.setImage(tileImage);
        coinIV.setLayoutX(850); coinIV.setLayoutY(250);
        coinIV.setFitWidth(150); coinIV.setFitHeight(150);

        stackPane.setOnMouseEntered(e -> {
            if (this.type.equals("grass")) {
                if(startController.buildBoolean) {
                    stackPane.setEffect(new Glow(.5));
                    startController.tiles2DArrayTD[row - 1][col].getStackPane().setEffect(new Glow(.5));
                    stackPane.setOnMouseClicked(e1 -> {
                        if (this.type.equals("grass")) {
                            if (startController.tiles2DArrayTD[row - 1][col].getType().equals("grass") || startController.tiles2DArrayTD[row - 1][col].getType().equals("barrier")) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Place tower?");
                                alert.setHeaderText("");
                                Circle circle = new Circle(45);
                                circle.setStroke(Color.BLACK);
                                circle.setStrokeWidth(2);
                                circle.setFill(new ImagePattern(startController.hammerImage));
                                alert.setGraphic(circle);
                                alert.getButtonTypes().clear();
                                alert.getButtonTypes().add(ButtonType.CLOSE);
                                DialogPane dialogPane = alert.getDialogPane();
                                AnchorPane ap = new AnchorPane();
                                dialogPane.getChildren().add(ap);
                                dialogPane.setPrefWidth(1000);
                                dialogPane.setPrefHeight(484);
                                Image bg = new Image("file:src/main/Images/Misc/buildTowerBackground.v3.png");
                                dialogPane.setStyle("-fx-background-image: url('" + bg.getUrl() + "')");
                                Label label = new Label("Do you want to place a tower here?");
                                ListView<String> listView = new ListView<>();
                                listView.getItems().add("Arcane Cannon");
                                listView.getItems().add("Arcane Tower");
                                listView.getItems().add("Mana Blaster");
                                listView.getItems().add("Mystic Shockwave");
                                listView.getItems().add("Poison Cloud Slingshot");
                                listView.getItems().add("Rapid Crossbow");
                                listView.getItems().add("Sniper's Nest");
                                listView.getItems().add("Thunder Catapult");
                                listView.getItems().add("Back");
                                Label userGold = new Label("Purse: " + startController.gold + "G");
                                userGold.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                ap.getChildren().add(userGold);
                                userGold.setLayoutX(800);
                                userGold.setLayoutY(50);
                                listView.setOnMouseClicked(e2 -> {
                                    String selected = listView.getSelectionModel().getSelectedItem();
                                    Label label2 = new Label(selected);
                                    Label description = new Label();
                                    Image image = null;
                                    ap.getChildren().clear();
                                    ap.getChildren().add(listView);
                                    ap.getChildren().add(label);
                                    ap.getChildren().add(userGold);
                                    if (selected == null) {
                                        return;
                                    }
                                    if (selected.equals("Back")) {
                                        listView.getSelectionModel().clearSelection();
                                    } else {
                                        label2.setStyle("-fx-font-size: 25px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                        ap.getChildren().add(label2);
                                        description.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                        switch (selected) {
                                            case "Arcane Cannon":
                                                description.setText(arcaneCannon.getDescription());
                                                image = arcaneCannon.getTowerDisplayImage();
                                                break;
                                            case "Arcane Tower":
                                                description.setText(arcaneTower.getDescription());
                                                image = arcaneTower.getTowerDisplayImage();
                                                break;
                                            case "Mana Blaster":
                                                description.setText(manaBlaster.getDescription());
                                                image = manaBlaster.getTowerDisplayImage();
                                                break;
                                            case "Mystic Shockwave":
                                                description.setText(mysticShockwave.getDescription());
                                                image = mysticShockwave.getTowerDisplayImage();
                                                break;
                                            case "Poison Cloud Slingshot":
                                                label2.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                                description.setText(poisonCloudSlingshot.getDescription());
                                                image = poisonCloudSlingshot.getTowerDisplayImage();
                                                break;
                                            case "Rapid Crossbow":
                                                description.setText(rapidCrossbow.getDescription());
                                                image = rapidCrossbow.getTowerDisplayImage();
                                                break;
                                            case "Sniper's Nest":
                                                description.setText(snipersNest.getDescription());
                                                image = snipersNest.getTowerDisplayImage();
                                                break;
                                            case "Thunder Catapult":
                                                label2.setStyle("-fx-font-size: 24px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                                description.setText(thunderCatapult.getDescription());
                                                image = thunderCatapult.getTowerDisplayImage();
                                                break;
                                        }
                                        description.setWrapText(true);
                                        label2.setLayoutX(450);
                                        label2.setLayoutY(159);
                                        description.setLayoutX(450);
                                        description.setLayoutY(300);
                                        ap.getChildren().add(description);
                                        description.setMinHeight(100);
                                        description.setMaxWidth(400);
                                        ImageView displayIV1 = new ImageView(new WritableImage(image.getPixelReader(), 0, 0, 64, 128));
                                        ImageView displayIV2 = new ImageView(new WritableImage(image.getPixelReader(), 64, 0, 64, 128));
                                        ImageView displayIV3 = new ImageView(new WritableImage(image.getPixelReader(), 128, 0, 64, 128));
                                        AtomicInteger lastGold = new AtomicInteger();
                                        Label goldLabel = new Label();
                                        goldLabel.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: #cfff00;");
                                        goldLabel.setLayoutY(400);
                                        displayIV1.setOnMouseClicked(e3 -> {
                                            ap.getChildren().remove(goldLabel);
                                            ap.getChildren().add(goldLabel);
                                            goldLabel.setLayoutX(878);
                                            if (lastGold.get() == 0) {
                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 0, 0, 800, 573));
                                                ap.getChildren().remove(coinIV);
                                                ap.getChildren().add(coinIV);
                                            } else if (lastGold.get() == 100) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 11;
                                                    int col = 4;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 0 && col == 0) {
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 0, 0, 800, 573));
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col--;
                                                            if (col == -1) {
                                                                col = 4;
                                                                row--;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            } else if (lastGold.get() == 150) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 23;
                                                    int col = 4;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 0 && col == 0) {
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 0, 0, 800, 573));
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col--;
                                                            if (col == -1) {
                                                                col = 4;
                                                                row--;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            }
                                            lastGold.set(50);
                                            goldLabel.setText("Gold: " + lastGold.get() + "G");
                                            goldLabelAni(goldLabel, 50);
                                            if (displayIV1.getEffect() == null) {
                                                if (e3.getButton() == MouseButton.PRIMARY) {
                                                    displayIV1.setEffect(new Glow(0.5));
                                                    displayIV2.setEffect(null);
                                                    displayIV3.setEffect(null);
                                                }
                                            } else {
                                                lastGold.set(0);
                                                ap.getChildren().remove(coinIV);
                                                ap.getChildren().remove(goldLabel);
                                                displayIV1.setEffect(null);
                                            }
                                        });
                                        displayIV2.setOnMouseClicked(e3 -> {
                                            ap.getChildren().remove(goldLabel);
                                            ap.getChildren().add(goldLabel);
                                            ap.getChildren().remove(coinIV);
                                            ap.getChildren().add(coinIV);
                                            goldLabel.setLayoutX(874);
                                            if (lastGold.get() == 50) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 0;
                                                    int col = 0;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 12) {
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 11 * 573, 800, 573));
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col++;
                                                            if (col == 5) {
                                                                col = 0;
                                                                row++;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            } else if (lastGold.get() == 150) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 23;
                                                    int col = 4;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 12) {
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 11 * 573, 800, 573));
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col--;
                                                            if (col == -1) {
                                                                col = 4;
                                                                row--;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            } else {
                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 11 * 573, 800, 573));
                                            }
                                            lastGold.set(100);
                                            goldLabel.setText("Gold: " + lastGold.get() + "G");
                                            goldLabelAni(goldLabel, 100);
                                            if (displayIV2.getEffect() == null) {
                                                if (e3.getButton() == MouseButton.PRIMARY) {
                                                    displayIV2.setEffect(new Glow(0.5));
                                                    displayIV1.setEffect(null);
                                                    displayIV3.setEffect(null);
                                                }
                                            } else {
                                                lastGold.set(0);
                                                ap.getChildren().remove(coinIV);
                                                ap.getChildren().remove(goldLabel);
                                                displayIV2.setEffect(null);
                                            }
                                        });
                                        displayIV3.setOnMouseClicked(e3 -> {
                                            ap.getChildren().remove(goldLabel);
                                            ap.getChildren().add(goldLabel);
                                            ap.getChildren().remove(coinIV);
                                            ap.getChildren().add(coinIV);
                                            goldLabel.setLayoutX(875);
                                            if (lastGold.get() == 50) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 0;
                                                    int col = 0;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 24) {
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 23 * 573, 800, 573));
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col++;
                                                            if (col == 5) {
                                                                col = 0;
                                                                row++;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            } else if (lastGold.get() == 100) {
                                                goldAnimation = new AnimationTimer() {
                                                    long lastUpdate = 0;
                                                    int row = 11;
                                                    int col = 4;

                                                    @Override
                                                    public void handle(long l) {
                                                        if (l - lastUpdate >= 20_000_000) {
                                                            if (row == 23) {
                                                                goldAnimation.stop();
                                                                goldAnimation = null;
                                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 23 * 573, 800, 573));
                                                                return;
                                                            }
                                                            coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), col * 800, row * 573, 800, 573));
                                                            col++;
                                                            if (col == 5) {
                                                                col = 0;
                                                                row++;
                                                            }
                                                            lastUpdate = l;
                                                        }
                                                    }
                                                };
                                                goldAnimation.start();
                                            } else {
                                                coinIV.setImage(new WritableImage(goldSpriteSheet.getPixelReader(), 4 * 800, 23 * 573, 800, 573));
                                            }
                                            lastGold.set(150);
                                            goldLabel.setText("Gold: " + lastGold.get() + "G");
                                            goldLabelAni(goldLabel, 150);
                                            if (displayIV3.getEffect() == null) {
                                                if (e3.getButton() == MouseButton.PRIMARY) {
                                                    displayIV3.setEffect(new Glow(0.5));
                                                    displayIV1.setEffect(null);
                                                    displayIV2.setEffect(null);
                                                }
                                            } else {
                                                lastGold.set(0);
                                                ap.getChildren().remove(coinIV);
                                                ap.getChildren().remove(goldLabel);
                                                displayIV3.setEffect(null);
                                            }
                                        });
                                        ap.getChildren().addAll(displayIV1, displayIV2, displayIV3);
                                        displayIV1.setLayoutX(1000 - 192);
                                        displayIV1.setLayoutY(159);
                                        displayIV2.setLayoutX(1000 - 128);
                                        displayIV2.setLayoutY(159);
                                        displayIV3.setLayoutX(1000 - 64);
                                        displayIV3.setLayoutY(159);
                                        Button button = new Button("Place Tower");
                                        button.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 12px;");
                                        ap.getChildren().add(button);
                                        button.setLayoutX(417.5);
                                        button.setLayoutY(411);
                                        button.setOnMouseClicked(e4 -> {
                                            if (displayIV1.getEffect() != null || displayIV2.getEffect() != null || displayIV3.getEffect() != null) {
                                                if (startController.gold >= lastGold.get()) {
                                                    bankReduce = new AnimationTimer() {
                                                        long lastUpdate = 0;
                                                        int amountReduced = 0;

                                                        @Override
                                                        public void handle(long l) {
                                                            if (l - lastUpdate >= 25_000_000) {
                                                                startController.changeGold(-5);
                                                                amountReduced += 5;
                                                                userGold.setText("Purse: " + startController.gold + "G");
                                                                if (amountReduced == lastGold.get()) {
                                                                    bankReduce.stop();
                                                                    bankReduce = null;
                                                                    goldLabelAni(goldLabel, lastGold.get());
                                                                    Thread thread = new Thread(() -> {
                                                                        try {
                                                                            Thread.sleep(500);
                                                                        } catch (InterruptedException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        Platform.runLater(alert::close);
                                                                        Thread thread1 = new Thread(() -> {
                                                                            try {
                                                                                Thread.sleep(500);
                                                                            } catch (InterruptedException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            Platform.runLater(() -> {
                                                                                startController.createTower(row, col, selected, lastGold.get() / 50, Tile.this);
                                                                                stackPane.setOnMouseClicked(null);
                                                                                stackPane.setOnMouseEntered(null);
                                                                            });
                                                                        });
                                                                        thread1.start();
                                                                    });
                                                                    thread.start();
                                                                    return;
                                                                }
                                                                lastUpdate = l;
                                                            }
                                                        }
                                                    };
                                                    bankReduce.start();
                                                    this.type = "tower1";
                                                    startController.tiles2DArrayTD[row-1][col].setType("tower2");
                                                }
                                            } else {
                                                button.setOnMouseClicked(e5 -> {
                                                    startController.displayError("You must choose a tower to place!", "Click on a tower level!");
                                                });
                                            }
                                        });
                                    }
                                });
                                ap.getChildren().addAll(listView);
                                listView.setPrefHeight(200);
                                listView.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: lightblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 5px; -fx-background-radius: 8px;");
                                Platform.runLater(() -> {
                                    label.setLayoutX(500 - label.getWidth() / 2);
                                    label.setLayoutY(50);
                                    listView.setLayoutX(150);
                                    listView.setLayoutY(50 + label.getLayoutY() + label.getHeight());
                                });
                                label.setStyle("-fx-font-size: 25px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
                                ap.getChildren().add(label);
                                // if alert is closed
                                alert.showAndWait();
                            } else {
                                startController.displayError("Can't place tower here!", "");
                            }
                        }
                    });
                }
            } else if (this.type.equals("rock") || this.type.equals("tree")) {
                if(startController.deleteBoolean){
                    stackPane.setEffect(new Glow(.5));
                    stackPane.setOnMouseClicked(ee -> {
                        if(startController.gold >= 25){
                            this.backgroundImageView.setImage(new Image("file:src/main/Images/Misc/grass.png"));
                            this.type = "grass";
                            startController.changeGold(-25);
                            ImageView demolishIV = new ImageView();
                            demolishIV.setImage(new WritableImage(collapseImage.getPixelReader(),0,0,256,192));
                            startController.anchorPaneStatic.getChildren().add(demolishIV);
                            demolishIV.setLayoutX(this.col * 64 + 32 - 128);
                            demolishIV.setLayoutY(this.row * 64 + 32 - 96);
                            AnimationTimer demo = new AnimationTimer() {
                                int frameDemo = 0;
                                long lastUpdate = 0;
                                @Override
                                public void handle(long l) {
                                    if(l - lastUpdate >= 50_000_000){
                                        demolishIV.setImage(new WritableImage(collapseImage.getPixelReader(),frameDemo*256,0,256,192));
                                        frameDemo++;
                                        if(frameDemo == 12){
                                            startController.anchorPaneStatic.getChildren().remove(demolishIV);
                                            stackPane.setOnMouseClicked(null);
                                            stackPane.setEffect(null);
                                            this.stop();
                                            return;
                                        }
                                        lastUpdate = l;
                                    }
                                }
                            };
                            demo.start();
                        } else {
                            startController.displayError("You don't have enough gold!", "You need 25G to destroy a rock or tree!");
                        }
                    });
                }
            } else if(this.type.equals("tower1") || this.type.equals("tower2")){
                if(startController.deleteBoolean){
                    stackPane.setOnMouseClicked(ee ->{
                        System.out.println(this.type);
                        startController.towerArrayList.remove(this.tower);
                        startController.anchorPaneStatic.getChildren().remove(this.tower.getWeaponImageView());
                        startController.changeGold(25);
                        ImageView demolishIV = new ImageView();
                        demolishIV.setImage(new WritableImage(collapseImage.getPixelReader(),0,0,256,192));
                        startController.anchorPaneStatic.getChildren().add(demolishIV);
                        demolishIV.setLayoutX(this.col * 64 + 32 - 128);
                        demolishIV.setLayoutY((this.row+1) * 64 + 32 - 96);
                        AnimationTimer demo = new AnimationTimer() {
                            int frameDemo = 0;
                            long lastUpdate = 0;
                            @Override
                            public void handle(long l) {
                                if(l - lastUpdate >= 75_000_000){
                                    demolishIV.setImage(new WritableImage(collapseImage.getPixelReader(),frameDemo*256,0,256,192));
                                    frameDemo++;
                                    if(frameDemo == 12){
                                        startController.anchorPaneStatic.getChildren().remove(demolishIV);
                                        stackPane.setOnMouseClicked(null);
                                        stackPane.setEffect(null);
                                        this.stop();
                                        return;
                                    }
                                    lastUpdate = l;
                                }
                            }
                        };
                        demo.start();
                        if(this.type.equals("tower1")){
                            System.out.println("tower1");
                            startController.tiles2DArrayTD[row-1][col].setType("grass");
                            startController.tiles2DArrayTD[row-1][col].setBuildingImageView(null);
                            startController.tiles2DArrayTD[row-1][col].setTower(null);
                        } else {
                            System.out.println("tower2");
                            startController.tiles2DArrayTD[row+1][col].setType("grass");
                            startController.tiles2DArrayTD[row+1][col].setBuildingImageView(null);
                            startController.tiles2DArrayTD[row+1][col].setTower(null);
                        }
                        this.buildingImageView.setImage(null);
                        this.type = "grass";
                        this.tower = null;
                        stackPane.setOnMouseClicked(null);
                    });
                }
                if(startController.upgradeBoolean){
                    stackPane.setOnMouseClicked(ee -> {
                        if(this.tower.getLevel() == 3){
                            startController.displayError("Tower is already max level!", "");
                            return;
                        }
                        if(startController.gold >= 50) {
                            if(this.tower.setUpAttackReturn() != null) {
                                if (this.tower instanceof arcaneCannon) {
                                    ((arcaneCannon) this.tower).upgradeTower();
                                } else if (this.tower instanceof arcaneTower) {
                                    ((arcaneTower) this.tower).upgradeTower();
                                } else if (this.tower instanceof manaBlaster) {
                                    ((manaBlaster) this.tower).upgradeTower();
                                } else if (this.tower instanceof mysticShockwave) {
                                    ((mysticShockwave) this.tower).upgradeTower();
                                } else if (this.tower instanceof poisonCloudSlingshot) {
                                    ((poisonCloudSlingshot) this.tower).upgradeTower();
                                } else if (this.tower instanceof rapidCrossbow) {
                                    ((rapidCrossbow) this.tower).upgradeTower();
                                } else if (this.tower instanceof snipersNest) {
                                    ((snipersNest) this.tower).upgradeTower();
                                } else if (this.tower instanceof thunderCatapult) {
                                    ((thunderCatapult) this.tower).upgradeTower();
                                } else {
                                    System.out.println("No tower found");
                                }
                                startController.changeGold(-50);
                            } else {
                                startController.displayError("Can't upgrade while tower is attacking", "");
                            }
                        } else {
                            startController.displayError("You don't have enough gold!", "You need 50G to upgrade a tower!");
                        }
                        stackPane.setOnMouseClicked(null);
                    });
                }
            }
        });

        stackPane.setOnMouseExited(e -> {
            if (this.type.equals("grass") || this.type.equals("rock") || this.type.equals("tree")) {
                startController.tiles2DArrayTD[row-1][col].getStackPane().setEffect(null);
                stackPane.setEffect(null);
            } else {
                stackPane.setEffect(null);
            }
        });


    }

    private void goldLabelAni(Label label, int gold){
        if(startController.gold < gold){
            goldLabelAnimation = new AnimationTimer() {
                long lastUpdate = 0;
                int c = 0;
                @Override
                public void handle(long l) {
                    if(l - lastUpdate >= 250_000_000){
                        if(c % 2 == 0) {
                            label.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: #cfff00;");
                        } else {
                            label.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: #ff0000;");
                        }
                        c++;
                        lastUpdate = l;
                    }
                }
            };
            goldLabelAnimation.start();
        } else {
            if(goldLabelAnimation != null) {
                goldLabelAnimation.stop();
                goldLabelAnimation = null;
            }
            label.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 5px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: #cfff00;");
        }
    }

    public void setBuildingImageView(Image image) {
        buildingImageView.setImage(image);
    }

    public void setBackgroundImageView(Image image) {
        backgroundImageView.setImage(image);
    }
    private static Image goldCoinImage = new Image("file:src/main/Images/Misc/Full Coins.png");
    int goldFrame = 0;
    int rotations = 0;
    boolean goldCoinAnimation = false;
    public void setFishGold() {
        stackPane.setOnMouseClicked(e -> {
            if (goldCoinAnimation) {
                return;
            }
            ImageView tempiv = new ImageView();
            startController.anchorPaneStatic.getChildren().add(tempiv);
            tempiv.setLayoutX(col * 64 + 32 - 8);
            tempiv.setLayoutY(row * 64 + 32 - 8);
            startController.changeGold(10);
            AnimationTimer goldCoin = new AnimationTimer() {
                long lastUpdate = 0;

                @Override
                public void handle(long l) {
                    if (l - lastUpdate >= 50_000_000) {
                        goldCoinAnimation = true;
                        PixelReader pixelReader = goldCoinImage.getPixelReader();
                        if (goldFrame == 8) {
                            goldFrame = 0;
                            rotations++;
                        }
                        if (rotations == 2) {
                            stackPane.setOnMouseClicked(null);
                            this.stop();
                            startController.anchorPaneStatic.getChildren().remove(tempiv);
                            tempiv.setImage(null);
                            goldFrame = 0;
                            rotations = 0;
                            goldCoinAnimation = false;
                            return;
                        }
                        WritableImage writableImage = new WritableImage(pixelReader, goldFrame * 16, 0, 16, 16);
                        tempiv.setImage(writableImage);
                        goldFrame++;
                        lastUpdate = l;
                    }
                }
            };
            goldCoin.start();
        });
    }

    public void setStackPaneClickNull() {
        stackPane.setOnMouseClicked(null);
    }

    public StackPane getStackPane() {
        return stackPane;
    } // Returns the stack pane of the tile

    public Object getType() {
        return type;
    } // Returns the type of the tile

    public void setType(String type) {
        this.type = type;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
