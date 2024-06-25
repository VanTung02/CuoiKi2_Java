package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;

public class ChatDialog extends JDialog {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;

    public ChatDialog(JFrame parent, PrintWriter out, BufferedReader in) {
        super(parent, "Chat with Admin", true);
        this.out = out;
        this.in = in;

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

        new Thread(this::receiveMessages).start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            chatArea.append("Me: " + message + "\n");
            inputField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                chatArea.append(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
