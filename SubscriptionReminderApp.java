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

 dialog.setSize(400, 300);

 dialog.setLayout(new GridBagLayout());

 GridBagConstraints gbc = new GridBagConstraints();

 gbc.insets = new Insets(10, 10, 10, 10);

 gbc.fill = GridBagConstraints.HORIZONTAL;

 

 JTextField serviceField = new JTextField(20);

 JTextField costField = new JTextField(20);

 JTextField dateField = new JTextField(20);

 dateField.setText(LocalDate.now().format(formatter));

 

 gbc.gridx = 0; gbc.gridy = 0;

 dialog.add(new JLabel("Service Name:"), gbc);

 gbc.gridx = 1;

 dialog.add(serviceField, gbc);

 

 gbc.gridx = 0; gbc.gridy = 1;

 dialog.add(new JLabel("Cost ($):"), gbc);

 gbc.gridx = 1;

 dialog.add(costField, gbc);

 

 gbc.gridx = 0; gbc.gridy = 2;

 dialog.add(new JLabel("Renewal Date (yyyy-MM-dd):"), gbc);

 gbc.gridx = 1;

 dialog.add(dateField, gbc);

 

 JButton saveBtn = createStyledButton("Save", new Color(46, 204, 113));
saveBtn.addActionListener(e -> {

 try {

 String service = serviceField.getText().trim();

 double cost = Double.parseDouble(costField.getText().trim());

 LocalDate renewalDate = LocalDate.parse(dateField.getText().trim(), formatter);

 

 if (service.isEmpty()) {

 JOptionPane.showMessageDialog(dialog, "Service name cannot be empty!");

 return;

 }

 

 subscriptions.add(new Subscription(service, cost, renewalDate));

 updateTable();

 dialog.dispose();

 JOptionPane.showMessageDialog(this, "Subscription added successfully!");

 } catch (Exception ex) {

 JOptionPane.showMessageDialog(dialog, "Invalid input! Please check your data.");

 }

 });

 

 gbc.gridx = 0; gbc.gridy = 3;

 gbc.gridwidth = 2;

 gbc.anchor = GridBagConstraints.CENTER;

 dialog.add(saveBtn, gbc);

 

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

 }

 } else {

 JOptionPane.showMessageDialog(this, "Please select a subscription to remove.");

 }

 }

 

 private void renewSelected() {

 int selectedRow = table.getSelectedRow();

 if (selectedRow >= 0) {

 Subscription sub = subscriptions.get(selectedRow);

 sub.setRenewalDate(sub.getRenewalDate().plusYears(1));

 updateTable();

 JOptionPane.showMessageDialog(this, 

 "Subscription renewed for another year!");

 } else {

 JOptionPane.showMessageDialog(this, "Please select a subscription to renew.");

 }

 }

 

 private void checkReminders() {

 StringBuilder reminders = new StringBuilder();

 LocalDate today = LocalDate.now();

 

 for (Subscription sub : subscriptions) {
long daysLeft = ChronoUnit.DAYS.between(today, sub.getRenewalDate());

 if (daysLeft <= 7 && daysLeft >= 0) {

 reminders.append(String.format(" %s renews in %d days!\n", 

 sub.getServiceName(), daysLeft));

 } else if (daysLeft < 0) {

 reminders.append(String.format(" %s is overdue by %d days!\n", 

 sub.getServiceName(), Math.abs(daysLeft)));

 }

 }

 

 if (reminders.length() > 0) {

 JOptionPane.showMessageDialog(this, reminders.toString(), 

 "Renewal Reminders", JOptionPane.WARNING_MESSAGE);

 } else {

 JOptionPane.showMessageDialog(this, 

 "No upcoming renewals within 7 days!", 

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
