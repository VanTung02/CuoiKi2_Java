package view;

import javax.swing.*;

import Controller.UserInfoService;
import model.UserInfo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginView extends JFrame {
    private JPasswordField txtPassword;
	private JTextField txtUsername;
    private static UserInfo loggedInUser;

    public static UserInfo getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(UserInfo user) {
        loggedInUser = user;
    }

    public LoginView() {
        setTitle("Đăng nhập");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel pnTitle = new JPanel();
        pnTitle.setLayout(new FlowLayout());
        pnTitle.setPreferredSize(new Dimension(0, 50));

        JLabel lblTitle = new JLabel("Đăng Nhập");
        pnTitle.add(lblTitle);

        JPanel pnLogin = new JPanel();
        pnLogin.setLayout(new BorderLayout());

        JPanel pnInput = new JPanel();
        pnInput.setLayout(new BoxLayout(pnInput, BoxLayout.Y_AXIS)); 

        JPanel pnUsername = new JPanel(new FlowLayout());
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(20); 
        pnUsername.add(lblUsername);
        pnUsername.add(txtUsername);

        JPanel pnPassword = new JPanel(new FlowLayout());
        JLabel lblPassword = new JLabel("Mật khẩu:");
        txtPassword = new JPasswordField(20); 
        pnPassword.add(lblPassword);
        pnPassword.add(txtPassword);
        
        lblPassword.setPreferredSize(lblUsername.getPreferredSize());
        
        // **************************************************************

        pnInput.add(pnUsername);
        pnInput.add(pnPassword);

        JPanel pnBtnLogin = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Đăng nhập");
        pnBtnLogin.add(btnLogin);
        pnBtnLogin.setPreferredSize(new Dimension(0, 60));

        pnLogin.add(pnTitle, BorderLayout.NORTH);
        pnLogin.add(pnInput, BorderLayout.CENTER);
        pnLogin.add(pnBtnLogin, BorderLayout.SOUTH);

        panel.add(pnLogin, BorderLayout.CENTER);

        add(panel);

        // Bộ xử lý sự kiện cho nút đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lấy thông tin đăng nhập từ các trường nhập liệu
//                String username = txtUsername.getText();
//                String password = new String(txtPassword.getPassword());
//
//                // Gọi phương thức đăng nhập từ UserInfoService
//                UserInfoService userInfoService = new UserInfoService();
//                UserInfo user = userInfoService.login(username, password);
//
//                // Kiểm tra kết quả đăng nhập
//                if (user != null) {
//                    // Nếu đăng nhập thành công, lưu thông tin người dùng vào biến static
//                    setLoggedInUser(user);
//                    MainAdminView adminView = new MainAdminView();
//                    adminView.setVisible(true);
//                    dispose();
//                } else {
//                    // Nếu đăng nhập thất bại, hiển thị thông báo lỗi
//                    JOptionPane.showMessageDialog(LoginView.this, "Email hoặc mật khẩu không chính xác", "Đăng nhập không thành công", JOptionPane.ERROR_MESSAGE);
//                }
                
                handleLogin();
            }

			
        });
    }
    
    private void handleLogin() {
    	String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
     // Gọi phương thức đăng nhập từ UserInfoService
        UserInfoService userInfoService = new UserInfoService();
        UserInfo user = userInfoService.login(username, password);
        
        if (user != null) {
            setLoggedInUser(user);
            
         // Thiết lập kết nối socket tới server
            try {
                Socket socket = new Socket("localhost", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Gửi thông tin đăng nhập tới server
                out.println(username);
                out.println(password);

                String response = in.readLine();
                if ("Login successful".equals(response)) {
                	MainAdminView adminView = new MainAdminView(user, socket);
                	adminView.setVisible(true);
                	this.dispose();
                	
                } else {
                    JOptionPane.showMessageDialog(this, "Email hoặc mật khẩu không đúng", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
            
        } else {
            // Nếu đăng nhập thất bại, hiển thị thông báo lỗi
            JOptionPane.showMessageDialog(LoginView.this, "Email hoặc mật khẩu không chính xác", "Đăng nhập không thành công", JOptionPane.ERROR_MESSAGE);
        }
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            }
        });
    }
}
