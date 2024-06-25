package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;

public class AdminChatDialog extends JDialog {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private String username;

    public AdminChatDialog(JFrame parent, String username, PrintWriter out) {
        super(parent, "Chat with " + username, true);
        this.out = out;
        this.username = username;

        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField(40);
        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);

        add(scrollPane, "Center");
        add(inputPanel, "South");

        pack();
        setLocationRelativeTo(parent);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            out.println(username + ": " + message);
            chatArea.append("Me: " + message + "\n");
            inputField.setText("");
        }
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
}


