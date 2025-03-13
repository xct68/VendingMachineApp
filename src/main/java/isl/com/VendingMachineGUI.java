package isl.com;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class VendingMachineGUI {
    private JFrame frame;
    private JTable stockTable;
    private DefaultTableModel tableModel;

    public VendingMachineGUI() {
        frame = new JFrame("Vending Machine Stock Manager");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price ($)"}, 0);
        stockTable = new JTable(tableModel);
        updateTable();

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton updateButton = new JButton("Update Item");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        frame.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = JOptionPane.showInputDialog("Enter item name:");
                String quantityStr = JOptionPane.showInputDialog("Enter quantity:");
                String priceStr = JOptionPane.showInputDialog("Enter price:");

                if (item != null && quantityStr != null && priceStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);
                        StockManager.addItem(item, quantity, price);
                        updateTable();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = JOptionPane.showInputDialog("Enter item to remove:");
                if (item != null) {
                    StockManager.removeItem(item);
                    updateTable();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = JOptionPane.showInputDialog("Enter item to update:");
                String quantityStr = JOptionPane.showInputDialog("Enter new quantity:");
                String priceStr = JOptionPane.showInputDialog("Enter new price:");

                if (item != null && quantityStr != null && priceStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);
                        StockManager.updateItem(item, quantity, price);
                        updateTable();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<String, StockItem> entry : StockManager.getStock().entrySet()) {
            StockItem item = entry.getValue();
            tableModel.addRow(new Object[]{item.getName(), item.getQuantity(), item.getPrice()});
        }
    }
}
