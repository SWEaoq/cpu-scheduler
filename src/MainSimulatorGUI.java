import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class MainSimulatorGUI extends JFrame {
    private JTextField jobFileField;
    private JTextField totalMemoryField;
    private JComboBox<String> algorithmComboBox;
    private JTextField quantumField;
    private JButton runButton;
    private JTextArea outputArea;
    
    public MainSimulatorGUI() {
        setTitle("CPU Scheduler Simulator - GUI Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Job file path input
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Job file path:"), gbc);
        jobFileField = new JTextField(30);
        gbc.gridx = 1;
        inputPanel.add(jobFileField, gbc);
        
        // Total memory input
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Total memory:"), gbc);
        totalMemoryField = new JTextField(10);
        gbc.gridx = 1;
        inputPanel.add(totalMemoryField, gbc);
        
        // Algorithm selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Algorithm:"), gbc);
        String[] algorithms = {"FCFS", "Round Robin", "Priority Scheduling", "Run All"};
        algorithmComboBox = new JComboBox<>(algorithms);
        gbc.gridx = 1;
        inputPanel.add(algorithmComboBox, gbc);
        
        // Quantum input (applicable for Round Robin)
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Quantum (ms):"), gbc);
        quantumField = new JTextField(10);
        gbc.gridx = 1;
        inputPanel.add(quantumField, gbc);
        
        // Run button
        runButton = new JButton("Run Simulation");
        gbc.gridx = 1;
        gbc.gridy = 4;
        inputPanel.add(runButton, gbc);
        
        // Output text area
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // When the algorithm selection changes, enable or disable the quantum field.
        algorithmComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Enable quantum only if Round Robin is selected or if "Run All" is chosen.
                int idx = algorithmComboBox.getSelectedIndex();
                quantumField.setEnabled(idx == 1 || idx == 3);
            }
        });
        
        // Run simulation when button is clicked.
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSimulationFromGUI();
            }
        });
    }
    
    private void runSimulationFromGUI() {
        String filePath = jobFileField.getText().trim();
        int totalMemory = Integer.parseInt(totalMemoryField.getText().trim());
        int choice = algorithmComboBox.getSelectedIndex() + 1; // 1: FCFS, 2: RR, 3: Priority, 4: Run All
        
        int quantum = 0;
        if(choice == 2 || choice == 4) {
            try {
                quantum = Integer.parseInt(quantumField.getText().trim());
            } catch (NumberFormatException ex) {
                quantum = 7; // default quantum if parsing fails
            }
        }
        final int finalQuantum = quantum;  // make it effectively final
        
        // Redirect console output to the text area.
        ConsoleOutputStream cos = new ConsoleOutputStream(outputArea);
        PrintStream ps = new PrintStream(cos);
        PrintStream oldOut = System.out;
        System.setOut(ps);
        
        // Run simulation in a background thread.
        new Thread(() -> {
            MainSimulator.runSimulation(choice, filePath, totalMemory, finalQuantum);
            // Restore original System.out when done.
            System.setOut(oldOut);
        }).start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainSimulatorGUI().setVisible(true);
        });
    }
}
