import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// Enum for absence types
enum AbsenceType {
    LATE("Late"),
    EARLY_LEAVE("Early Leave"),
    LEAVE("Leave"),
    ABSENT("Absent");

    private final String displayName;

    AbsenceType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

// Class to represent a single absence record
class AbsenceRecord {
    private Date date;
    private int classNumber;
    private String courseName;
    private String studentName;
    private AbsenceType absenceType;
    private int id;
    private static int nextId = 1;

    public AbsenceRecord(Date date, int classNumber, String courseName, 
                         String studentName, AbsenceType absenceType) {
        this.id = nextId++;
        this.date = date;
        this.classNumber = classNumber;
        this.courseName = courseName;
        this.studentName = studentName;
        this.absenceType = absenceType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public int getClassNumber() { return classNumber; }
    public void setClassNumber(int classNumber) { this.classNumber = classNumber; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public AbsenceType getAbsenceType() { return absenceType; }
    public void setAbsenceType(AbsenceType absenceType) { this.absenceType = absenceType; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Date: %s | Class: %d | Course: %s | Student: %s | Type: %s",
                id, getFormattedDate(), classNumber, courseName, studentName, absenceType);
    }
}

// Custom Table Model
class AbsenceTableModel extends AbstractTableModel {
    private List<AbsenceRecord> records;
    private String[] columnNames = {"ID", "Date", "Class", "Course", "Student", "Absence Type"};

    public AbsenceTableModel(List<AbsenceRecord> records) {
        this.records = records;
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbsenceRecord record = records.get(rowIndex);
        switch (columnIndex) {
            case 0: return record.getId();
            case 1: return record.getFormattedDate();
            case 2: return record.getClassNumber();
            case 3: return record.getCourseName();
            case 4: return record.getStudentName();
            case 5: return record.getAbsenceType().toString();
            default: return null;
        }
    }

    public AbsenceRecord getRecordAt(int row) {
        if (row >= 0 && row < records.size()) {
            return records.get(row);
        }
        return null;
    }

    public void addRecord(AbsenceRecord record) {
        records.add(record);
        fireTableRowsInserted(records.size() - 1, records.size() - 1);
    }

    public void updateRecord(int row, AbsenceRecord record) {
        records.set(row, record);
        fireTableRowsUpdated(row, row);
    }

    public void removeRecord(int row) {
        records.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setRecords(List<AbsenceRecord> records) {
        this.records = records;
        fireTableDataChanged();
    }
}

// Custom button with hover effect
class CustomButton extends JButton {
    private Color defaultColor;
    private Color hoverColor;
    private Color pressedColor;

    public CustomButton(String text) {
        super(text);
     initColors();
        setupButtonUI();
    }

    private void initColors() {
        defaultColor = new Color(66, 133, 244); // Google blue
        hoverColor = new Color(41, 98, 198);    // Darker blue
        pressedColor = new Color(30, 78, 158);  // Even darker blue
    }

    private void setupButtonUI() {
        setBackground(defaultColor);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setFont(new Font("Arial", Font.PLAIN, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
            }
        });
    }
}

// Main GUI Class with Colors
public class AttendanceSystemGUI extends JFrame {
    private List<AbsenceRecord> records;
    private AbsenceTableModel tableModel;
    private JTable recordsTable;
    private JTextArea detailsArea;
    private JTextField searchField;
    private JComboBox<String> studentComboBox;
    private Map<String, Integer> studentGroups;

    // GUI Components
    private JButton addButton, editButton, deleteButton, viewButton;
    private JButton checkButton, statsButton, clearButton;
    private JPanel mainPanel, buttonPanel, formPanel, statsPanel;
    private JTextField dateField, classField, courseField, studentField;
    private JComboBox<AbsenceType> typeComboBox;
    private JLabel statusLabel;

    // Color palette
    private final Color PRIMARY_COLOR = new Color(66, 133, 244);       // Main blue
    private final Color SECONDARY_COLOR = new Color(52, 168, 83);     // Green
    private final Color WARNING_COLOR = new Color(217, 48, 37);        // Red
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);  // Light gray
    private final Color PANEL_COLOR = new Color(255, 255, 255);       // White
    private final Color TEXT_COLOR = new Color(32, 33, 36);           // Dark gray
    private final Color ACCENT_COLOR = new Color(251, 188, 4);        // Yellow
    
    public AttendanceSystemGUI() {
        records = new ArrayList<>();
        studentGroups = new HashMap<>();
        initializeSampleData();
        initComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeSampleData() {
        // Initialize sample student groups (3-5 students per group)
        String[] group1 = {"Alice Johnson", "Bob Smith", "Charlie Brown", "David Wilson"};
        String[] group2 = {"Emma Davis", "Frank Miller", "Grace Lee", "Henry Clark", "Ivy Taylor"};
        String[] group3 = {"Jack White", "Karen Hall", "Leo Scott"};

        for (String student : group1) studentGroups.put(student, 1);
        for (String student : group2) studentGroups.put(student, 2);
        for (String student : group3) studentGroups.put(student, 3);

        // Sample records
        Calendar cal = Calendar.getInstance();
        
        cal.set(2024, 0, 15);
        records.add(new AbsenceRecord(cal.getTime(), 1, "Mathematics", "Alice Johnson", AbsenceType.LATE));
        
        cal.set(2024, 0, 16);
        records.add(new AbsenceRecord(cal.getTime(), 2, "Physics", "Bob Smith", AbsenceType.ABSENT));
        
        cal.set(2024, 0, 17);
        records.add(new AbsenceRecord(cal.getTime(), 3, "Chemistry", "Charlie Brown", AbsenceType.LEAVE));
        
        cal.set(2024, 0, 18);
        records.add(new AbsenceRecord(cal.getTime(), 1, "Mathematics", "Alice Johnson", AbsenceType.EARLY_LEAVE));
        
        cal.set(2024, 0, 19);
        records.add(new AbsenceRecord(cal.getTime(), 2, "Physics", "David Wilson", AbsenceType.LATE));
    }

    private void initComponents() {
        setTitle("Student Attendance System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create main panel with tabbed interface
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL_COLOR);
        tabbedPane.setForeground(PRIMARY_COLOR);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Tab 1: Record Management
        JPanel recordsPanel = new JPanel(new BorderLayout(10, 10));
        recordsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        recordsPanel.setBackground(PANEL_COLOR);
        
        // Table for displaying records
        tableModel = new AbsenceTableModel(records);
        recordsTable = new JTable(tableModel);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordsTable.setBackground(Color.WHITE);
        recordsTable.setForeground(TEXT_COLOR);
        recordsTable.setGridColor(new Color(200, 200, 200));
        recordsTable.setRowHeight(25);
        
        // Set table header style
        JTableHeader header = recordsTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add alternating row colors
        recordsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });
        
        recordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsArea();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(recordsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Details area
        detailsArea = new JTextArea(8, 60);
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(245, 245, 245));
        detailsArea.setForeground(TEXT_COLOR);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), "Record Details", 
                    TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), PRIMARY_COLOR),
            new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(PANEL_COLOR);
        searchPanel.add(new JLabel("Search Student:", JLabel.RIGHT));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        searchPanel.add(searchField);
        
        JButton searchButton = new CustomButton("Search");
        searchButton.addActionListener(e -> searchRecords());
        searchPanel.add(searchButton);
        
        JButton refreshButton = new CustomButton("Refresh All");
        refreshButton.addActionListener(e -> refreshTable());
        searchPanel.add(refreshButton);
        
        recordsPanel.add(searchPanel, BorderLayout.NORTH);
        recordsPanel.add(tableScrollPane, BorderLayout.CENTER);
        recordsPanel.add(detailsScrollPane, BorderLayout.SOUTH);
        
        // Tab 2: Add/Edit Records
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Form fields with styled labels
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Color labelColor = PRIMARY_COLOR;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateLabel.setFont(labelFont);
        dateLabel.setForeground(labelColor);
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        dateField = new JTextField(20);
        dateField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel classLabel = new JLabel("Class Number:");
        classLabel.setFont(labelFont);
        classLabel.setForeground(labelColor);
        formPanel.add(classLabel, gbc);
        
        gbc.gridx = 1;
        classField = new JTextField(20);
        classField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        formPanel.add(classField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel courseLabel = new JLabel("Course Name:");
        courseLabel.setFont(labelFont);
        courseLabel.setForeground(labelColor);
        formPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        courseField = new JTextField(20);
        courseField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        formPanel.add(courseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel studentLabel = new JLabel("Student Name:");
        studentLabel.setFont(labelFont);
        studentLabel.setForeground(labelColor);
        formPanel.add(studentLabel, gbc);
        
        gbc.gridx = 1;
        studentField = new JTextField(20);
        studentField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        formPanel.add(studentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel typeLabel = new JLabel("Absence Type:");
        typeLabel.setFont(labelFont);
        typeLabel.setForeground(labelColor);
        formPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(AbsenceType.values());
        typeComboBox.setBackground(Color.WHITE);
        typeComboBox.setForeground(TEXT_COLOR);
        typeComboBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        formPanel.add(typeComboBox, gbc);
        
        // Student selection combo
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel selectStudentLabel = new JLabel("Select Student:");
        selectStudentLabel.setFont(labelFont);
        selectStudentLabel.setForeground(labelColor);
        formPanel.add(selectStudentLabel, gbc);
        
        gbc.gridx = 1;
        studentComboBox = new JComboBox<>(studentGroups.keySet().toArray(new String[0]));
        studentComboBox.setBackground(Color.WHITE);
        studentComboBox.setForeground(TEXT_COLOR);
        studentComboBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        studentComboBox.addActionListener(e -> {
            String selected = (String) studentComboBox.getSelectedItem();
            if (selected != null) {
                studentField.setText(selected);
            }
        });
        formPanel.add(studentComboBox, gbc);
        
        // Buttons for form
        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.setBackground(PANEL_COLOR);
        
        addButton = new CustomButton("Add Record");
        editButton = new CustomButton("Edit Selected");
        deleteButton = new CustomButton("Delete Selected");
        deleteButton.setBackground(WARNING_COLOR);
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteButton.setBackground(new Color(190, 40, 30));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setBackground(WARNING_COLOR);
            }
        });
        
        clearButton = new CustomButton("Clear Form");
        
        formButtonPanel.add(addButton);
        formButtonPanel.add(editButton);
        formButtonPanel.add(deleteButton);
        formButtonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(formButtonPanel, gbc);
        
        // Tab 3: Statistics
        statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(PANEL_COLOR);
        
        JTextArea statsArea = new JTextArea(20, 60);
        statsArea.setEditable(false);
        statsArea.setBackground(new Color(245, 245, 245));
        statsArea.setForeground(TEXT_COLOR);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(SECONDARY_COLOR), "Statistics", 
                    TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), SECONDARY_COLOR),
            new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane statsScrollPane = new JScrollPane(statsArea);
        
        JPanel statsButtonPanel = new JPanel(new FlowLayout());
        statsButtonPanel.setBackground(PANEL_COLOR);
        
        checkButton = new CustomButton("Check Student Status");
        statsButton = new CustomButton("Generate Period Report");
        JButton groupStatsButton = new CustomButton("Group Statistics");
        groupStatsButton.setBackground(SECONDARY_COLOR);
        groupStatsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                groupStatsButton.setBackground(new Color(40, 140, 65));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                groupStatsButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        statsButtonPanel.add(checkButton);
        statsButtonPanel.add(statsButton);
        statsButtonPanel.add(groupStatsButton);
        
        statsPanel.add(statsButtonPanel, BorderLayout.NORTH);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        // Set up button actions for stats tab
        checkButton.addActionListener(e -> {
            String student = (String) studentComboBox.getSelectedItem();
            if (student != null) {
                String stats = generateStudentStats(student);
                statsArea.setText(stats);
            }
        });
        
        statsButton.addActionListener(e -> showPeriodReportDialog(statsArea));
        
        groupStatsButton.addActionListener(e -> {
            String groupStats = generateGroupStatistics();
            statsArea.setText(groupStats);
        });
        
        // Add tabs
        tabbedPane.addTab("View Records", recordsPanel);
        tabbedPane.addTab("Add/Edit Records", formPanel);
        tabbedPane.addTab("Statistics & Reports", statsPanel);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setBackground(PRIMARY_COLOR);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setPreferredSize(new Dimension(0, 30));
        
        // Add components to frame
        add(tabbedPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void setupLayout() {
        // Already done in initComponents
    }

    private void setupEventHandlers() {
        // Add Record button
        addButton.addActionListener(e -> addRecord());
        
        // Edit Record button
        editButton.addActionListener(e -> editRecord());
        
        // Delete Record button
        deleteButton.addActionListener(e -> deleteRecord());
        
        // Clear Form button
        clearButton.addActionListener(e -> clearForm());
        
        // Update details when table selection changes
        recordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsArea();
            }
        });
    }

    private void addRecord() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateField.getText());
            int classNum = Integer.parseInt(classField.getText());
            String course = courseField.getText();
            String student = studentField.getText();
            AbsenceType type = (AbsenceType) typeComboBox.getSelectedItem();
            
            AbsenceRecord record = new AbsenceRecord(date, classNum, course, student, type);
            tableModel.addRecord(record);
            
            statusLabel.setText("Record added successfully! ID: " + record.getId());
            clearForm();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error adding record: " + ex.getMessage(),
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editRecord() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a record to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (!validateInput()) {
                return;
            }
            
            AbsenceRecord record = tableModel.getRecordAt(selectedRow);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            record.setDate(sdf.parse(dateField.getText()));
            record.setClassNumber(Integer.parseInt(classField.getText()));
            record.setCourseName(courseField.getText());
            record.setStudentName(studentField.getText());
            record.setAbsenceType((AbsenceType) typeComboBox.getSelectedItem());
            
            tableModel.updateRecord(selectedRow, record);
            statusLabel.setText("Record updated successfully!");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating record: " + ex.getMessage(),
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a record to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this record?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRecord(selectedRow);
            statusLabel.setText("Record deleted successfully!");
            clearForm();
        }
    }

    private void clearForm() {
        dateField.setText("");
        classField.setText("");
        courseField.setText("");
        studentField.setText("");
        typeComboBox.setSelectedIndex(0);
    }

    private boolean validateInput() {
        if (dateField.getText().trim().isEmpty()) {
            showError("Date cannot be empty");
            return false;
        }
        if (classField.getText().trim().isEmpty()) {
            showError("Class number cannot be empty");
            return false;
        }
        if (courseField.getText().trim().isEmpty()) {
            showError("Course name cannot be empty");
            return false;
        }
        if (studentField.getText().trim().isEmpty()) {
            showError("Student name cannot be empty");
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.parse(dateField.getText());
        } catch (Exception e) {
            showError("Invalid date format. Use yyyy-MM-dd");
            return false;
        }
        
        try {
            Integer.parseInt(classField.getText());
        } catch (NumberFormatException e) {
            showError("Class number must be a valid integer");
            return false;
        }
        
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateDetailsArea() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow >= 0) {
            AbsenceRecord record = tableModel.getRecordAt(selectedRow);
            detailsArea.setText(record.toString());
            
            // Fill form with selected record data
            dateField.setText(record.getFormattedDate());
            classField.setText(String.valueOf(record.getClassNumber()));
            courseField.setText(record.getCourseName());
            studentField.setText(record.getStudentName());
            typeComboBox.setSelectedItem(record.getAbsenceType());
        } else {
            detailsArea.setText("");
        }
    }

    private void searchRecords() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<AbsenceRecord> filtered = new ArrayList<>();
        for (AbsenceRecord record : records) {
            if (record.getStudentName().toLowerCase().contains(searchText) ||
                record.getCourseName().toLowerCase().contains(searchText)) {
                filtered.add(record);
            }
        }
        
        tableModel.setRecords(filtered);
        statusLabel.setText("Found " + filtered.size() + " records matching: " + searchText);
    }

    private void refreshTable() {
        tableModel.setRecords(records);
        statusLabel.setText("Displaying all " + records.size() + " records");
    }

    private String generateStudentStats(String studentName) {
        StringBuilder stats = new StringBuilder();
        List<AbsenceRecord> studentRecords = new ArrayList<>();
        
        for (AbsenceRecord record : records) {
            if (record.getStudentName().equals(studentName)) {
                studentRecords.add(record);
            }
        }
        
        if (studentRecords.isEmpty()) {
            return "No absence records found for " + studentName;
        }
        
        stats.append("ABSENCE STATISTICS FOR: ").append(studentName).append("\n");
        stats.append("=".repeat(50)).append("\n\n");
        
        // Count by type
        Map<AbsenceType, Integer> typeCount = new HashMap<>();
        for (AbsenceRecord record : studentRecords) {
            typeCount.put(record.getAbsenceType(), 
                         typeCount.getOrDefault(record.getAbsenceType(), 0) + 1);
        }
        
        stats.append("Absence Type Breakdown:\n");
        for (Map.Entry<AbsenceType, Integer> entry : typeCount.entrySet()) {
            stats.append(String.format("  %-15s: %d times\n", entry.getKey(), entry.getValue()));
        }
        
        stats.append("\nDetailed Records:\n");
        for (AbsenceRecord record : studentRecords) {
            stats.append("  ").append(record.toString()).append("\n");
        }
        
        stats.append("\nTotal Absences: ").append(studentRecords.size());
        
        return stats.toString();
    }

    private void showPeriodReportDialog(JTextArea statsArea) {
        JDialog dialog = new JDialog(this, "Generate Period Report", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.getContentPane().setBackground(PANEL_COLOR);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(PANEL_COLOR);
        
        JLabel startLabel = new JLabel("Start Date (yyyy-MM-dd):");
        startLabel.setForeground(PRIMARY_COLOR);
        startLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(startLabel);
        
        JTextField startField = new JTextField();
        startField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        panel.add(startField);
        
        JLabel endLabel = new JLabel("End Date (yyyy-MM-dd):");
        endLabel.setForeground(PRIMARY_COLOR);
        endLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(endLabel);
        
        JTextField endField = new JTextField();
        endField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        panel.add(endField);
        
        JButton generateButton = new CustomButton("Generate Report");
        JButton cancelButton = new CustomButton("Cancel");
        cancelButton.setBackground(WARNING_COLOR);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        generateButton.addActionListener(e -> {
            try {
                String startStr = startField.getText();
                String endStr = endField.getText();
                
                if (startStr.isEmpty() || endStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter both dates");
                    return;
                }
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(startStr);
                Date endDate = sdf.parse(endStr);
                
                String report = generatePeriodReport(startDate, endDate);
                statsArea.setText(report);
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid date format. Use yyyy-MM-dd",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String generatePeriodReport(Date startDate, Date endDate) {
        StringBuilder report = new StringBuilder();
        Map<String, Integer> studentCounts = new HashMap<>();
        List<AbsenceRecord> periodRecords = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (AbsenceRecord record : records) {
            if (!record.getDate().before(startDate) && !record.getDate().after(endDate)) {
                periodRecords.add(record);
                studentCounts.put(record.getStudentName(),
                                 studentCounts.getOrDefault(record.getStudentName(), 0) + 1);
            }
        }
        
        if (periodRecords.isEmpty()) {
            return "No records found for the specified period.";
        }
        
        report.append("PERIOD REPORT: ").append(sdf.format(startDate))
              .append(" to ").append(sdf.format(endDate)).append("\n");
        report.append("=".repeat(60)).append("\n\n");
        
        // Sort by count descending
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(studentCounts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        report.append("Absence Frequency (Sorted):\n");
        for (Map.Entry<String, Integer> entry : sorted) {
            report.append(String.format("  %-20s: %d absences\n", entry.getKey(), entry.getValue()));
        }
        
        report.append("\nDetailed Records (").append(periodRecords.size()).append(" total):\n");
        for (AbsenceRecord record : periodRecords) {
            report.append("  ").append(record.toString()).append("\n");
        }
        
        return report.toString();
    }

    private String generateGroupStatistics() {
        StringBuilder stats = new StringBuilder();
        Map<Integer, List<String>> groups = new HashMap<>();
        
        // Organize students by group
        for (Map.Entry<String, Integer> entry : studentGroups.entrySet()) {
            groups.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }
        
        stats.append("GROUP STATISTICS\n");
        stats.append("=".repeat(40)).append("\n\n");
        
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            stats.append("Group ").append(entry.getKey()).append(":\n");
            List<String> students = entry.getValue();
            
            // Count absences per student in group
            for (String student : students) {
                int count = 0;
                for (AbsenceRecord record : records) {
                    if (record.getStudentName().equals(student)) {
                        count++;
                    }
                }
                stats.append(String.format("  %-20s: %d absences\n", student, count));
            }
            stats.append("\n");
        }
        
        return stats.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set Nimbus look and feel for modern appearance
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel
            }
            
            AttendanceSystemGUI gui = new AttendanceSystemGUI();
            gui.setVisible(true);
        });
    }
}
