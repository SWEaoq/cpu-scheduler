import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReadingThread implements Runnable {
    private String filePath;
    private JobQueue jobQueue;
    
    // We keep track of how many lines we’ve already processed,
    // so we only add new jobs if the file grows.
    private int lastLineRead = 0;

    // Time to sleep (ms) between each read cycle.
    // Adjust based on how quickly you want to detect new lines.
    private static final long READ_INTERVAL_MS = 5000;

    public FileReadingThread(String filePath, JobQueue jobQueue) {
        this.filePath = filePath;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<String> allLines = readAllLines(filePath);
                // If the file has more lines than we’ve processed, parse the new lines
                if (allLines.size() > lastLineRead) {
                    for (int i = lastLineRead; i < allLines.size(); i++) {
                        String line = allLines.get(i).trim();
                        if (!line.isEmpty()) {
                            parseAndAddJob(line);
                        }
                    }
                    // Update how many lines we've read
                    lastLineRead = allLines.size();
                }
            } catch (FileNotFoundException e) {
                System.err.println("Could not find file: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Sleep so we don't busy-wait
            try {
                Thread.sleep(READ_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads all lines from the file.
     */
    private List<String> readAllLines(String filePath) throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }
        }
        return lines;
    }

    /**
     * Parse a line of format: "ProcessID:BurstTime:Priority;MemoryRequired"
     * and add a new PCB to the jobQueue.
     */
    private void parseAndAddJob(String line) {
        // Example line: "1:25:4;500"
        // Split on ';' => leftSide="1:25:4", mem="500"
        String[] parts = line.split(";");
        if (parts.length != 2) {
            System.err.println("Invalid line format (must contain exactly one ';'): " + line);
            return;
        }
        String leftSide = parts[0]; // "1:25:4"
        String memStr   = parts[1]; // "500"

        // Split left side on ':' => ["1", "25", "4"]
        String[] subParts = leftSide.split(":");
        if (subParts.length != 3) {
            System.err.println("Invalid job format (must contain two ':'): " + line);
            return;
        }

        try {
            int pid       = Integer.parseInt(subParts[0]);
            int burstTime = Integer.parseInt(subParts[1]);
            int priority  = Integer.parseInt(subParts[2]);
            int memReq    = Integer.parseInt(memStr);

            PCB pcb = new PCB(
                pid,
                burstTime,
                priority,
                memReq,
                System.currentTimeMillis()
            );
            jobQueue.addJob(pcb);
            System.out.println("Added new job from file: " + pcb.toString());
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse job line: " + line + " => " + e.getMessage());
        }
    }
}
