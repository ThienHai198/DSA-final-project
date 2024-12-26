import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

class GUI {

    Phone phoneBook;
    DefaultTableModel tableModel;
    JTable contactTable;
    Deque<Runnable> undoStack;

    public GUI() {
        phoneBook = new Phone();
        undoStack = new ArrayDeque<>();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Phone Book Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));

        // Initialize table
        tableModel = new DefaultTableModel(new String[]{"Name", "Phone"}, 0);
        contactTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        frame.add(searchPanel, BorderLayout.NORTH);

        // Sort button
        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> sortTable());
        frame.add(sortButton, BorderLayout.WEST);

        // Action button
        JButton actionButton = new JButton("Actions");
        actionButton.addActionListener(e -> openActionWindow());
        frame.add(actionButton, BorderLayout.SOUTH);

        // Undo button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                undoStack.pop().run();
                updateTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Nothing to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        frame.add(undoButton, BorderLayout.EAST);

        // Search function
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (!query.isEmpty()) {
                DefaultTableModel searchResultsModel = new DefaultTableModel(new String[]{"Name", "Phone"}, 0);
                for (int i = 0; i < phoneBook.nameList.size(); i++) {
                    if (phoneBook.nameList.get(i).toLowerCase().contains(query) || phoneBook.phoneList.get(i).toLowerCase().contains(query)) {
                        searchResultsModel.addRow(new Object[]{phoneBook.nameList.get(i), phoneBook.phoneList.get(i)});
                    }
                }

                if (searchResultsModel.getRowCount() > 0) {
                    JFrame resultsFrame = new JFrame("Search Results");
                    resultsFrame.setSize(400, 300);
                    JTable resultsTable = new JTable(searchResultsModel);
                    JScrollPane scrollPane = new JScrollPane(resultsTable);
                    resultsFrame.add(scrollPane);
                    resultsFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "No contact found for: " + query, "Search Result", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a Name or Phone to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Populate table
        updateTable();

        frame.setVisible(true);
    }

    private void openActionWindow() {
        JFrame actionFrame = new JFrame("Actions");
        actionFrame.setSize(300, 200);
        actionFrame.setLayout(new GridLayout(3, 1, 10, 10));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> openAddWindow());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> openDeleteWindow());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> openEditWindow());

        actionFrame.add(addButton);
        actionFrame.add(deleteButton);
        actionFrame.add(editButton);

        actionFrame.setVisible(true);
    }

    private void openAddWindow() {
        JFrame addFrame = new JFrame("Add Contact");
        addFrame.setSize(300, 200);
        addFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JButton saveButton = new JButton("Save");

        addFrame.add(nameLabel);
        addFrame.add(nameField);
        addFrame.add(phoneLabel);
        addFrame.add(phoneField);
        addFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                phoneBook.addName(name, phone);
                undoStack.push(() -> phoneBook.delete(name));
                updateTable();
                addFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(addFrame, "Please fill in both Name and Phone fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addFrame.setVisible(true);
    }

    private void openDeleteWindow() {
        JFrame deleteFrame = new JFrame("Delete Contact");
        deleteFrame.setSize(300, 150);
        deleteFrame.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JButton confirmButton = new JButton("Delete");

        deleteFrame.add(nameLabel);
        deleteFrame.add(nameField);
        deleteFrame.add(confirmButton);

        confirmButton.addActionListener(e -> {
            String nameToDelete = nameField.getText().trim();
            if (!nameToDelete.isEmpty()) {
                int index = phoneBook.binarySearch(nameToDelete);
                if (index != -1) {
                    String deletedName = phoneBook.nameList.get(index);
                    String deletedPhone = phoneBook.phoneList.get(index);
                    phoneBook.delete(nameToDelete);
                    undoStack.push(() -> phoneBook.addName(deletedName, deletedPhone));
                    updateTable();
                    deleteFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(deleteFrame, "Contact not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(deleteFrame, "Please enter a Name to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteFrame.setVisible(true);
    }

    private void openEditWindow() {
        JFrame editFrame = new JFrame("Edit Contact");
        editFrame.setSize(300, 200);
        editFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel nameLabel = new JLabel("Existing Name:");
        JTextField existingNameField = new JTextField();
        JLabel newNameLabel = new JLabel("New Name:");
        JTextField newNameField = new JTextField();
        JLabel newPhoneLabel = new JLabel("New Phone:");
        JTextField newPhoneField = new JTextField();
        JButton saveButton = new JButton("Save");

        editFrame.add(nameLabel);
        editFrame.add(existingNameField);
        editFrame.add(newNameLabel);
        editFrame.add(newNameField);
        editFrame.add(newPhoneLabel);
        editFrame.add(newPhoneField);
        editFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String existingName = existingNameField.getText().trim();
            String newName = newNameField.getText().trim();
            String newPhone = newPhoneField.getText().trim();

            if (!existingName.isEmpty() && !newName.isEmpty() && !newPhone.isEmpty()) {
                int index = phoneBook.binarySearch(existingName);
                if (index != -1) {
                    String oldName = phoneBook.nameList.get(index);
                    String oldPhone = phoneBook.phoneList.get(index);
                    phoneBook.nameList.set(index, newName);
                    phoneBook.phoneList.set(index, newPhone);
                    phoneBook.quickSort(0, phoneBook.nameList.size() - 1);
                    undoStack.push(() -> {
                        phoneBook.nameList.set(index, oldName);
                        phoneBook.phoneList.set(index, oldPhone);
                        phoneBook.quickSort(0, phoneBook.nameList.size() - 1);
                    });
                    updateTable();
                    editFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(editFrame, "Contact not found.", "Edit Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(editFrame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editFrame.setVisible(true);
    }

    private void sortTable() {
        phoneBook.quickSort(0, phoneBook.nameList.size() - 1);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < phoneBook.nameList.size(); i++) {
            tableModel.addRow(new Object[]{phoneBook.nameList.get(i), phoneBook.phoneList.get(i)});
        }
    }


}