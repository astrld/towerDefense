package com.example.battlefortress;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;

    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

    private boolean playerReady = false;


    private String playerName;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(String message) {
        System.out.println("Sending msg: " + message);
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void sendObject(Object object) {
        new Thread(() -> {
            try {
                objectOutputStream.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object object = objectInputStream.readObject();
                if (object instanceof String) {
                    String message = (String) object;
                    server.messageReceived(message, this);
                } else if (object != null) {
                    server.objectReceived(object, this);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Closing client socket: " + socket.getInetAddress().getHostName() + " " + socket.getPort());
                socket.close();
                server.removeClient(this);
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e);
            }
        }
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean getPlayerReady() {
        return playerReady;
    }

    public void setPlayerReady(boolean playerReady) {
        this.playerReady = playerReady;
    }
}

