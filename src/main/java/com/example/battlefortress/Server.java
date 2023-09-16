package com.example.battlefortress;

import com.example.battlefortress.monsters.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public int port;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.port = port;
        System.out.println("Server started on port " + port);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress().getHostName() + " on port " + socket.getPort());
            ClientHandler clientHandler = new ClientHandler(socket, this);
            clients.add(clientHandler);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendObject(message);
            }
        }
    }

    public void broadcastALL(String message) {
        for (ClientHandler client : clients) {
            client.sendObject(message);
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected: " + clientHandler.getSocket().getInetAddress().getHostName());
    }

    public ClientHandler getClient(){
        return clients.get(0);
    }

    public void messageReceived(String message, ClientHandler client) {
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

    public void objectReceived(Object object, ClientHandler client) {
        //TODO: Handle object
    }

}
