import java.util.Scanner;

public class MainSimulator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // 1) Ask for the file path containing job definitions
        System.out.print("Enter the path to your job file (e.g., job.txt): ");
        String filePath = input.nextLine();

        // 2) Ask for total memory
        System.out.print("Enter total memory (e.g., 1024): ");
        int totalMemory = input.nextInt();
        input.nextLine(); // consume leftover newline if any

        // 3) Ask which scheduling algorithm to use
        System.out.println("\nWhich scheduling algorithm do you want to use?");
        System.out.println("[1] FCFS");
        System.out.println("[2] Round Robin");
        System.out.println("[3] Priority");
        System.out.print("Enter choice: ");
        int choice = input.nextInt();

        int quantum = 0; // only used if Round Robin
        if (choice == 2) {
            System.out.print("Enter time quantum in ms: ");
            quantum = input.nextInt();
        }

        // Create core components
        MemoryManager memoryManager = new MemoryManager(totalMemory);
        JobQueue jobQueue = new JobQueue();
        ReadyQueue readyQueue = new ReadyQueue();

        // 4) Start the file-reading thread to continuously read new jobs
        Thread fileReader = new Thread(new FileReadingThread(filePath, jobQueue));
        fileReader.start();

        // 5) Start the memory-loading thread
        Thread memLoader = new Thread(new MemoryLoadingThread(jobQueue, readyQueue, memoryManager));
        memLoader.start();

        // 6) Prepare and start the chosen scheduler
        Thread scheduler;
        switch (choice) {
            case 1:
                scheduler = new Thread(new FCFSScheduler(readyQueue, memoryManager));
                break;
            case 2:
                scheduler = new Thread(new RRScheduler(readyQueue, memoryManager, quantum));
                break;
            case 3:
                scheduler = new Thread(new PriorityScheduler(readyQueue, memoryManager));
                break;
            default:
                System.out.println("Invalid choice. Using FCFS by default.");
                scheduler = new Thread(new FCFSScheduler(readyQueue, memoryManager));
        }
        scheduler.start();

        System.out.println("\nSimulation started. Reading jobs from: " + filePath);
        System.out.println("Press Ctrl+C to stop or let it run in the background...");

        // The program runs indefinitely: the fileReader, memLoader,
        // and scheduler threads will keep running.
        // (Optional) consoleScanner.close();
    }
}
