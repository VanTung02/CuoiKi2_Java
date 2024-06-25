package server;

import dao.UserInfoDao;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import model.UserInfo;

public class ChatServer {
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> admins;
    private Map<String, ClientHandler> users;
    private ConcurrentHashMap<String, List<String>> messageQueue;

    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            admins = new HashMap<>();
            users = new HashMap<>();
            messageQueue = new ConcurrentHashMap<>();
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private UserInfoDao userDao;
        private UserInfo loggedInUser;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.userDao = new UserInfoDao();
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // Đọc thông tin đăng nhập từ khách hàng
                String email = in.readLine();
                String password = in.readLine();

                if (authenticate(email, password)) {
                    username = email;
                    if (loggedInUser != null) { // Kiểm tra loggedInUser có null không
                        System.out.println("Logged in user: " + loggedInUser.getEmail() + ", Role ID: " + loggedInUser.getRoleID());
                    } else {
                        System.out.println("Authentication failed, user is null.");
                        out.println("Login failed");
                        return;
                    }
                    
                    if (loggedInUser.getRoleID() == 1) {
                        admins.put(email, this);
                    } else if (loggedInUser.getRoleID() == 2) {
                        users.put(email, this);
                    }
                    out.println("Login successful");
                    System.out.println(email + " (role: " + (loggedInUser.getRoleID() == 1 ? "admin" : "user") + ") has logged in.");

                    // Xử lý tin nhắn từ khách hàng
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        logMessage(email, msg);
                        if (loggedInUser.getRoleID() == 2) {
                            sendToAdmins(email, msg);
                        } else {
                            String[] parts = msg.split(":", 2);
                            if (parts.length == 2) {
                                String targetUser = parts[0];
                                String message = parts[1];
                                sendToUser(targetUser, email + ": " + message);
                            }
                        }
                    }
                } else {
                    out.println("Login failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    if (loggedInUser != null && loggedInUser.getRoleID() == 1) {
                        admins.remove(username);
                    } else if (loggedInUser != null && loggedInUser.getRoleID() == 2) {
                        users.remove(username);
                    }
                    System.out.println(username + " has logged out.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean authenticate(String email, String password) {
            loggedInUser = userDao.login(email, password);
            return loggedInUser != null;
        }

        private void sendToAdmins(String username, String message) {
            for (ClientHandler admin : admins.values()) {
                admin.out.println(username + ": " + message);
                logMessage("Server", "Message sent to admin: " + admin.username);
                if (!messageQueue.containsKey(username)) {
                    messageQueue.put(username, new ArrayList<>());
                }
                messageQueue.get(username).add(message);
            }
        }

        private void sendToUser(String targetUser, String message) {
            ClientHandler userHandler = users.get(targetUser);
            if (userHandler != null) {
                userHandler.out.println(message);
                logMessage("Server", "Message sent to user: " + userHandler.username);
            }
        }

        private void logMessage(String username, String message) {
            String log = username + ": " + message;
            System.out.println(log);
            try (FileWriter fw = new FileWriter("chat_log.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter pw = new PrintWriter(bw)) {
                pw.println(log);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer(12345);
    }
}
