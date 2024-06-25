package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;

import Controller.RoleService;
import Controller.UserInfoService;
import model.UserInfo;

import java.util.Date;
import java.util.List;
import model.Role; // Import Role model

public class ManageUserPanel extends JPanel {
    private JTextField userIDField;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox; // Change JComboBox<String> to JComboBox<Role>
    private JTextField genderField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JDateChooser birthdayChooser;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deleteUserButton;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserInfoService userService;
    private RoleService roleService; // Add RoleService reference

    public ManageUserPanel() {
        userService = new UserInfoService();
        roleService = new RoleService(); // Initialize RoleService
        setLayout(new BorderLayout());

        // Title at the top
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("User Management"));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        centerPanel.add(userPanel, BorderLayout.NORTH);

        JPanel northOfUserPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");

        northOfUserPanel.add(addUserButton);
        northOfUserPanel.add(editUserButton);
        northOfUserPanel.add(deleteUserButton);

        userPanel.add(northOfUserPanel, BorderLayout.SOUTH);

        JPanel infoUserPanel = new JPanel();
        infoUserPanel.setLayout(new GridLayout(3, 3, 20, 30));
        userPanel.add(infoUserPanel, BorderLayout.CENTER);

        JLabel lblUserID = new JLabel("User ID:");
        userIDField = new JTextField();
        userIDField.setEnabled(false);
        userIDField.setDisabledTextColor(Color.BLACK);

        JLabel lblUserName = new JLabel("User Name:");
        userNameField = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        passwordField = new JPasswordField();

        JLabel lblRole = new JLabel("Role:");
        roleComboBox = new JComboBox<>(); // Change to JComboBox<Role>

        JLabel lblGender = new JLabel("Gender:");
        genderField = new JTextField();

        JLabel lblEmail = new JLabel("Email:");
        emailField = new JTextField();

        JLabel lblPhone = new JLabel("Phone:");
        phoneField = new JTextField();

        JLabel lblAddress = new JLabel("Address:");
        addressField = new JTextField();

        JLabel lblBirthday = new JLabel("Birthday:");
        birthdayChooser = new JDateChooser();

        infoUserPanel.add(lblUserID);
        infoUserPanel.add(userIDField);
        infoUserPanel.add(lblUserName);
        infoUserPanel.add(userNameField);
        infoUserPanel.add(lblPassword);
        infoUserPanel.add(passwordField);
        infoUserPanel.add(lblRole);
        infoUserPanel.add(roleComboBox); // Change to JComboBox<Role>
        infoUserPanel.add(lblGender);
        infoUserPanel.add(genderField);
        infoUserPanel.add(lblEmail);
        infoUserPanel.add(emailField);
        infoUserPanel.add(lblPhone);
        infoUserPanel.add(phoneField);
        infoUserPanel.add(lblAddress);
        infoUserPanel.add(addressField);
        infoUserPanel.add(lblBirthday);
        infoUserPanel.add(birthdayChooser);

        // User table
        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.setBorder(BorderFactory.createTitledBorder("User List"));

        tableModel = new DefaultTableModel();
        tableModel.addColumn("User ID");
        tableModel.addColumn("User Name");
        tableModel.addColumn("Role");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Address");
        tableModel.addColumn("Birthday");

        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        userListPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(userListPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Display user list on initialization
        displayUsers();

        // Load roles into the combobox
        loadRoles();

        // Set up event listeners for buttons
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        // Event listener for selecting a row in the user table
        userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                    int selectedRow = userTable.getSelectedRow();
                    int userID = (int) userTable.getValueAt(selectedRow, 0);
                    String userName = (String) userTable.getValueAt(selectedRow, 1);
                    int roleID = (int) userTable.getValueAt(selectedRow, 2);
                    String gender = (String) userTable.getValueAt(selectedRow, 3);
                    String email = (String) userTable.getValueAt(selectedRow, 4);
                    String phone = (String) userTable.getValueAt(selectedRow, 5);
                    String address = (String) userTable.getValueAt(selectedRow, 6);
                    Date birthday = (Date) userTable.getValueAt(selectedRow, 7);

                    userIDField.setText(String.valueOf(userID));
                    userNameField.setText(userName);
                    // Set the selected role in the combobox
                    selectRole(roleID);
                    genderField.setText(gender);
                    emailField.setText(email);
                    phoneField.setText(phone);
                    addressField.setText(address);
                    birthdayChooser.setDate(birthday);
                }
            }
        });
    }
    
    private void selectRole(int roleID) {
        for (int i = 0; i < roleComboBox.getItemCount(); i++) {
            Role role = (Role) roleComboBox.getItemAt(i);
            if (role.getRoleID() == roleID) {
                roleComboBox.setSelectedIndex(i);
                return;
            }
        }
        // If role not found, select the first item in the combobox
        if (roleComboBox.getItemCount() > 0) {
            roleComboBox.setSelectedIndex(0);
        }
    }

    private void displayUsers() {
        tableModel.setRowCount(0);

        List<UserInfo> users = userService.getAllUsers();

        for (UserInfo user : users) {
            Object[] rowData = {user.getUserID(), user.getUserName(), user.getRoleID(), user.getGender(),
                    user.getEmail(), user.getPhone(), user.getAddress(), user.getBirthday()};
            tableModel.addRow(rowData);
        }
    }

    private void addUser() {
        // Get data from input fields
        String userName = userNameField.getText();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem(); // Get selected role from combobox
        String gender = genderField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        Date birthday = birthdayChooser.getDate();

        // Validate input
        if (userName.isEmpty() || password.isEmpty() || role == null || gender.isEmpty()
                || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new user object
        UserInfo user = new UserInfo(userName, password, role.getRoleID(), gender, email, phone, address, birthday);

        // Add user to database
        boolean success = userService.addUser(user);

        if (success) {
            JOptionPane.showMessageDialog(this, "User added successfully");
            displayUsers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userID = Integer.parseInt(userIDField.getText());
        String userName = userNameField.getText();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem(); // Get selected role from combobox
        String gender = genderField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        Date birthday = birthdayChooser.getDate();

        // Validate input
        if (userName.isEmpty() || role == null || gender.isEmpty()
                || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a user object
        UserInfo user = new UserInfo(userID, userName, password, role.getRoleID(), gender, email, phone, address, birthday);

        // Update user in database
        boolean success = userService.updateUser(user);

        if (success) {
            JOptionPane.showMessageDialog(this, "User updated successfully");
            displayUsers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userID = Integer.parseInt(userIDField.getText());

        // Delete user from database
        boolean success = userService.deleteUser(userID);

        if (success) {
            JOptionPane.showMessageDialog(this, "User deleted successfully");
            displayUsers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        userIDField.setText("");
        userNameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        genderField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        birthdayChooser.setDate(null);
    }

    // Load roles into the combobox
    private void loadRoles() {
        List<Role> roles = roleService.getAllRoles();
        for (Role role : roles) {
            roleComboBox.addItem(role);
        }
    }

    // Get Role object by ID
    private Role getRoleByID(int roleID) {
        List<Role> roles = roleService.getAllRoles();
        for (Role role : roles) {
            if (role.getRoleID() == roleID) {
                return role;
            }
        }
        return null; // Return null if role not found
    }
}
