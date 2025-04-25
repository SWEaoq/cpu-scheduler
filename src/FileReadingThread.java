import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReadingThread implements Runnable {
    private String filePath;
    private JobQueue jobQueue;
    private boolean running = true;

    private int lastLineRead = 0;
    private static final long READ_INTERVAL_MS = 5000;

    public FileReadingThread(String filePath, JobQueue jobQueue) {
        this.filePath = filePath;
        this.jobQueue = jobQueue;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                File file = new File(filePath);
                Scanner scanner = new Scanner(file);
                int currentLine = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    currentLine++;
                    if (currentLine <= lastLineRead)
                        continue;
                    parseAndAddJob(line);
                }
                lastLineRead = currentLine;
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(READ_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("FileReadingThread stopped.");
    }
    
    public static int countJobs(String filePath) {
        int count = 0;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("//")) {
                    try {
                        String[] parts = line.split(";");
                        if (parts.length == 2) {
                            String[] subParts = parts[0].split(":");
                            if (subParts.length == 3) {
                                // Try parsing the process ID
                                Integer.parseInt(subParts[0].trim());
                                count++;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: Could not find file: " + filePath);
            e.printStackTrace();
        }
        return count;
    }
    
    private void parseAndAddJob(String line) {
        try {
            line = line.trim();
            // Skip comment lines or empty lines
            if (line.isEmpty() || line.startsWith("//")) {
                return;
            }
            
            String[] parts = line.split(";");
            if (parts.length != 2) {
                System.err.println("Warning: Invalid job format (missing semicolon separator): " + line);
                return;
            }
    
            String[] subParts = parts[0].split(":");
            if (subParts.length != 3) {
                System.err.println("Warning: Invalid job format (expected 3 colon-separated values): " + parts[0]);
                return;
            }
    
            // Validate each part
            int processId;
            try {
                processId = Integer.parseInt(subParts[0].trim());
                if (processId <= 0) {
                    System.err.println("Warning: Process ID must be positive: " + subParts[0] + " - skipping job");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Invalid Process ID (must be an integer): " + subParts[0] + " - skipping job");
                return;
            }
            
            int burstTime;
            try {
                burstTime = Integer.parseInt(subParts[1].trim());
                if (burstTime <= 0) {
                    System.err.println("Warning: Burst time must be positive: " + subParts[1] + " - skipping job");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Invalid Burst Time (must be an integer): " + subParts[1] + " - skipping job");
                return;
            }
            
            int priority;
            try {
                priority = Integer.parseInt(subParts[2].trim());
                if (priority < 1 || priority > 8) {
                    System.err.println("Warning: Priority must be between 1-8: " + subParts[2] + " - using default of 1");
                    priority = 1;  // Use default priority instead of skipping
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Invalid Priority (must be an integer): " + subParts[2] + " - using default of 1");
                priority = 1;  // Use default priority
            }
            
            int memoryRequired;
            try {
                memoryRequired = Integer.parseInt(parts[1].trim());
                if (memoryRequired <= 0) {
                    System.err.println("Warning: Memory required must be positive: " + parts[1] + " - skipping job");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Invalid Memory (must be an integer): " + parts[1] + " - skipping job");
                return;
            }
    
            PCB pcb = new PCB(processId, burstTime, priority, memoryRequired, System.currentTimeMillis());
            jobQueue.addJob(pcb);
            System.out.println("Added job: " + pcb.toString());
            
        } catch (Exception e) {
            System.err.println("Error parsing job line: " + line);
            e.printStackTrace();
        }
    }
}