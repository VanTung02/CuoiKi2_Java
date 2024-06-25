package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;

import Controller.UserInfoService;
import model.UserInfo;

import java.util.Date;
import java.util.List;

public class ManageCustomerPanel extends JPanel {
    private JTextField customerIDField;
    private JTextField customerNameField;
    private JTextField genderField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JDateChooser birthdayChooser;
    private JButton addCustomerButton;
    private JButton editCustomerButton;
    private JButton deleteCustomerButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private UserInfoService userInfoService;

    public ManageCustomerPanel() {
        userInfoService = new UserInfoService();
        setLayout(new BorderLayout());

        // Title at the top
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Customer Management"));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));

        centerPanel.add(customerPanel, BorderLayout.NORTH);

        JPanel northOfCustomerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addCustomerButton = new JButton("Add Customer");
        editCustomerButton = new JButton("Edit Customer");
        deleteCustomerButton = new JButton("Delete Customer");

        northOfCustomerPanel.add(addCustomerButton);
        northOfCustomerPanel.add(editCustomerButton);
        northOfCustomerPanel.add(deleteCustomerButton);

        customerPanel.add(northOfCustomerPanel, BorderLayout.SOUTH);

        JPanel infoCustomerPanel = new JPanel();
        infoCustomerPanel.setLayout(new GridLayout(4, 2, 20, 30)); // Changed to 4 rows
        customerPanel.add(infoCustomerPanel, BorderLayout.CENTER);

        JLabel lblCustomerID = new JLabel("Customer ID:");
        customerIDField = new JTextField();
        customerIDField.setEnabled(false);
        customerIDField.setDisabledTextColor(Color.BLACK);

        JLabel lblCustomerName = new JLabel("Customer Name:");
        customerNameField = new JTextField();

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

        infoCustomerPanel.add(lblCustomerID);
        infoCustomerPanel.add(customerIDField);
        infoCustomerPanel.add(lblCustomerName);
        infoCustomerPanel.add(customerNameField);
        infoCustomerPanel.add(lblGender);
        infoCustomerPanel.add(genderField);
        infoCustomerPanel.add(lblEmail);
        infoCustomerPanel.add(emailField);
        infoCustomerPanel.add(lblPhone);
        infoCustomerPanel.add(phoneField);
        infoCustomerPanel.add(lblAddress);
        infoCustomerPanel.add(addressField);
        infoCustomerPanel.add(lblBirthday);
        infoCustomerPanel.add(birthdayChooser);

        // Customer table
        JPanel customerListPanel = new JPanel(new BorderLayout());
        customerListPanel.setBorder(BorderFactory.createTitledBorder("Customer List"));

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Customer ID");
        tableModel.addColumn("Customer Name");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Address");
        tableModel.addColumn("Birthday");

        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);

        customerListPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(customerListPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Display customer list on initialization
        displayCustomers();

        // Set up event listeners for buttons
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        editCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCustomer();
            }
        });

        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        // Event listener for selecting a row in the customer table
        customerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                    int selectedRow = customerTable.getSelectedRow();
                    int customerID = (int) customerTable.getValueAt(selectedRow, 0);
                    String customerName = (String) customerTable.getValueAt(selectedRow, 1);
                    String gender = (String) customerTable.getValueAt(selectedRow, 2);
                    String email = (String) customerTable.getValueAt(selectedRow, 3);
                    String phone = (String) customerTable.getValueAt(selectedRow, 4);
                    String address = (String) customerTable.getValueAt(selectedRow, 5);
                    Date birthday = (Date) customerTable.getValueAt(selectedRow, 6);

                    customerIDField.setText(String.valueOf(customerID));
                    customerNameField.setText(customerName);
                    genderField.setText(gender);
                    emailField.setText(email);
                    phoneField.setText(phone);
                    addressField.setText(address);
                    birthdayChooser.setDate(birthday);
                }
            }
        });
    }

    private void displayCustomers() {
        tableModel.setRowCount(0);

        List<UserInfo> customers = userInfoService.getCustomers();

        for (UserInfo customer : customers) {
            Object[] rowData = {customer.getUserID(), customer.getUserName(), customer.getGender(),
                    customer.getEmail(), customer.getPhone(), customer.getAddress(), customer.getBirthday()};
            tableModel.addRow(rowData);
        }
    }

    private void addCustomer() {
        // Get data from input fields
        String customerName = customerNameField.getText();
        String gender = genderField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        Date birthday = birthdayChooser.getDate();

        // Validate input
        if (customerName.isEmpty() || gender.isEmpty()
                || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new customer object with default roleID = 3 (Customer)
        UserInfo customer = new UserInfo(customerName, "123", 3, gender, email, phone, address, birthday);

        // Add customer to database
        boolean success = userInfoService.addUser(customer);

        if (success) {
            JOptionPane.showMessageDialog(this, "Customer added successfully");
            displayCustomers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerID = Integer.parseInt(customerIDField.getText());
        String customerName = customerNameField.getText();
        String gender = genderField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        Date birthday = birthdayChooser.getDate();

        // Validate input
        if (customerName.isEmpty() || gender.isEmpty()
                || email.isEmpty() || phone.isEmpty() || address.isEmpty() || birthday == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a customer object
        UserInfo customer = new UserInfo(customerID, customerName, "123", 3, gender, email, phone, address, birthday);

        // Update customer in database
        boolean success = userInfoService.updateUser(customer);

        if (success) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully");
            displayCustomers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerID = Integer.parseInt(customerIDField.getText());

        // Delete customer from database
        boolean success = userInfoService.deleteUser(customerID);

        if (success) {
            JOptionPane.showMessageDialog(this, "Customer deleted successfully");
            displayCustomers();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete customer", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        customerIDField.setText("");
        customerNameField.setText("");
        genderField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        birthdayChooser.setDate(null);
    }
}
