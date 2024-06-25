package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import Controller.TheatreService;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import model.Theatre;

public class ManageTheatrePanel extends JPanel {
    private JTextField theatreIDField;
    private JTextField theatreNameField;
    private JTextField screenField; // Thêm trường screen
    private JTextField seatCapacityField;
    private JTable theatreTable;
    private DefaultTableModel tableModel;
    private TheatreService theatreService;

    public ManageTheatrePanel() {
        theatreService = new TheatreService();
        setLayout(new BorderLayout());

        // Tiêu đề ở phía bắc
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Quản lý phòng chiếu"));
        add(topPanel, BorderLayout.NORTH);

        // Panel trung tâm
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel theatrePanel = new JPanel();
        theatrePanel.setLayout(new BorderLayout());
        theatrePanel.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng chiếu"));

        centerPanel.add(theatrePanel, BorderLayout.NORTH);

        JPanel northOfTheatrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTheatreButton = new JButton("Thêm phòng chiếu");
        JButton editTheatreButton = new JButton("Sửa phòng chiếu");
        JButton deleteTheatreButton = new JButton("Xóa phòng chiếu");

        northOfTheatrePanel.add(addTheatreButton);
        northOfTheatrePanel.add(editTheatreButton);
        northOfTheatrePanel.add(deleteTheatreButton);

        theatrePanel.add(northOfTheatrePanel, BorderLayout.SOUTH);

        JPanel infoTheatrePanel = new JPanel();
        infoTheatrePanel.setLayout(new GridLayout(4, 2, 20, 30)); // Thêm một hàng mới cho trường screen
        theatrePanel.add(infoTheatrePanel, BorderLayout.CENTER);

        JLabel lblTheatreID = new JLabel("Mã Phòng chiếu:");
        theatreIDField = new JTextField();
        theatreIDField.setEnabled(false);
        theatreIDField.setDisabledTextColor(Color.BLACK);

        JLabel lblTheatreName = new JLabel("Tên Phòng chiếu:");
        theatreNameField = new JTextField();

        JLabel lblScreen = new JLabel("Màn hình:");
        screenField = new JTextField(); // Thêm trường screen

        JLabel lblSeatCapacity = new JLabel("Sức chứa:");
        seatCapacityField = new JTextField();

        infoTheatrePanel.add(lblTheatreID);
        infoTheatrePanel.add(theatreIDField);
        infoTheatrePanel.add(lblTheatreName);
        infoTheatrePanel.add(theatreNameField);
        infoTheatrePanel.add(lblScreen);
        infoTheatrePanel.add(screenField); // Thêm trường screen
        infoTheatrePanel.add(lblSeatCapacity);
        infoTheatrePanel.add(seatCapacityField);

        // Bảng danh sách phòng chiếu
        JPanel theatreListPanel = new JPanel(new BorderLayout());
        theatreListPanel.setBorder(BorderFactory.createTitledBorder("Danh sách Phòng chiếu"));

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Mã Phòng chiếu");
        tableModel.addColumn("Tên Phòng chiếu");
        tableModel.addColumn("Màn hình"); // Thêm cột cho screen
        tableModel.addColumn("Sức chứa");

        theatreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(theatreTable);

        theatreListPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(theatreListPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Hiển thị danh sách phòng chiếu khi khởi tạo
        displayTheatres();

        // Sự kiện thêm phòng chiếu
        addTheatreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTheatre();
            }
        });

        // Sự kiện sửa phòng chiếu
        editTheatreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTheatre();
            }
        });

        // Sự kiện xóa phòng chiếu
        deleteTheatreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTheatre();
            }
        });

        // Sự kiện chọn dòng trong bảng phòng chiếu
        theatreTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Kiểm tra xem đã chọn một dòng trong bảng chưa
                if (!e.getValueIsAdjusting() && theatreTable.getSelectedRow() != -1) {
                    // Lấy thông tin từ dòng đã chọn
                    int selectedRow = theatreTable.getSelectedRow();
                    int theatreID = (int) theatreTable.getValueAt(selectedRow, 0);
                    String theatreName = (String) theatreTable.getValueAt(selectedRow, 1);
                    String screen = (String) theatreTable.getValueAt(selectedRow, 2); // Lấy thông tin từ cột screen
                    int seatCapacity = (int) theatreTable.getValueAt(selectedRow, 3);

                    // Đổ thông tin lên các trường nhập liệu
                    theatreIDField.setText(String.valueOf(theatreID));
                    theatreNameField.setText(theatreName);
                    screenField.setText(screen); // Hiển thị thông tin trong trường screen
                    seatCapacityField.setText(String.valueOf(seatCapacity));
                }
            }
        });
    }

    // Hiển thị danh sách phòng chiếu
    private void displayTheatres() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        // Lấy danh sách phòng chiếu từ CSDL
        List<Theatre> theatres = theatreService.getAllTheatres();

        // Hiển thị danh sách phòng chiếu trên bảng
        for (Theatre theatre : theatres) {
            Object[] rowData = {theatre.getTheatreID(), theatre.getTheatreName(), theatre.getScreen(), theatre.getSeatCapacity()}; // Thêm thông tin về screen
            tableModel.addRow(rowData);
        }
    }

    // Thêm phòng chiếu mới
    private void addTheatre() {
        // Lấy thông tin từ các trường nhập liệu
        String theatreName = theatreNameField.getText();
        String screen = screenField.getText(); // Lấy thông tin từ trường screen
        int seatCapacity = Integer.parseInt(seatCapacityField.getText());

        // Tạo đối tượng phòng chiếu mới
        Theatre theatre = new Theatre(theatreName, screen, seatCapacity); // Thêm thông tin về screen

        // Thêm phòng chiếu vào CSDL
        boolean success = theatreService.addTheatre(theatre);

        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm phòng chiếu thành công");
            displayTheatres(); // Hiển thị lại danh sách phòng chiếu sau khi thêm thành công
            clearFields(); // Xóa dữ liệu trong các trường nhập liệu
        } else {
            JOptionPane.showMessageDialog(this, "Thêm phòng chiếu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Sửa thông tin phòng chiếu
    private void editTheatre() {
        // Kiểm tra xem đã chọn một phòng chiếu từ bảng chưa
        int selectedRow = theatreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng chiếu để sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy thông tin từ các trường nhập liệu
        int theatreID = Integer.parseInt(theatreIDField.getText());
        String theatreName = theatreNameField.getText();
        String screen = screenField.getText(); // Lấy thông tin từ trường screen
        int seatCapacity = Integer.parseInt(seatCapacityField.getText());

        // Tạo đối tượng phòng chiếu cần sửa
        Theatre theatre = new Theatre(theatreID, theatreName, screen, seatCapacity); // Thêm thông tin về screen

        // Sửa thông tin phòng chiếu trong CSDL
        boolean success = theatreService.updateTheatre(theatre);

        if (success) {
            JOptionPane.showMessageDialog(this, "Sửa thông tin phòng chiếu thành công");
            displayTheatres(); // Hiển thị lại danh sách phòng chiếu sau khi sửa thành công
            clearFields(); // Xóa dữ liệu trong các trường nhập liệu
        } else {
            JOptionPane.showMessageDialog(this, "Sửa thông tin phòng chiếu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xóa phòng chiếu
    private void deleteTheatre() {
        // Kiểm tra xem đã chọn một phòng chiếu từ bảng chưa
        int selectedRow = theatreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng chiếu để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy mã phòng chiếu từ dòng đã chọn
        int theatreID = Integer.parseInt(theatreIDField.getText());

        // Xóa phòng chiếu trong CSDL
        boolean success = theatreService.deleteTheatre(theatreID);

        if (success) {
            JOptionPane.showMessageDialog(this, "Xóa phòng chiếu thành công");
            displayTheatres(); // Hiển thị lại danh sách phòng chiếu sau khi xóa thành công
            clearFields(); // Xóa dữ liệu trong các trường nhập liệu
        } else {
            JOptionPane.showMessageDialog(this, "Xóa phòng chiếu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xóa dữ liệu trong các trường nhập liệu
    private void clearFields() {
        theatreIDField.setText("");
        theatreNameField.setText("");
        screenField.setText(""); // Xóa dữ liệu trong trường screen
        seatCapacityField.setText("");
    }
}
