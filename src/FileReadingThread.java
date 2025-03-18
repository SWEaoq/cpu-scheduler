import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReadingThread implements Runnable {
    private String filePath;
    private JobQueue jobQueue;
    private ProcessTracker tracker;

    private int lastLineRead = 0;
    private static final long READ_INTERVAL_MS = 5000;

    public FileReadingThread(String filePath, JobQueue jobQueue, ProcessTracker tracker) {
        this.filePath = filePath;
        this.jobQueue = jobQueue;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int newJobs = countJobs(filePath) - lastLineRead; // ✅ Now correctly references the method
                if (newJobs > 0) {
                    Scanner sc = new Scanner(new File(filePath));
                    int lineCount = 0;
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine().trim();
                        if (!line.isEmpty() && lineCount >= lastLineRead) {
                            parseAndAddJob(line);
                            tracker.incrementTotalProcesses(); // ✅ Track new job
                        }
                        lineCount++;
                    }
                    sc.close();
                    lastLineRead = lineCount;
                }
            } catch (FileNotFoundException e) {
                System.err.println("Could not find file: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(READ_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ✅ Correctly defined static method to count jobs in the file.
     */
    public static int countJobs(String filePath) {
        int count = 0;
        try {
            File file = new File(filePath);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    count++; // ✅ Count valid job lines
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("Could not find job file: " + filePath);
        }
        return count;
    }

    /**
     * Parses and adds a job to the job queue.
     */
    private void parseAndAddJob(String line) {
        String[] parts = line.split(";");
        if (parts.length != 2) {
            System.err.println("Invalid line format (must contain exactly one ';'): " + line);
            return;
        }

        String[] subParts = parts[0].split(":");
        if (subParts.length != 3) {
            System.err.println("Invalid job format (must contain two ':'): " + line);
            return;
        }

        try {
            int pid = Integer.parseInt(subParts[0]);
            int burstTime = Integer.parseInt(subParts[1]);
            int priority = Integer.parseInt(subParts[2]);
            int memReq = Integer.parseInt(parts[1]);

            PCB pcb = new PCB(pid, burstTime, priority, memReq, System.currentTimeMillis());
            jobQueue.addJob(pcb);
            System.out.println("Added new job from file: " + pcb.toString());
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse job line: " + line + " => " + e.getMessage());
        }
    }
}
