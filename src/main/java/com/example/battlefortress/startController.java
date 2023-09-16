package com.example.battlefortress;

import com.example.battlefortress.monsters.clampBeetle;
import com.example.battlefortress.monsters.fireWasp;
import com.example.battlefortress.monsters.flyingLocust;
import com.example.battlefortress.monsters.voidButterfly;
import com.example.battlefortress.towers.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class startController {
    public static AnchorPane anchorPaneStatic;
    // 2D Arrays
    public static Tile[][] tiles2DArrayTD = new Tile[15][30]; // 2D array of tiles for the tower defense game
    public static double cursorX = 0;
    public static double cursorY = 0;
    // Images etc
    private static ImageView errorDisplay = new ImageView(new Image("file:src/main/Images/Misc/error.png"));
    public static Image hammerImage = new Image("file:src/main/Images/Misc/hammer.png");
    public static Image attackImage = new Image("file:src/main/Images/Misc/attack.png");
    public static Image attackPreview = new Image("file:src/main/Images/Misc/attackPreview.png");
    private static ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    private static ArrayList<Integer> rowArrayList = new ArrayList<>();
    private static ArrayList<Integer> colArrayList = new ArrayList<>();
    @FXML

    //FXML
    private AnchorPane anchorPane; // Main anchor pane
    private GridPane towerDefenseGridPane; // GridPane for the tower defense game
    private int[][] types2DayArray = new int[15][30]; // 2D array of the types of tiles
    // Networking
    private static Server server; // Server object
    private static Client client; // Client object
    private static boolean isServer = false; // If the player is the server or not

    public static int gold = 1500;

    private AnimationTimer goldCostTimer;
    public static ArrayList<Tower> towerArrayList = new ArrayList<>();

    public static void createTower(int row, int col, String type, int level, Tile tile) {
        switch (type) {
            case "Arcane Cannon":
                arcaneCannon arcaneCannon = new arcaneCannon(row, col, level);
                towerArrayList.add(arcaneCannon);
                tile.setTower(arcaneCannon);
                startController.tiles2DArrayTD[row-1][col].setTower(arcaneCannon);
                break;
            case "Arcane Tower":
                arcaneTower arcaneTower = new arcaneTower(row, col, level);
                towerArrayList.add(arcaneTower);
                tile.setTower(arcaneTower);
                startController.tiles2DArrayTD[row-1][col].setTower(arcaneTower);
                break;
            case "Mana Blaster":
                manaBlaster manaBlaster = new manaBlaster(row, col, level);
                towerArrayList.add(manaBlaster);
                tile.setTower(manaBlaster);
                startController.tiles2DArrayTD[row-1][col].setTower(manaBlaster);
                break;
            case "Mystic Shockwave":
                mysticShockwave mysticShockwave = new mysticShockwave(row, col, level);
                towerArrayList.add(mysticShockwave);
                tile.setTower(mysticShockwave);
                startController.tiles2DArrayTD[row-1][col].setTower(mysticShockwave);
                break;
            case "Poison Cloud Slingshot":
                poisonCloudSlingshot poisonCloudSlingshot = new poisonCloudSlingshot(row, col, level);
                towerArrayList.add(poisonCloudSlingshot);
                tile.setTower(poisonCloudSlingshot);
                startController.tiles2DArrayTD[row-1][col].setTower(poisonCloudSlingshot);
                break;
            case "Rapid Crossbow":
                rapidCrossbow rapidCrossbow = new rapidCrossbow(row, col, level);
                towerArrayList.add(rapidCrossbow);
                tile.setTower(rapidCrossbow);
                startController.tiles2DArrayTD[row-1][col].setTower(rapidCrossbow);
                break;
            case "Sniper's Nest":
                snipersNest snipersNest = new snipersNest(row, col, level);
                towerArrayList.add(snipersNest);
                tile.setTower(snipersNest);
                startController.tiles2DArrayTD[row-1][col].setTower(snipersNest);
                break;
            case "Thunder Catapult":
                thunderCatapult thunderCatapult = new thunderCatapult(row, col, level);
                towerArrayList.add(thunderCatapult);
                tile.setTower(thunderCatapult);
                startController.tiles2DArrayTD[row-1][col].setTower(thunderCatapult);
                break;
        }
    }

    // Displays an error given the header text and content text
    public static void displayError(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        errorDisplay.setFitHeight(50);
        errorDisplay.setFitWidth(50);
        alert.setGraphic(errorDisplay);
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        anchorPaneStatic = anchorPane;
        generateTypes2DayArray();
        generatePathArray();
        setUpAnchorPaneForStart();
    }

    public static boolean buildBoolean = false;
    public static boolean upgradeBoolean = false;
    public static boolean deleteBoolean = false;
    // makes the background for the game etc

    public static Label totalCoinLabel = new Label("Gold: " + gold + "G");

    public static Image castleIMG = new Image("file:src/main/Images/Misc/castleIMG.png");
    public static ProgressBar castleHealthBar = new ProgressBar();
    public static double castleHealth = 50;
    public static double castleMaxHealth = 50;
    private void setUpAnchorPaneForTowerDefense() {
        anchorPane.getChildren().clear();
        this.towerDefenseGridPane = new GridPane();
        anchorPane.getChildren().add(this.towerDefenseGridPane);
        Image img = new Image("file:src/main/Images/Misc/mainBG.png");
        anchorPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); ");
        totalCoinLabel.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
        Circle circle = new Circle(45);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        circle.setFill(new ImagePattern(attackImage));
        circle.setOnMouseEntered(e -> {
            circle.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 10, 0.5, 0, 0));
        });
        circle.setOnMouseExited(e -> {
            circle.setEffect(null);
        });
        circle.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Attack");
            alert.setHeaderText("");
            AtomicInteger route = new AtomicInteger();

            Circle attackRoute1 = new Circle(32);
            attackRoute1.setStroke(Color.BLACK);
            attackRoute1.setStrokeWidth(2);
            attackRoute1.setFill(new ImagePattern(attackImage));

            Circle attackRoute2 = new Circle(32);
            attackRoute2.setStroke(Color.BLACK);
            attackRoute2.setStrokeWidth(2);
            attackRoute2.setFill(new ImagePattern(attackImage));

            alert.setGraphic(null);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.OK); alert.getButtonTypes().add(ButtonType.CANCEL);
            DialogPane dialogPane = alert.getDialogPane();
            AnchorPane ap = new AnchorPane();
            dialogPane.getChildren().add(ap);
            dialogPane.setPrefWidth(640); dialogPane.setPrefHeight(900);
            ImageView iv = new ImageView();
            iv.setImage(attackPreview);
            dialogPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); ");
            ap.getChildren().addAll(iv, attackRoute1, attackRoute2);

            Label label = new Label("Choose a route to attack");
            label.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
            Platform.runLater(() -> {
                attackRoute1.setLayoutX(256 - attackRoute1.getRadius()*2 - 32);
                attackRoute2.setLayoutX(520 - attackRoute2.getRadius()*2 - 40);
                attackRoute1.setLayoutY(attackRoute1.getRadius()*2);
                attackRoute2.setLayoutY(attackRoute2.getRadius()*2);
                label.setLayoutX(320 - label.getWidth()/2);
                label.setLayoutY(660);
            });

            ap.getChildren().add(label);
            attackRoute1.setOnMouseClicked(e2 -> {
                route.set(1);
                if(attackRoute2.getEffect() != null){
                    attackRoute2.setEffect(null);
                }
                attackRoute1.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 10, 0.5, 0, 0));
                label.setText("Selected Route: 1");
                label.setLayoutX(214.5);
            });

            attackRoute2.setOnMouseClicked(e2 -> {
                route.set(2);
                if(attackRoute1.getEffect() != null){
                    attackRoute1.setEffect(null);
                }
                attackRoute2.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 10, .5, 0, 0));
                label.setText("Selected Route: 2");
                label.setLayoutX(214);
            });

            ChoiceBox<String> cb = new ChoiceBox<>();
            cb.getItems().addAll("Clamp Beetle", "Fire Wasp", "Flying Locust", "Void Butterfly");
            cb.setValue("Select a Monster");
            cb.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px;");
            Platform.runLater(() -> {
                cb.setLayoutX(320 - cb.getWidth()/2);
                cb.setLayoutY(660 + label.getHeight() + 25);
            });

            TextField count = new TextField();
            count.setPromptText("Enter Count");
            count.setStyle("-fx-font-size: 15px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: skyblue; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-prompt-text-fill: azure");
            count.setPrefWidth(150);
            Platform.runLater(() -> {
                count.setLayoutX(cb.getLayoutX() + cb.getWidth() + 25);
                count.setLayoutY(cb.getLayoutY() + 8);
            });

            Label goldCost = new Label("Gold Cost: 0G");
            goldCost.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: gold; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: black;");
            Platform.runLater(() -> {
                goldCost.setLayoutX(320 - goldCost.getWidth()/2);
                goldCost.setLayoutY(660 + label.getHeight() + 25 + cb.getHeight() + 25);
            });
            ap.getChildren().addAll(cb, count, goldCost);
            final int[] c = {0};
            count.setOnKeyTyped(e2 -> {
                if(!count.getText().equals("")) {
                    if (!count.getText().matches("[0-9]+")) {
                        displayError("Invalid Input", "Please enter a valid number");
                        if(goldCostTimer != null){
                            goldCostTimer.stop();
                            goldCostTimer = null;
                        }
                        count.setText("");
                        goldCost.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: gold; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: black;");
                        goldCost.setText("Gold Cost: 0G");
                    } else {
                        if(goldCostTimer != null){
                            goldCostTimer.stop();
                            goldCostTimer = null;
                        }
                        c[0] = Integer.parseInt(count.getText());
                        if (c[0] > 100) {
                            displayError("Invalid Input", "Please enter a number 0-100");
                            count.setText("100");
                        }
                        goldCost.setText("Gold Cost: " + c[0] * 10 + "G");
                        if(c[0] *10 > startController.gold){
                            goldCostTimer = new AnimationTimer() {
                                long lastUpdate = 0;
                                int color = 0;
                                @Override
                                public void handle(long l) {
                                    if(l - lastUpdate >= 250_000_000){
                                        if(color % 2 == 0) {
                                            goldCost.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: gold; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: black;");
                                        } else {
                                            goldCost.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: red; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: white;");
                                        }
                                        color++;
                                        lastUpdate = l;
                                    }
                                }
                            };
                            goldCostTimer.start();
                        } else {
                            if(goldCostTimer != null){
                                goldCostTimer.stop();
                                goldCostTimer = null;
                            }
                            goldCost.setStyle("-fx-font-size: 20px; -fx-font-family: 'Showcard Gothic'; -fx-padding: 10px; -fx-background-color: gold; -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 10px; -fx-background-radius: 15px; -fx-text-fill: black;");
                        }
                    }
                }
            });
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                if(gold < c[0] * 10){
                    displayError("Error", "Not Enough Gold");
                    return;
                }
                if(c[0] <= 0){
                    displayError("Invalid Input", "Please enter a number greater than 0");
                    return;
                }
                changeGold(-c[0] * 10);
                String name = cb.getValue();
                if(name.equals("Select a Monster")){
                    displayError("Invalid Input", "Please select a monster");
                    return;
                }
                if(route.get() <= 0){
                    displayError("Invalid Input", "Please select a route");
                    return;
                }
                new Thread(() -> {
                    if(isServer){
                        server.getClient().sendMessage("M:" + name + ":" + c[0] + ":" + route.get());
                    } else {
                        client.sendMessage("M:" + name + ":" + c[0] + ":" + route.get());
                    }
                }).start();
                alert.close();
            }
        });

        Circle circle2 = new Circle(45);
        circle2.setStroke(Color.BLACK);
        circle2.setStrokeWidth(2);
        circle2.setFill(new ImagePattern(hammerImage));
        circle2.setOnMouseEntered(e -> {
            circle2.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 10, 0.5, 0, 0));
        });
        circle2.setOnMouseExited(e -> {
            if(!buildBoolean) {
                circle2.setEffect(null);
            }
        });
        circle2.setOnMouseClicked(e -> {
            if(buildBoolean){
                buildBoolean = false;
                circle2.setEffect(null);
                for(Tile[] tileArray : tiles2DArrayTD){
                    for(Tile tile : tileArray){
                        tile.getStackPane().setOnMouseClicked(null);
                    }
                }
            } else {
                buildBoolean = true;
                circle2.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 10, 0.5, 0, 0));
            }
            HelloApplication.buildCursor(buildBoolean);
        });
        anchorPane.getChildren().addAll(circle, circle2);


        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 30; j++) {
                int t = types2DayArray[i][j];
                String type = "";
                switch (t) {
                    case 0:
                        type = "barrier";
                        break;
                    case 1:
                        type = "grass";
                        break;
                    case 2:
                        type = "tree";
                        break;
                    case 3:
                        type = "rock";
                        break;
                    case 4:
                        type = "water";
                        break;
                    case 5:
                        type = "path.txt";
                        break;
                }
                Tile tile = new Tile(i, j, type);
                tiles2DArrayTD[i][j] = tile;
                towerDefenseGridPane.add(tiles2DArrayTD[i][j].getStackPane(), j, i);
            }
        }
        ImageView castleIV = new ImageView(castleIMG);
        castleHealthBar.setPrefWidth(160);
        castleHealthBar.setPrefHeight(20);
        castleHealthBar.setProgress(castleHealth/castleMaxHealth);
        castleHealthBar.setStyle("-fx-accent: lightgreen");
        anchorPaneStatic.getChildren().add(castleIV);
        anchorPaneStatic.getChildren().add(castleHealthBar);
        castleIV.setFitWidth(160); castleIV.setFitHeight(160);

        Circle circle3 = new Circle(45);
        circle3.setStroke(Color.BLACK);
        circle3.setStrokeWidth(2);
        circle3.setFill(new ImagePattern(new Image("file:src/main/Images/Misc/deleteIcon.png")));
        circle3.setOnMouseEntered(e -> {
            circle3.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.RED, 10, 0.5, 0, 0));
        });
        circle3.setOnMouseExited(e -> {
            if(!deleteBoolean) {
                circle3.setEffect(null);
            }
        });
        circle3.setOnMouseClicked(e -> {
            if(deleteBoolean){
                deleteBoolean = false;
                circle3.setEffect(null);
            } else {
                deleteBoolean = true;
                circle3.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.RED, 10, 0.5, 0, 0));
            }
        });
        anchorPane.getChildren().add(circle3);
        Circle circle4 = new Circle(45);
        circle4.setStroke(Color.BLACK);
        circle4.setStrokeWidth(2);
        circle4.setFill(new ImagePattern(new Image("file:src/main/Images/Misc/upgrade.png")));
        circle4.setOnMouseEntered(e -> {
            circle4.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.LIGHTGREEN, 10, 0.5, 0, 0));
        });
        circle4.setOnMouseExited(e -> {
            if(!upgradeBoolean) {
                circle4.setEffect(null);
            }
        });
        circle4.setOnMouseClicked(e -> {
            if(upgradeBoolean){
                upgradeBoolean = false;
                circle4.setEffect(null);
            } else {
                upgradeBoolean = true;
                circle4.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.LIGHTGREEN, 10, 0.5, 0, 0));
            }
        });
        anchorPane.getChildren().add(circle4);
        Platform.runLater(() -> {
            circle2.setLayoutX(HelloApplication.stage.getWidth() - circle2.getRadius()*2 - 15);
            circle2.setLayoutY(1009);
            circle.setLayoutX(circle2.getLayoutX() - circle.getRadius()*2 - 15);
            circle.setLayoutY(1009);
            circle4.setLayoutX(circle.getLayoutX() - circle2.getRadius()*2 - 15);
            circle4.setLayoutY(1009);
            circle3.setLayoutX(circle4.getLayoutX() - circle2.getRadius()*2 - 15);
            circle3.setLayoutY(1009);
            totalCoinLabel.setLayoutY(1009 - circle2.getRadius()/2);
            totalCoinLabel.setLayoutX(50 + 160 + 50);
            anchorPaneStatic.getChildren().add(totalCoinLabel);
            castleIV.setLayoutX(50);
            castleIV.setLayoutY(1009 - 70 - circle2.getRadius()/2 - 20);
            castleHealthBar.setLayoutX(50);
            castleHealthBar.setLayoutY(1009 - 70 - circle2.getRadius()/2 + 120);
//            Button button = new Button("Summon");
//            anchorPaneStatic.getChildren().add(button);
//            button.setOnMouseClicked(e -> {
//                voidButterfly vb = new voidButterfly();
//                addMonster(vb,1);
//            }); //todo delete later
        });
        Platform.runLater(this::goldTimer);
    }

    // Sets up the anchor pane so players can join and create server/game
    private void setUpAnchorPaneForStart() {
        Image img = new Image("file:src/main/Images/Misc/castleBG.png");
        anchorPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); ");
        Label gameNameLabel = new Label("Castle Defense");
        gameNameLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: black; -fx-font-family: 'Copperplate Gothic Bold'; -fx-background-color: skyblue; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: black; -fx-padding: 10px");
        gameNameLabel.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, 0.5, 0, 0));
        Platform.runLater(() -> {
            gameNameLabel.setLayoutY(gameNameLabel.getHeight() + 75);
            gameNameLabel.setLayoutX((700 - gameNameLabel.getWidth()) / 2 + 250);
        });
        anchorPane.getChildren().add(gameNameLabel);


        // TextField where the player inputs the port number
        TextField portNumber = new TextField();
        portNumber.setPromptText("Enter port number");
        anchorPane.getChildren().add(portNumber);
        portNumber.setStyle("-fx-background-color: skyblue; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: black; -fx-padding: 10px; -fx-font-size: 20px; -fx-text-fill: black; -fx-font-family: 'Copperplate Gothic Bold'; -fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 0); -fx-prompt-text-fill: black;");
        Platform.runLater(() -> {
            portNumber.setLayoutX(gameNameLabel.getLayoutX() + gameNameLabel.getWidth()/2 - portNumber.getWidth()/2);
            portNumber.setLayoutY(gameNameLabel.getLayoutY() + gameNameLabel.getHeight() + 50);
        });
        // Button that creates a server
        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-background-color: skyblue; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: black; -fx-padding: 10px; -fx-font-size: 20px; -fx-text-fill: black; -fx-font-family: 'Copperplate Gothic Bold'; -fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 0);");
        startGameButton.setOnMouseClicked(e -> {
            if (portNumber.getText().length() > 0 && portNumber.getText().matches("[0-9]+")) {
                new Thread(() -> {
                    try {
                        isServer = true;
                        Platform.runLater(() -> HelloApplication.stage.setTitle("Server - " + portNumber.getText()));
                        Platform.runLater(this::setUpAnchorPaneForTowerDefense);
                        Server server = new Server();
                        this.server = server;
                        server.start(Integer.parseInt(portNumber.getText()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }).start();
            } else {
                // Invalid Port Error
                displayError("Invalid port number!", "Please enter a valid port number");
            }
        });
        anchorPane.getChildren().add(startGameButton);
        Platform.runLater(() -> {
            startGameButton.setLayoutX(gameNameLabel.getLayoutX() + gameNameLabel.getWidth()/2 - startGameButton.getWidth()/2);
            startGameButton.setLayoutY(portNumber.getLayoutY() + portNumber.getHeight() + 50);
        });
        // Button that connects to the server and creates a client
        Button joinGameButton = new Button("Join Game");
        joinGameButton.setStyle("-fx-background-color: skyblue; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: black; -fx-padding: 10px; -fx-font-size: 20px; -fx-text-fill: black; -fx-font-family: 'Copperplate Gothic Bold'; -fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 0);");
        anchorPane.getChildren().add(joinGameButton);
        Platform.runLater(() -> {
            joinGameButton.setLayoutX(startGameButton.getLayoutX() + startGameButton.getWidth()/2 - joinGameButton.getWidth()/2);
            joinGameButton.setLayoutY(startGameButton.getLayoutY() + startGameButton.getHeight() + 20);

        });
        joinGameButton.setOnMouseClicked(e -> {
            if (portNumber.getText().length() > 0 && portNumber.getText().matches("[0-9]+")) {
                new Thread(() -> {
                    isServer = false;
                    Platform.runLater(() -> HelloApplication.stage.setTitle("Client - " + portNumber.getText()));
                    Platform.runLater(this::setUpAnchorPaneForTowerDefense);
                    Client client = new Client();
                    this.client = client;
                    client.start(Integer.parseInt(portNumber.getText()));
                }).start();
            } else {
                // Invalid Port Error
                displayError("Invalid port number!", "Please enter a valid port number");
            }
        });

        Platform.runLater(() -> HelloApplication.changeWindowSize(700, 750));
    }

    private void generateTypes2DayArray() {
        try {
            File file = new File("src/main/Textfiles/types2D.txt");
            Scanner scanner = new Scanner(file);
            for (int row = 0; row < types2DayArray.length; row++) {
                String line = scanner.nextLine();
                String[] lineArray = line.split(" ");
                for (int col = 0; col < types2DayArray[0].length; col++) {
                    types2DayArray[row][col] = Integer.parseInt(lineArray[col]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    } // sets the types for 2D array
    private static int[][] pathArray = new int[15][30];
    public static void addMonster(monster monster, int route){
        ImageView monsterIV = monster.getMonsterIV();
        anchorPaneStatic.getChildren().add(monsterIV);
        monsterIV.setFitHeight(64);
        monsterIV.setFitWidth(64);
        monsterIV.setPreserveRatio(true);
        monster.animate();
        monsterIV.setRotate(-90);
        int startX = findLocation(route)[0];
        int startY = findLocation(route)[1];
        int[] currentLocation = new int[]{startX,startY};
        if(route == 2){
            moveCurved(monster,10,-2,11,-2,11,-1, move(monster,currentLocation,new int[]{currentLocation[0],currentLocation[1]-1}));
        } else {
            monsterIV.setLayoutX((startY-1) * 64);
            monsterIV.setLayoutY(startX * 64);
            moveLine(monster,startY,startX, move(monster,currentLocation,new int[]{currentLocation[0],currentLocation[1]-1}));
        }
    }
    public static void looseCastleHealth(){
        castleHealth--;
        castleHealthBar.setProgress(castleHealth/castleMaxHealth);
        castleHealthBar.setStyle("-fx-accent: red");
        if(castleHealth == 0){
            if(isServer){
                server.getClient().sendMessage("youWon");
            } else {
                client.sendMessage("youWon");
            }
            startController.youLost();
        }
    }
    private static Runnable move(monster monster, int[] currentLocation, int [] lastLocation) {
        if(currentLocation[0] == 7 && currentLocation[1] == 27){
            return () -> {
                moveLine(monster,31,7, startController::looseCastleHealth);
            };
        }
        boolean up = false;
        int upCount = 0;
        boolean down = false;
        int downCount = 0;
        boolean right = false;
        int rightCount = 0;
        boolean left = false;
        int leftCount = 0;

        if(currentLocation[0] != 14) {
            if (pathArray[currentLocation[0] + 1][currentLocation[1]] == 5 && currentLocation[0] + 1 != lastLocation[0]) {
                down = true;
                downCount = 1;
                while(pathArray[currentLocation[0] + downCount + 1][currentLocation[1]] == 5 && pathArray[currentLocation[0] + downCount + 1][currentLocation[1] + 1] != 5 && pathArray[currentLocation[0] + downCount + 1][currentLocation[1] - 1] != 5){
                    downCount++;
                }
            }
        }
        if(currentLocation[0] != 0){
            if(pathArray[currentLocation[0] - 1][currentLocation[1]] == 5 && currentLocation[0] - 1 != lastLocation[0]){
                up = true;
                upCount = 1;
                while(pathArray[currentLocation[0] - upCount - 1][currentLocation[1]] == 5 && pathArray[currentLocation[0] - upCount - 1][currentLocation[1] + 1] != 5 && pathArray[currentLocation[0] - upCount - 1][currentLocation[1] - 1] != 5){
                    upCount++;
                }
            }
        }
        if(currentLocation[1] != 29) {
            if (pathArray[currentLocation[0]][currentLocation[1] + 1] == 5 && currentLocation[1] + 1 != lastLocation[1]) {
                right = true;
                rightCount = 1;
                while(pathArray[currentLocation[0]][currentLocation[1] + rightCount + 1] == 5 && pathArray[currentLocation[0] + 1][currentLocation[1] + rightCount + 1] != 5 && pathArray[currentLocation[0] - 1][currentLocation[1] + rightCount + 1] != 5){
                    rightCount++;
                }
            }
        }
        if(currentLocation[1] != 0){
            if(pathArray[currentLocation[0]][currentLocation[1] - 1] == 5 && currentLocation[1] - 1 != lastLocation[1]){
                left = true;
                leftCount = 1;
                while(pathArray[currentLocation[0]][currentLocation[1] - leftCount - 1] == 5 && pathArray[currentLocation[0] + 1][currentLocation[1] - leftCount - 1] != 5 && pathArray[currentLocation[0] - 1][currentLocation[1] - leftCount - 1] != 5){
                    leftCount++;
                }
            }
        }

        if ((up && !down && !right && !left) || (!up && down && !right && !left) || (!up && !down && right && !left) || (!up && !down && !right && left)) {
            if (up && upCount >= 1 && pathArray[currentLocation[0] - 2][currentLocation[1]] == 5) {
                int x = currentLocation[0];
                int y = currentLocation[1];
                currentLocation[0] = currentLocation[0] - upCount;
                int finalUpCount = upCount;
                return () -> {
                    moveLine(monster, y, x - finalUpCount, move(monster, currentLocation, new int[]{currentLocation[0] + 1,y}));
                };
            } else if (down && downCount >= 1 && pathArray[currentLocation[0] + 2][currentLocation[1]] == 5) {
                int x = currentLocation[0];
                int y = currentLocation[1];
                currentLocation[0] = currentLocation[0] + downCount;
                int finalDownCount = downCount;
                return () -> {
                    moveLine(monster, y, x + finalDownCount, move(monster, currentLocation, new int[]{currentLocation[0] - 1,y}));
                };
            } else if (right && rightCount >= 1 && pathArray[currentLocation[0]][currentLocation[1] + 2] == 5) {
                int x = currentLocation[0];
                int y = currentLocation[1];
                currentLocation[1] = currentLocation[1] + rightCount;
                int finalRightCount = rightCount;
                return () -> {
                    moveLine(monster, y + finalRightCount, x, move(monster, currentLocation, new int[]{x,currentLocation[1] - 1}));
                };
            } else if (left && leftCount >= 1 && pathArray[currentLocation[0]][currentLocation[1] - 2] == 5) {
                int x = currentLocation[0];
                int y = currentLocation[1];
                currentLocation[1] = currentLocation[1] - leftCount;
                int finalLeftCount = leftCount;
                return () -> {
                    moveLine(monster, y - finalLeftCount, x, move(monster, currentLocation, new int[]{x,currentLocation[1] + 1}));
                };
            }
        }
        if (right && pathArray[currentLocation[0] - 1][currentLocation[1] + 1] == 5) { // right up turn
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] - 1;
            currentLocation[1] = currentLocation[1] + 1;
            return () -> {
                moveCurved(monster,y,x,y+1,x,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] + 1 ,currentLocation[1]}));
            };
        }
        if(right && pathArray[currentLocation[0] + 1][currentLocation[1] + 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] + 1;
            currentLocation[1] = currentLocation[1] + 1;
            return () -> {
                moveCurved(monster,y,x,y+1,x,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] - 1 ,currentLocation[1]}));
            };
        }
        if(left && pathArray[currentLocation[0] - 1][currentLocation[1] - 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] - 1;
            currentLocation[1] = currentLocation[1] - 1;
            return () -> {
                moveCurved(monster,y,x,y-1,x,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] + 1 ,currentLocation[1]}));
            };
        }
        if(left && pathArray[currentLocation[0] + 1][currentLocation[1] - 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] + 1;
            currentLocation[1] = currentLocation[1] - 1;
            return () -> {
                moveCurved(monster,y,x,y-1,x,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] - 1 ,currentLocation[1]}));
            };
        }

        if(up && pathArray[currentLocation[0] - 1][currentLocation[1] + 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] - 1;
            currentLocation[1] = currentLocation[1] + 1;
            return () -> {
                moveCurved(monster,y,x,y,x-1,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] ,currentLocation[1] - 1}));
            };
        }
        if(up && pathArray[currentLocation[0] - 1][currentLocation[1] - 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] - 1;
            currentLocation[1] = currentLocation[1] - 1;
            return () -> {
                moveCurved(monster,y,x,y,x-1,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] ,currentLocation[1] + 1}));
            };
        }
        if(down && pathArray[currentLocation[0] + 1][currentLocation[1] + 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] + 1;
            currentLocation[1] = currentLocation[1] + 1;
            return () -> {
                moveCurved(monster,y,x,y,x+1,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] ,currentLocation[1] - 1}));
            };
        }
        if(down && pathArray[currentLocation[0] + 1][currentLocation[1] - 1] == 5){
            int x = currentLocation[0];
            int y = currentLocation[1];
            currentLocation[0] = currentLocation[0] + 1;
            currentLocation[1] = currentLocation[1] - 1;
            return () -> {
                moveCurved(monster,y,x,y,x+1,currentLocation[1],currentLocation[0],move(monster,currentLocation,new int[]{currentLocation[0] ,currentLocation[1] + 1}));
            };
        }
        return () -> {
            displayError("Error", "Monster is stuck");
        };
    }

    private void generatePathArray() {
        try {
            File file = new File("src/main/Textfiles/path.txt");
            Scanner scanner = new Scanner(file);
            for (int row = 0; row < 15; row++) {
                String line = scanner.nextLine();
                String[] lineArray = line.split(" ");
                for (int col = 0; col < 30; col++) {
                    pathArray[row][col] = Integer.parseInt(lineArray[col]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static int[] findLocation(int target){
        for(int r = 0; r < pathArray.length; r++){
            for(int c = 0; c < pathArray[r].length; c++){
                if(pathArray[r][c] == target){
                    return new int[]{r,c};
                }
            }
        }
        return new int[]{-1,-1};
    }

    private static void moveLine(monster monster, double endX, double endY, Runnable callback){
        Node node = monster.getMonsterIV();
        AnimationTimer animationTimer = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if(now - lastUpdate >= 1_000_000){
                    if(monster.isDead()){
                        this.stop();
                        return;
                    }
                    if(!monster.isStun()) {
                        if (node.getLayoutX() != endX * 64) {
                            int direction = (int) Math.signum(endX * 64 - node.getLayoutX());
                            node.setLayoutX(node.getLayoutX() + 2 * direction);
                        }
                        if (node.getLayoutY() != endY * 64) {
                            int direction = (int) Math.signum(endY * 64 - node.getLayoutY());
                            node.setLayoutY(node.getLayoutY() + 2 * direction);
                        }
                        if (node.getLayoutX() == endX * 64 && node.getLayoutY() == endY * 64) {
                            this.stop();
                            if (callback != null) {
                                callback.run();
                            }
                        }
                    }
                }
                lastUpdate = now;
                if(node != null){
                    monster.alertTowers();
                }
            }
        };
        animationTimer.start();
    }

    private static void moveCurved(monster monster, double startX, double startY, double controlX, double controlY, double endX, double endY, Runnable callback){
        Node node = monster.getMonsterIV();
        QuadCurve quadcurve = new QuadCurve(startX*64,startY*64,controlX*64,controlY*64,endX*64,endY*64);
        double step = 0.1;
        double totalLength = 0;
        for (double t = 0; t < 1; t += step) {
            double x1 = quadcurve.getStartX() + t * (quadcurve.getControlX() - quadcurve.getStartX());
            double y1 = quadcurve.getStartY() + t * (quadcurve.getControlY() - quadcurve.getStartY());
            double x2 = quadcurve.getControlX() + t * (quadcurve.getEndX() - quadcurve.getControlX());
            double y2 = quadcurve.getControlY() + t * (quadcurve.getEndY() - quadcurve.getControlY());
            double dx = x2 - x1;
            double dy = y2 - y1;
            totalLength += Math.sqrt(dx * dx + dy * dy);
        }
        double finalTotalLength = totalLength;
        AnimationTimer animationTimer = new AnimationTimer() {
            double distance = 0;
            double stepSize = finalTotalLength / 100;
            double totalLength2 = finalTotalLength;
            double lastX = node.getLayoutX();
            double lastY = node.getLayoutY();

            @Override
            public void handle(long l) {
                if(monster.isDead()){
                    this.stop();
                    return;
                }
                if(!monster.isStun()){
                    if (distance <= totalLength2) {
                        Point2D point = deCasteljau(quadcurve.getStartX(), quadcurve.getStartY(),
                                quadcurve.getControlX(), quadcurve.getControlY(),
                                quadcurve.getEndX(), quadcurve.getEndY(), distance / totalLength2);
                        node.setLayoutX(point.getX());
                        node.setLayoutY(point.getY());
                        distance += stepSize;
                        double dx = point.getX() - lastX;
                        double dy = point.getY() - lastY;
                        double angle = Math.atan2(dy, dx);
                        node.getTransforms().clear();
                        node.getTransforms().add(new Rotate(Math.toDegrees(angle), node.getBoundsInLocal().getWidth() / 2, node.getBoundsInLocal().getHeight() / 2));
                        distance += stepSize;
                        lastX = point.getX();
                        lastY = point.getY();
                    } else {
                        this.stop();
                        node.setLayoutX(endX * 64);
                        node.setLayoutY(endY * 64);
                        if (callback != null) {
                            callback.run();
                        }
                    }
                }
                if (node != null) {
                    monster.alertTowers();
                }
            }
        };
        animationTimer.start();
    }
    private static Point2D deCasteljau(double x1, double y1, double x2, double y2, double x3, double y3, double t) {
        double x12 = x1 + t * (x2 - x1);
        double y12 = y1 + t * (y2 - y1);
        double x23 = x2 + t * (x3 - x2);
        double y23 = y2 + t * (y3 - y2);
        double x = x12 + t * (x23 - x12);
        double y = y12 + t * (y23 - y12);
        return new Point2D(x, y);
    }

    private static void monsterReachEnd(){
        anchorPaneStatic.setStyle("-fx-background-color: #ff0000");
    }

    private static Image fishImage = new Image("file:src/main/Images/Misc/fishAnimation.png");
    private static Image goldCoinImage = new Image("file:src/main/Images/Misc/Full Coins.png");
    private int fishFrame = 0;
    private void goldTimer(){
        int[] water = findWater();
        tiles2DArrayTD[water[0]][water[1]].setFishGold();
        new Thread (() -> {
            AnimationTimer fish = new AnimationTimer() {
                long lastUpdate = 0;
                @Override
                public void handle(long l) {
                    if(l - lastUpdate >= 200_000_000) {
                        PixelReader pixelReader = fishImage.getPixelReader();
                        if(fishFrame < 8) {
                            WritableImage writableImage = new WritableImage(pixelReader, fishFrame * 64, 0, 64, 64);
                            tiles2DArrayTD[water[0]][water[1]].setBackgroundImageView(writableImage);
                            fishFrame++;
                        } else {
                            fishFrame = 0;
                            WritableImage writableImage = new WritableImage(pixelReader, 8*64, 0, 64, 64);
                            tiles2DArrayTD[water[0]][water[1]].setBackgroundImageView(writableImage);
                            this.stop();
                            tiles2DArrayTD[water[0]][water[1]].setStackPaneClickNull();
                            goldTimer();
                        }
                        lastUpdate = l;
                    }
                }
            };
            fish.start();
        }).start();
    }

    private int[] findWater(){
        ArrayList<Integer> waterListRow = new ArrayList<>();
        ArrayList<Integer> waterListCol = new ArrayList<>();
        for(int r = 0; r < types2DayArray.length; r++){
            for(int c = 0; c < types2DayArray[r].length; c++){
                if(types2DayArray[r][c] == 4){
                    waterListRow.add(r);
                    waterListCol.add(c);
                }
            }
        }
        int random = (int) (Math.random() * waterListRow.size());
        return new int[]{waterListRow.get(random), waterListCol.get(random)};
    }

    public static void monsterDeath(monster m){
        for(Tower tower : towerArrayList){
            tower.outOfRangeMonster(m);
            if(tower.getTempMonster() == m){
                tower.setTempMonsterNull();
                tower.stopRotate();
            }
        }
    }

    public static void changeGold(int gold){
        startController.gold += gold;
        totalCoinLabel.setText("Gold: " + startController.gold + "G");
    }

    public static void youLost(){
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You Lost");
                alert.setHeaderText("");
                alert.setGraphic(null);
                Image youLost = new Image("file:src/main/Images/Misc/castleBGDestroyed.png");
                alert.getDialogPane().setStyle("-fx-background-image: url('" + youLost.getUrl() + "'); ");
                alert.getDialogPane().setPrefWidth(youLost.getWidth());
                alert.getDialogPane().setPrefHeight(youLost.getHeight());
                alert.showAndWait();
                System.exit(0);
            });
        });
        thread.start();
    }
    public static void youWon(){
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You Won");
                alert.setHeaderText("");
                alert.setGraphic(null);
                Image youWon = new Image("file:src/main/Images/Misc/castleBG.png");
                alert.getDialogPane().setStyle("-fx-background-image: url('" + youWon.getUrl() + "'); ");
                alert.getDialogPane().setPrefWidth(youWon.getWidth());
                alert.getDialogPane().setPrefHeight(youWon.getHeight());
                alert.showAndWait();
                System.exit(0);
            });
        });
        thread.start();
    }

} // End of class startController