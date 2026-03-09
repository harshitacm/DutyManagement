import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;

/**
 * DutySystemGUI.java
 * Main GUI for the Duty Management System.
 * Run this file to start the application.
 *
 * Compile:  javac *.java
 * Run:      java DutySystemGUI
 */
public class DutySystemGUI extends JFrame {

    // ── Managers ─────────────────────────────────────────────────────────────
    private final StaffManager staffManager = new StaffManager();
    private final DutyManager  dutyManager  = new DutyManager();

    // ── Navigation ───────────────────────────────────────────────────────────
    private JPanel     mainPanel;
    private CardLayout cardLayout;

    // ── Tables ───────────────────────────────────────────────────────────────
    private JTable             staffTable,     dutyTable;
    private DefaultTableModel  staffTableModel, dutyTableModel;

    // ── Dashboard labels ─────────────────────────────────────────────────────
    private JLabel totalStaffLabel, totalDutiesLabel,
                   pendingDutiesLabel, completedDutiesLabel;

    // ── Date formatter used throughout the GUI ───────────────────────────────
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────────────────

    public DutySystemGUI() {
        buildWindow();
        setVisible(true);
    }

    // =========================================================================
    // Window setup
    // =========================================================================

    private void buildWindow() {
        setTitle("Duty Management System");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        mainPanel.add(buildDashboard(),        "dashboard");
        mainPanel.add(buildStaffManagement(),  "staff");
        mainPanel.add(buildAddStaffForm(),     "addStaff");
        mainPanel.add(buildDutyAssignment(),   "assignDuty");
        mainPanel.add(buildViewDuties(),       "viewDuties");
        mainPanel.add(buildDutyRoster(),       "roster");

        buildMenuBar();
        add(mainPanel);
        refreshDashboard();
    }

    // =========================================================================
    // Menu bar
    // =========================================================================

    private void buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        // Dashboard
        JMenuItem dashItem = new JMenuItem("Dashboard");
        dashItem.addActionListener(e -> { refreshDashboard(); show("dashboard"); });

        // Staff menu
        JMenu staffMenu = new JMenu("Staff Management");
        JMenuItem viewStaff = new JMenuItem("View All Staff");
        viewStaff.addActionListener(e -> { loadStaffTable(); show("staff"); });
        JMenuItem addStaff = new JMenuItem("Add New Staff");
        addStaff.addActionListener(e -> show("addStaff"));
        staffMenu.add(viewStaff);
        staffMenu.add(addStaff);

        // Duty menu
        JMenu dutyMenu = new JMenu("Duty Management");
        JMenuItem assignDuty = new JMenuItem("Assign Duty");
        assignDuty.addActionListener(e -> show("assignDuty"));
        JMenuItem viewDuties = new JMenuItem("View All Duties");
        viewDuties.addActionListener(e -> { loadDutyTable(); show("viewDuties"); });
        JMenuItem roster = new JMenuItem("Generate Roster");
        roster.addActionListener(e -> show("roster"));
        dutyMenu.add(assignDuty);
        dutyMenu.add(viewDuties);
        dutyMenu.add(roster);

        // Exit
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        bar.add(dashItem);
        bar.add(staffMenu);
        bar.add(dutyMenu);
        bar.add(exitItem);
        setJMenuBar(bar);
    }

    private void show(String card) { cardLayout.show(mainPanel, card); }

    // =========================================================================
    // Dashboard
    // =========================================================================

    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Duty Management System – Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(title, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(2, 2, 30, 30));
        stats.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        totalStaffLabel     = statLabel("Total Staff: 0",      new Color(30, 100, 200));
        totalDutiesLabel    = statLabel("Total Duties: 0",     new Color(30, 160, 60));
        pendingDutiesLabel  = statLabel("Pending Duties: 0",   new Color(200, 120, 0));
        completedDutiesLabel= statLabel("Completed Duties: 0", new Color(0, 150, 0));

        stats.add(totalStaffLabel);
        stats.add(totalDutiesLabel);
        stats.add(pendingDutiesLabel);
        stats.add(completedDutiesLabel);
        panel.add(stats, BorderLayout.CENTER);
        return panel;
    }

    private JLabel statLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createLineBorder(color, 2));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(245, 245, 255));
        return lbl;
    }

    private void refreshDashboard() {
        totalStaffLabel.setText("Total Staff: " + staffManager.getTotalStaff());
        Map<String, Integer> s = dutyManager.getStatistics();
        totalDutiesLabel.setText    ("Total Duties: "     + s.get("Total"));
        pendingDutiesLabel.setText  ("Pending Duties: "   + s.get("Pending"));
        completedDutiesLabel.setText("Completed Duties: " + s.get("Completed"));
    }

    // =========================================================================
    // Staff Management (view + delete)
    // =========================================================================

    private JPanel buildStaffManagement() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = sectionTitle("Staff Management");
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Staff ID", "Name", "Department", "Role", "Contact", "Email"};
        staffTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(staffTableModel);
        staffTable.setRowHeight(22);
        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton del = new JButton("Delete Selected");
        del.addActionListener(e -> deleteSelectedStaff());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadStaffTable());
        btns.add(del);
        btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void loadStaffTable() {
        staffTableModel.setRowCount(0);
        for (Staff s : staffManager.getAllStaff()) {
            staffTableModel.addRow(new Object[]{
                s.getStaffId(), s.getName(), s.getDepartment(),
                s.getRole(), s.getContactNumber(), s.getEmail()
            });
        }
    }

    private void deleteSelectedStaff() {
        int row = staffTable.getSelectedRow();
        if (row < 0) { alert("Please select a staff member first."); return; }
        if (confirm("Delete this staff member?") == JOptionPane.YES_OPTION) {
            String id = (String) staffTableModel.getValueAt(row, 0);
            if (staffManager.deleteStaff(id)) {
                info("Staff deleted successfully.");
                loadStaffTable();
                refreshDashboard();
            } else {
                alert("Could not delete staff.");
            }
        }
    }

    // =========================================================================
    // Add Staff form
    // =========================================================================

    private JPanel buildAddStaffForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sectionTitle("Add New Staff Member"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(30, 120, 20, 120));

        JTextField idField      = new JTextField();
        JTextField nameField    = new JTextField();
        JTextField deptField    = new JTextField();
        JTextField roleField    = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField   = new JTextField();

        form.add(new JLabel("Staff ID:"));        form.add(idField);
        form.add(new JLabel("Name:"));            form.add(nameField);
        form.add(new JLabel("Department:"));      form.add(deptField);
        form.add(new JLabel("Role / Designation:")); form.add(roleField);
        form.add(new JLabel("Contact Number:"));  form.add(contactField);
        form.add(new JLabel("Email:"));           form.add(emailField);

        JButton save  = new JButton("Save Staff");
        JButton clear = new JButton("Clear");

        save.addActionListener(e -> {
            String id      = idField.getText().trim();
            String name    = nameField.getText().trim();
            String dept    = deptField.getText().trim();
            String role    = roleField.getText().trim();
            String contact = contactField.getText().trim();
            String email   = emailField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || dept.isEmpty() ||
                role.isEmpty() || contact.isEmpty()) {
                alert("Please fill all required fields (ID, Name, Dept, Role, Contact).");
                return;
            }
            Staff staff = new Staff(id, name, dept, contact, email, role);
            if (staffManager.addStaff(staff)) {
                info("Staff added successfully!");
                clearFields(idField, nameField, deptField, roleField, contactField, emailField);
                refreshDashboard();
            } else {
                alert("Staff ID already exists. Please use a unique ID.");
            }
        });

        clear.addActionListener(e ->
            clearFields(idField, nameField, deptField, roleField, contactField, emailField));

        JPanel btns = new JPanel();
        btns.add(save);
        btns.add(clear);
        form.add(btns);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // Assign Duty form
    // =========================================================================

    private JPanel buildDutyAssignment() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sectionTitle("Assign Duty"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(30, 120, 20, 120));

        JComboBox<Staff> staffCombo = new JComboBox<>();
        refreshStaffCombo(staffCombo);

        JTextField dateField      = new JTextField(LocalDate.now().format(DATE_FMT));
        JTextField startTimeField = new JTextField("09:00");
        JTextField endTimeField   = new JTextField("17:00");
        JComboBox<String> shiftCombo =
            new JComboBox<>(new String[]{"Morning", "Evening", "Night"});
        JTextField locationField  = new JTextField();
        JTextField notesField     = new JTextField();

        form.add(new JLabel("Select Staff:"));             form.add(staffCombo);
        form.add(new JLabel("Date (DD/MM/YYYY):"));        form.add(dateField);
        form.add(new JLabel("Start Time (HH:MM):"));       form.add(startTimeField);
        form.add(new JLabel("End Time (HH:MM):"));         form.add(endTimeField);
        form.add(new JLabel("Shift Type:"));               form.add(shiftCombo);
        form.add(new JLabel("Location:"));                 form.add(locationField);
        form.add(new JLabel("Notes (optional):"));         form.add(notesField);

        JButton assign = new JButton("Assign Duty");
        JButton clear  = new JButton("Clear");

        assign.addActionListener(e -> {
            Staff selected = (Staff) staffCombo.getSelectedItem();
            if (selected == null) { alert("No staff members found. Add staff first."); return; }

            String dateText  = dateField.getText().trim();
            String startText = startTimeField.getText().trim();
            String endText   = endTimeField.getText().trim();
            String location  = locationField.getText().trim();
            String shift     = (String) shiftCombo.getSelectedItem();

            if (dateText.isEmpty() || startText.isEmpty() ||
                endText.isEmpty()  || location.isEmpty()) {
                alert("Please fill all required fields (Date, Times, Location).");
                return;
            }

            LocalDate date;
            LocalTime startTime, endTime;
            try {
                date      = LocalDate.parse(dateText, DATE_FMT);
                startTime = LocalTime.parse(startText);
                endTime   = LocalTime.parse(endText);
            } catch (DateTimeParseException ex) {
                alert("Invalid date/time format.\nDate: DD/MM/YYYY  |  Time: HH:MM");
                return;
            }

            if (!endTime.isAfter(startTime)) {
                alert("End time must be after start time.");
                return;
            }

            if (dutyManager.hasConflict(selected.getStaffId(), date, startTime, endTime)) {
                alert("Scheduling conflict! This staff member already has a duty\nthat overlaps with the requested time.");
                return;
            }

            Duty duty = new Duty(null, selected.getStaffId(), date,
                                 startTime, endTime, shift, location);
            duty.setNotes(notesField.getText().trim());
            String dutyId = dutyManager.addDuty(duty);
            info("Duty assigned successfully!\nDuty ID: " + dutyId);

            // Reset form to defaults
            dateField.setText(LocalDate.now().format(DATE_FMT));
            startTimeField.setText("09:00");
            endTimeField.setText("17:00");
            locationField.setText("");
            notesField.setText("");
            refreshDashboard();
        });

        clear.addActionListener(e -> {
            dateField.setText(LocalDate.now().format(DATE_FMT));
            startTimeField.setText("09:00");
            endTimeField.setText("17:00");
            locationField.setText("");
            notesField.setText("");
        });

        JPanel btns = new JPanel();
        btns.add(assign);
        btns.add(clear);
        form.add(btns);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private void refreshStaffCombo(JComboBox<Staff> combo) {
        combo.removeAllItems();
        staffManager.getAllStaff().forEach(combo::addItem);
    }

    // =========================================================================
    // View / manage all duties
    // =========================================================================

    private JPanel buildViewDuties() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sectionTitle("All Duties"), BorderLayout.NORTH);

        String[] cols = {"Duty ID", "Staff Name", "Date", "Time", "Shift", "Location", "Status"};
        dutyTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        dutyTable = new JTable(dutyTableModel);
        dutyTable.setRowHeight(22);
        panel.add(new JScrollPane(dutyTable), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton markDone = new JButton("Mark Complete");
        JButton cancel   = new JButton("Mark Cancelled");
        JButton delete   = new JButton("Delete Duty");
        JButton refresh  = new JButton("Refresh");

        markDone.addActionListener(e -> updateSelectedDutyStatus("Completed"));
        cancel.addActionListener  (e -> updateSelectedDutyStatus("Cancelled"));
        delete.addActionListener  (e -> deleteSelectedDuty());
        refresh.addActionListener (e -> loadDutyTable());

        btns.add(markDone);
        btns.add(cancel);
        btns.add(delete);
        btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void loadDutyTable() {
        dutyTableModel.setRowCount(0);
        for (Duty d : dutyManager.getAllDuties()) {
            Staff s = staffManager.getStaff(d.getStaffId());
            String name = (s != null) ? s.getName() : "Unknown";
            dutyTableModel.addRow(new Object[]{
                d.getDutyId(), name, d.getDate(),
                d.getStartTime() + " – " + d.getEndTime(),
                d.getShiftType(), d.getLocation(), d.getStatus()
            });
        }
    }

    private void updateSelectedDutyStatus(String status) {
        int row = dutyTable.getSelectedRow();
        if (row < 0) { alert("Please select a duty first."); return; }
        String id = (String) dutyTableModel.getValueAt(row, 0);
        if (dutyManager.updateDutyStatus(id, status)) {
            info("Duty marked as " + status + ".");
            loadDutyTable();
            refreshDashboard();
        } else {
            alert("Could not update duty status.");
        }
    }

    private void deleteSelectedDuty() {
        int row = dutyTable.getSelectedRow();
        if (row < 0) { alert("Please select a duty first."); return; }
        if (confirm("Delete this duty permanently?") == JOptionPane.YES_OPTION) {
            String id = (String) dutyTableModel.getValueAt(row, 0);
            if (dutyManager.deleteDuty(id)) {
                info("Duty deleted successfully.");
                loadDutyTable();
                refreshDashboard();
            } else {
                alert("Could not delete duty.");
            }
        }
    }

    // =========================================================================
    // Duty Roster
    // =========================================================================

    private JPanel buildDutyRoster() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sectionTitle("Generate Duty Roster"), BorderLayout.NORTH);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        JTextField fromField = new JTextField(10);
        JTextField toField   = new JTextField(10);
        fromField.setText(LocalDate.now().format(DATE_FMT));
        toField.setText(LocalDate.now().plusDays(6).format(DATE_FMT));
        JButton generateBtn = new JButton("Generate Roster");

        inputRow.add(new JLabel("From (DD/MM/YYYY):"));
        inputRow.add(fromField);
        inputRow.add(new JLabel("To (DD/MM/YYYY):"));
        inputRow.add(toField);
        inputRow.add(generateBtn);

        JTextArea rosterArea = new JTextArea();
        rosterArea.setEditable(false);
        rosterArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(rosterArea);
        scroll.setPreferredSize(new Dimension(900, 420));

        generateBtn.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(fromField.getText().trim(), DATE_FMT);
                LocalDate to   = LocalDate.parse(toField.getText().trim(), DATE_FMT);
                if (to.isBefore(from)) { alert("'To' date must not be before 'From' date."); return; }

                ArrayList<Duty> duties = dutyManager.getDutiesByDateRange(from, to);
                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════════════════════════\n");
                sb.append("                      DUTY ROSTER                         \n");
                sb.append("═══════════════════════════════════════════════════════════\n");
                sb.append(String.format("  Period : %s  to  %s%n", from, to));
                sb.append(String.format("  Total  : %d duties%n", duties.size()));
                sb.append("───────────────────────────────────────────────────────────\n");

                if (duties.isEmpty()) {
                    sb.append("\n  No duties scheduled for this period.\n");
                } else {
                    LocalDate currentDate = null;
                    for (Duty d : duties) {
                        if (!d.getDate().equals(currentDate)) {
                            currentDate = d.getDate();
                            sb.append(String.format("%n  ▶ %s%n", currentDate));
                            sb.append("  " + "─".repeat(54) + "\n");
                        }
                        Staff s = staffManager.getStaff(d.getStaffId());
                        String staffName = (s != null) ? s.getName() : "Unknown";
                        sb.append(String.format("  %-6s │ %s–%s │ %-7s │ %-18s │ %-10s │ %s%n",
                            d.getDutyId(),
                            d.getStartTime(), d.getEndTime(),
                            d.getShiftType(),
                            staffName,
                            d.getLocation(),
                            d.getStatus()));
                    }
                }
                sb.append("\n═══════════════════════════════════════════════════════════\n");
                rosterArea.setText(sb.toString());
                rosterArea.setCaretPosition(0);

            } catch (DateTimeParseException ex) {
                alert("Invalid date format. Please use DD/MM/YYYY.");
            }
        });

        panel.add(inputRow, BorderLayout.CENTER);
        panel.add(scroll,   BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // Utility helpers
    // =========================================================================

    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 0, 10, 0));
        return lbl;
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    private void alert(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private int confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm",
               JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    // =========================================================================
    // Entry point
    // =========================================================================

    public static void main(String[] args) {
        // Use native OS look-and-feel for a cleaner appearance
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(DutySystemGUI::new);
    }
}