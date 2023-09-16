package com.example.battlefortress;

import com.example.battlefortress.monsters.clampBeetle;
import com.example.battlefortress.monsters.fireWasp;
import com.example.battlefortress.monsters.flyingLocust;
import com.example.battlefortress.monsters.voidButterfly;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";

    private Socket socket;
    private BufferedReader reader;
    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

    public void start(int port) {
        new Thread(() -> connectToServer(port)).start();
    }

    private void connectToServer(int port) {
        System.out.println("Ran");
        try {
            socket = new Socket(HOST, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object object = objectInputStream.readObject();
                if (object instanceof String) {
                    System.out.println("Received message: " + object);
                    String message = (String) object;
                    Platform.runLater(() -> messageReceived(message));
                } else if (object != null) {
                    Platform.runLater(() -> objectReceived(object));
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e + " on port " + port);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                System.err.println("Error closing connection: " + e);
            }
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending message: " + message);
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendObject(Object object) {
        System.out.println("Sending object: " + object);
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void closeSocket() throws IOException {
        if (!this.socket.isClosed()) {
            this.socket.close();
        }
    }

    public void messageReceived(String message) {
        if(message.startsWith("M:")){
            String[] messageSplit = message.split(":");
            int route = Integer.parseInt(messageSplit[3]);
            AnimationTimer summon = new AnimationTimer() {
                long lastUpdate = 0;
                int i = 0;
                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 1_000_000_000) {
                        lastUpdate = now;
                        if(i != Integer.parseInt(messageSplit[2])) {
                            monster mon = null;
                            switch (messageSplit[1]) {
                                case "Clamp Beetle":
                                    mon = new clampBeetle();
                                    break;
                                case "Fire Wasp":
                                    mon = new fireWasp();
                                    break;
                                case "Flying Locust":
                                    mon = new flyingLocust();
                                    break;
                                case "Void Butterfly":
                                    mon = new voidButterfly();
                                    break;
                            }
                            monster finalMon = mon;
                            Platform.runLater(() ->{
                                startController.addMonster(finalMon, route);
                            });
                            i++;
                        } else {
                            this.stop();
                        }
                    }
                }
            };
            summon.start();
        } else if (message.equals("youWon")){
            startController.youWon();
        }
    }

    public void objectReceived(Object object) {
    }

}