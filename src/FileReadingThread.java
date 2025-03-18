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
                if (!line.isEmpty()) {  // Only count non-empty lines
                    count++;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    private void parseAndAddJob(String line) {
        String[] parts = line.split(";");
        if (parts.length != 2) {
            return;
        }

        String[] subParts = parts[0].split(":");
        if (subParts.length != 3) {
            return;
        }

        try {
            int processId = Integer.parseInt(subParts[0].trim());
            int burstTime = Integer.parseInt(subParts[1].trim());
            int priority = Integer.parseInt(subParts[2].trim());
            int memoryRequired = Integer.parseInt(parts[1].trim());

            PCB pcb = new PCB(processId, burstTime, priority, memoryRequired, System.currentTimeMillis());
            jobQueue.addJob(pcb);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}