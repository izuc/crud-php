package software.crud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import software.crud.Models.CodeInput;
import software.crud.Models.ColumnModel;
import software.crud.Models.CrudMessage;
import software.crud.Models.FinalQueryData;
import software.crud.MySQL.MySQLDBHelper;
import software.crud.MySQL.PHP_APIMySQL;

public class App extends JFrame {
    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtDatabase;
    private JList<String> tableList;
    private DefaultListModel<String> tableListModel;
    private JTextField txtPackageName;
    private JTextField txtAppName;
    private JTextField txtAppUrl;
    private JTextField txtJwtSecret;
    private JTextField txtDbDriver;
    private JTextField txtDbCharset;
    private JComboBox<String> cmbAuthTable;
    private JComboBox<String> cmbAuthUsernameField;
    private JComboBox<String> cmbAuthPasswordField;
    private JComboBox<String> cmbAuthPrimaryKey;
    private JButton btnConnect;
    private JButton btnSelectAll;
    private JButton btnUnselectAll;
    private JButton btnGenerateAPI;
    private JTextArea logTextArea;

    public App() {
        initComponents();
        layoutComponents();
        txtJwtSecret.setText(generateJwtSecret());
    }

    private void initComponents() {
        txtHost = new JTextField(20);
        txtHost.setText("localhost");
        txtPort = new JTextField(10);
        txtPort.setText("3306");
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtDatabase = new JTextField(20);
        tableListModel = new DefaultListModel<>();
        tableList = new JList<>(tableListModel);
        txtPackageName = new JTextField(20);
        txtPackageName.setText("com.packagename");
        txtAppName = new JTextField(20);
        txtAppName.setText("MyApp");
        txtAppUrl = new JTextField(20);
        txtAppUrl.setText("http://localhost");
        txtJwtSecret = new JTextField(20);
        txtJwtSecret.setText("your_jwt_secret");
        txtDbDriver = new JTextField(20);
        txtDbDriver.setText("mysql");
        txtDbCharset = new JTextField(20);
        txtDbCharset.setText("utf8mb4");

        cmbAuthTable = new JComboBox<>();
        cmbAuthUsernameField = new JComboBox<>();
        cmbAuthPasswordField = new JComboBox<>();
        cmbAuthPrimaryKey = new JComboBox<>();

        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(e -> loadTables());

        btnSelectAll = new JButton("Select All");
        btnSelectAll.addActionListener(e -> selectAllTables());

        btnUnselectAll = new JButton("Unselect All");
        btnUnselectAll.addActionListener(e -> unselectAllTables());

        btnGenerateAPI = new JButton("Generate API");
        btnGenerateAPI.addActionListener(e -> generateAPI());

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel logPanel = createLogPanel();
        add(logPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.EAST);

        // Set background color
        getContentPane().setBackground(Color.WHITE);

        // Add padding to the content pane
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Database Connection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtHost, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPort, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDatabase, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Auth Table:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbAuthTable, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Auth Username Field:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbAuthUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Auth Password Field:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbAuthPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Auth Primary Key:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbAuthPrimaryKey, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Package Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPackageName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(new JLabel("App Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAppName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        formPanel.add(new JLabel("App URL:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAppUrl, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        formPanel.add(new JLabel("JWT Secret:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtJwtSecret, gbc);

        gbc.gridx = 0;
        gbc.gridy = 13;
        formPanel.add(new JLabel("DB Driver:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDbDriver, gbc);

        gbc.gridx = 0;
        gbc.gridy = 14;
        formPanel.add(new JLabel("DB Charset:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDbCharset, gbc);

        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnConnect, gbc);

        return formPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Tables"));

        JScrollPane scrollPane = new JScrollPane(tableList);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setPreferredSize(new Dimension(250, 100));
        logPanel.add(scrollPane, BorderLayout.CENTER);

        return logPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        buttonPanel.add(btnSelectAll);
        buttonPanel.add(btnUnselectAll);
        buttonPanel.add(btnGenerateAPI);

        return buttonPanel;
    }

    private void loadTables() {
        MySQLDBHelper dbHelper = createDBHelper();

        try {
            dbHelper.connect();
            List<String> tables = dbHelper.getListOfTables();
            populateTableList(tables);
            populateAuthTableComboBox(tables);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private MySQLDBHelper createDBHelper() {
        return new MySQLDBHelper(
                txtHost.getText(),
                txtPort.getText(),
                txtUsername.getText(),
                new String(txtPassword.getPassword()),
                txtDatabase.getText());
    }

    private void populateTableList(List<String> tables) {
        tableListModel.clear();
        tables.forEach(tableListModel::addElement);
    }

    private void populateAuthTableComboBox(List<String> tables) {
        cmbAuthTable.removeAllItems();
        tables.forEach(cmbAuthTable::addItem);

        cmbAuthTable.addActionListener(e -> {
            if (cmbAuthTable.getSelectedItem() != null) {
                String selectedTable = cmbAuthTable.getSelectedItem().toString();
                loadAuthFields(selectedTable);
            }
        });
    }

    private void loadAuthFields(String table) {
        MySQLDBHelper dbHelper = createDBHelper();

        try {
            dbHelper.connect();
            List<ColumnModel> columns = dbHelper.getTableColumns(table);
            populateAuthFieldComboBox(cmbAuthUsernameField, columns);
            populateAuthFieldComboBox(cmbAuthPasswordField, columns);
            populateAuthPrimaryKeyComboBox(cmbAuthPrimaryKey, columns);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private void populateAuthFieldComboBox(JComboBox<String> comboBox, List<ColumnModel> columns) {
        comboBox.removeAllItems();
        List<String> columnNames = columns.stream()
                .map(ColumnModel::getField)
                .collect(Collectors.toList());
        columnNames.forEach(comboBox::addItem);
    }

    private void populateAuthPrimaryKeyComboBox(JComboBox<String> comboBox, List<ColumnModel> columns) {
        comboBox.removeAllItems();
        List<String> primaryKeyColumns = columns.stream()
                .filter(column -> "PRI".equals(column.getKey()))
                .map(ColumnModel::getField)
                .collect(Collectors.toList());
        primaryKeyColumns.forEach(comboBox::addItem);
    }

    private void handleException(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void selectAllTables() {
        tableList.setSelectionInterval(0, tableListModel.size() - 1);
    }

    private void unselectAllTables() {
        tableList.clearSelection();
    }

    private void generateAPI() {
        List<String> selectedTables = tableList.getSelectedValuesList();
        PHP_APIMySQL generator = new PHP_APIMySQL(txtPackageName.getText());
        MySQLDBHelper dbHelper = createDBHelper();

        String authTable = cmbAuthTable.getSelectedItem() != null ? cmbAuthTable.getSelectedItem().toString() : "";
        String authUsernameField = cmbAuthUsernameField.getSelectedItem() != null
                ? cmbAuthUsernameField.getSelectedItem().toString()
                : "";
        String authPasswordField = cmbAuthPasswordField.getSelectedItem() != null
                ? cmbAuthPasswordField.getSelectedItem().toString()
                : "";
        String authPrimaryKey = cmbAuthPrimaryKey.getSelectedItem() != null
                ? cmbAuthPrimaryKey.getSelectedItem().toString()
                : "";

        try {
            CodeInput<FinalQueryData> codeInput = generator.automator(
                    txtPackageName.getText(), selectedTables, dbHelper,
                    authTable, authUsernameField, authPasswordField, authPrimaryKey,
                    txtAppName.getText(), txtAppUrl.getText(), txtJwtSecret.getText(),
                    txtDbDriver.getText(), txtDbCharset.getText(),
                    txtHost.getText(), txtPort.getText(), txtDatabase.getText(),
                    txtUsername.getText(), new String(txtPassword.getPassword()));

            displayMessages(generator.getMessages());
            generator.createPHPApp(codeInput);
            displayMessages(generator.getMessages());

            // Update the composer.json file with project details
            updateComposerJson(codeInput.getDestinationFolder(), txtAppName.getText(), txtPackageName.getText());

        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private void displayMessages(ArrayList<CrudMessage> messages) {
        if (messages != null && !messages.isEmpty()) {
            StringBuilder messageText = new StringBuilder();
            messages.forEach(message -> messageText.append(message.getMessage()).append("\n"));
            logTextArea.append(messageText.toString());
            messages.clear();
        }
    }

    private String generateJwtSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void updateComposerJson(String projectDirectory, String appName, String packageName) {
        String composerJsonPath = projectDirectory + "/composer.json";
        try {
            // Read the existing composer.json content
            String composerJsonContent = new String(Files.readAllBytes(Paths.get(composerJsonPath)),
                    StandardCharsets.UTF_8);

            // Update the composer.json content with the app name and package name
            composerJsonContent = composerJsonContent.replace("\"name\": \"\"", "\"name\": \"" + packageName + "\"")
                    .replace("\"description\": \"\"",
                            "\"description\": \"" + appName + " - A generated API for your project\"");

            // Write the updated content back to composer.json
            Files.write(Paths.get(composerJsonPath), composerJsonContent.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);

            logMessage("composer.json file updated successfully", true);
        } catch (IOException e) {
            logMessage("Error updating composer.json file: " + e.getMessage(), false);
        }
    }

    private void logMessage(String message, boolean isSuccess) {
        logTextArea.append((isSuccess ? "[SUCCESS] " : "[ERROR] ") + message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App gui = new App();
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setTitle("CRUD Software - Created by www.lance.name");
            gui.setPreferredSize(new Dimension(600, 1000));
            gui.pack();
            gui.setVisible(true);
        });
    }
}
