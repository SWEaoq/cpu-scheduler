import java.util.Scanner;

public class MainSimulator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Get job file and memory size
        System.out.print("Enter the path to your job file (e.g., job.txt): ");
        String filePath = input.nextLine();

        System.out.print("Enter total memory (e.g., 2048): ");
        int totalMemory = input.nextInt();
        input.nextLine(); // Consume leftover newline

        // Scheduler selection
        System.out.println("\nChoose a scheduling algorithm:");
        System.out.println("[1] FCFS");
        System.out.println("[2] Round Robin (Configurable Quantum)");
        System.out.println("[3] Priority Scheduling");
        System.out.print("Enter choice: ");
        int choice = input.nextInt();

        int quantum = 0; // Only needed for Round Robin
        if (choice == 2) {
            System.out.print("Enter time quantum in ms: ");
            quantum = input.nextInt();
        }

        // Create core components
        MemoryManager memoryManager = new MemoryManager(totalMemory);
        JobQueue jobQueue = new JobQueue();
        ReadyQueue readyQueue = new ReadyQueue();

        // Track the total number of jobs
        int totalJobs = FileReadingThread.countJobs(filePath);
        ProcessTracker tracker = new ProcessTracker(totalJobs);

        // Start the file-reading thread (tracker not used in this constructor)
        FileReadingThread fileReaderTask = new FileReadingThread(filePath, jobQueue);
        Thread fileReader = new Thread(fileReaderTask);
        fileReader.start();

        // Start the memory-loading thread
        MemoryLoadingThread memLoaderTask = new MemoryLoadingThread(jobQueue, readyQueue, memoryManager);
        Thread memLoader = new Thread(memLoaderTask);
        memLoader.start();

        // Start the selected scheduler
        Thread scheduler;
        switch (choice) {
            case 1:
                scheduler = new Thread(new FCFSScheduler(readyQueue, tracker));
                break;
            case 2:
                scheduler = new Thread(new RRScheduler(readyQueue, memoryManager, quantum, tracker));
                break;
            case 3:
                scheduler = new Thread(new PriorityScheduler(readyQueue, memoryManager, tracker));
                break;
            default:
                System.out.println("Invalid choice. Using FCFS by default.");
                scheduler = new Thread(new FCFSScheduler(readyQueue, tracker));
        }
        scheduler.start();

        // Wait for all processes to finish
        while (!tracker.isAllProcessesFinished()) {
            try {
                Thread.sleep(100);  // Check every 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Stop other threads when done
        fileReaderTask.stopThread();
        memLoaderTask.stopThread();
        input.close();

        // Print final performance metrics
        System.out.println("\nSimulation completed. All processes finished.");
        System.out.println("Average Waiting Time: " + tracker.getAverageWaitingTime() + " ms");
        System.out.println("Average Turnaround Time: " + tracker.getAverageTurnaroundTime() + " ms");

        System.exit(0);
    }
}