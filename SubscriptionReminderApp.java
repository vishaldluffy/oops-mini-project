import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionReminderApp extends JFrame {

    private List<Subscription> subscriptions;
    private DefaultTableModel tableModel;
    private JTable table;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SubscriptionReminderApp() {
        subscriptions = new ArrayList<>();
        initializeUI();
        checkReminders();
    }

    private void initializeUI() {
        setTitle("Subscription Renewal Reminder");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("Subscription Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Service", "Cost", "Renewal Date", "Days Left", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton addBtn = createStyledButton("Add Subscription", new Color(46, 204, 113));
        JButton removeBtn = createStyledButton("Remove Selected", new Color(231, 76, 60));
        JButton checkBtn = createStyledButton("Check Reminders", new Color(52, 152, 219));
        JButton renewBtn = createStyledButton("Renew Selected", new Color(241, 196, 15));

        addBtn.addActionListener(e -> showAddDialog());
        removeBtn.addActionListener(e -> removeSelected());
        checkBtn.addActionListener(e -> checkReminders());
        renewBtn.addActionListener(e -> renewSelected());

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(renewBtn);
        buttonPanel.add(checkBtn);

        add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add Subscription", true);
        dialog.setSize(450, 320);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create text fields with proper settings
        JTextField serviceField = new JTextField(20);
        JTextField costField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        
        // Set default date
        dateField.setText(LocalDate.now().format(formatter));
        
        // Ensure text fields are editable
        serviceField.setEditable(true);
        costField.setEditable(true);
        dateField.setEditable(true);
        
        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Service Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(serviceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Cost ($):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(costField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Renewal Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(dateField, gbc);
        
        // Format hint
        gbc.gridx = 1;
        gbc.gridy = 3;
        JLabel formatLabel = new JLabel("(Format: yyyy-MM-dd)");
        formatLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formatLabel.setForeground(Color.GRAY);
        mainPanel.add(formatLabel, gbc);

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton saveBtn = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));
        
        saveBtn.addActionListener(e -> {
            try {
                String service = serviceField.getText().trim();
                String costText = costField.getText().trim();
                String dateText = dateField.getText().trim();

                if (service.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Service name cannot be empty!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    serviceField.requestFocus();
                    return;
                }
                
                if (costText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Cost cannot be empty!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    costField.requestFocus();
                    return;
                }

                double cost = Double.parseDouble(costText);
                
                if (cost < 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Cost must be positive!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    costField.requestFocus();
                    return;
                }
                
                LocalDate renewalDate = LocalDate.parse(dateText, formatter);

                subscriptions.add(new Subscription(service, cost, renewalDate));
                updateTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    "Subscription added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid cost! Please enter a valid number.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                costField.requestFocus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid date format! Please use yyyy-MM-dd (e.g., 2025-12-31)", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                dateField.requestFocus();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set default button for Enter key
        dialog.getRootPane().setDefaultButton(saveBtn);
        
        // Request focus on first field when dialog opens
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> serviceField.requestFocusInWindow());
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this subscription?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                subscriptions.remove(selectedRow);
                updateTable();
                JOptionPane.showMessageDialog(this, 
                    "Subscription removed successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a subscription to remove.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void renewSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Subscription sub = subscriptions.get(selectedRow);
            sub.setRenewalDate(sub.getRenewalDate().plusYears(1));
            updateTable();
            JOptionPane.showMessageDialog(this, 
                "Subscription renewed for another year!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a subscription to renew.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkReminders() {
        StringBuilder reminders = new StringBuilder();
        LocalDate today = LocalDate.now();

        for (Subscription sub : subscriptions) {
            long daysLeft = ChronoUnit.DAYS.between(today, sub.getRenewalDate());
            if (daysLeft <= 7 && daysLeft >= 0) {
                reminders.append(String.format("⚠ %s renews in %d days!\n", 
                    sub.getServiceName(), daysLeft));
            } else if (daysLeft < 0) {
                reminders.append(String.format("❌ %s is overdue by %d days!\n", 
                    sub.getServiceName(), Math.abs(daysLeft)));
            }
        }

        if (reminders.length() > 0) {
            JOptionPane.showMessageDialog(this, reminders.toString(),
                    "Renewal Reminders", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "✓ No upcoming renewals within 7 days!",
                    "All Clear", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();

        for (Subscription sub : subscriptions) {
            long daysLeft = ChronoUnit.DAYS.between(today, sub.getRenewalDate());
            String status;

            if (daysLeft < 0) {
                status = "Overdue";
            } else if (daysLeft <= 7) {
                status = "Due Soon";
            } else if (daysLeft <= 30) {
                status = "Upcoming";
            } else {
                status = "Active";
            }

            tableModel.addRow(new Object[]{
                    sub.getServiceName(),
                    String.format("$%.2f", sub.getCost()),
                    sub.getRenewalDate().format(formatter),
                    daysLeft + " days",
                    status
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SubscriptionReminderApp app = new SubscriptionReminderApp();
            app.setVisible(true);
        });
    }
}

// Subscription Class
class Subscription {
    private String serviceName;
    private double cost;
    private LocalDate renewalDate;

    public Subscription(String serviceName, double cost, LocalDate renewalDate) {
        this.serviceName = serviceName;
        this.cost = cost;
        this.renewalDate = renewalDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }
}
