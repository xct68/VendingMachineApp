package isl.com;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class VendingMachineGUI {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JTable stockTable;
    private DefaultTableModel tableModel;

    public VendingMachineGUI() {
        frame = new JFrame("Vending Machine Stock Manager");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Start with Client 1 as the default tab
        addNewClientTab("Client 1");

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton updateButton = new JButton("Update Item");
        JButton openButton = new JButton("Open File");
        JButton addClientButton = new JButton("Add Client");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(openButton);
        buttonPanel.add(addClientButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Action listener for adding a new client
        addClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clientName = JOptionPane.showInputDialog(frame, "Enter client name:");
                if (clientName != null && !clientName.trim().isEmpty()) {
                    addNewClientTab(clientName);
                }
            }
        });

        // Action listener for adding an item
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = JOptionPane.showInputDialog(frame, "Enter item name:");
                String quantityStr = JOptionPane.showInputDialog(frame, "Enter quantity:");
                String priceStr = JOptionPane.showInputDialog(frame, "Enter price:");
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    double price = Double.parseDouble(priceStr);
                    StockManager.addItem(itemName, quantity, price);
                    updateTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for removing an item
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = stockTable.getSelectedRow();
                if (selectedRow != -1) {
                    String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                    StockManager.getStock().remove(itemName);
                    updateTable();
                    StockManager.saveStockToExcel("stock.xlsx");
                } else {
                    JOptionPane.showMessageDialog(frame, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for updating an item
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = stockTable.getSelectedRow();
                if (selectedRow != -1) {
                    String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                    StockItem selectedItem = StockManager.getStock().get(itemName);
                    String quantityStr = JOptionPane.showInputDialog(frame, "Enter new quantity:", selectedItem.getQuantity());
                    String priceStr = JOptionPane.showInputDialog(frame, "Enter new price:", selectedItem.getPrice());
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);
                        selectedItem.setQuantity(quantity);
                        selectedItem.setPrice(price);
                        updateTable();
                        StockManager.saveStockToExcel("stock.xlsx");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for opening a file
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    StockManager.loadStockFromExcel(selectedFile.getAbsolutePath());
                    updateTable();
                }
            }
        });

        // Update the table when switching between clients
        tabbedPane.addChangeListener(e -> {
            String clientName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
            StockManager.setActiveClient(clientName);
            updateTable();
        });
    }

    private void addNewClientTab(String clientName) {
        // Set the active client in StockManager
        StockManager.setActiveClient(clientName);

        // Create a new tab with a table
        JPanel clientPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price ($)"}, 0);
        stockTable = new JTable(tableModel);
        clientPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        tabbedPane.addTab(clientName, clientPanel);
        tabbedPane.setSelectedComponent(clientPanel);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        Map<String, StockItem> stock = StockManager.getStock();
        for (Map.Entry<String, StockItem> entry : stock.entrySet()) {
            StockItem item = entry.getValue();
            tableModel.addRow(new Object[]{item.getName(), item.getQuantity(), item.getPrice()});
        }
    }
}
