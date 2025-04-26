import java.util.Scanner;

public class MainSimulator {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the path to your job file (e.g., job.txt): ");
        String filePath = input.nextLine();

        System.out.print("Enter total memory (e.g., 2048): ");
        int totalMemory = input.nextInt();
        input.nextLine();

        System.out.println("\nChoose a scheduling algorithm:");
        System.out.println("[1] FCFS");
        System.out.println("[2] Round Robin (Configurable Quantum)");
        System.out.println("[3] Priority Scheduling");
        System.out.println("[4] Run All Algorithms");
        System.out.print("Enter choice: ");
        int choice = input.nextInt();

        if (choice == 4) {
            System.out.print("Enter time quantum in ms (for RR): ");
            int quantum = input.nextInt();
            
            System.out.println("\n--- Running FCFS ---");
            runFCFS(filePath, totalMemory);

            System.out.println("\n--- Running Round Robin ---");
            runRR(filePath, totalMemory, quantum);

            System.out.println("\n--- Running Priority Scheduling ---");
            runPriority(filePath, totalMemory);
        } else {
            int quantum = 0;
            if (choice == 2) {
                System.out.print("Enter time quantum in ms: ");
                quantum = input.nextInt();
            }
            runSimulation(choice, filePath, totalMemory, quantum);
        }

        input.close();
        System.exit(0);
    }

    private static void runSimulation(int choice, String filePath, int totalMemory, int quantum) {
        MemoryManager memoryManager = new MemoryManager(totalMemory);
        JobQueue jobQueue = new JobQueue();
        ReadyQueue readyQueue = new ReadyQueue();

        int totalJobs = FileReadingThread.countJobs(filePath);
        ProcessTracker tracker = new ProcessTracker(totalJobs);

        FileReadingThread fileReaderTask = new FileReadingThread(filePath, jobQueue);
        Thread fileReader = new Thread(fileReaderTask);
        fileReader.start();

        MemoryLoadingThread memLoaderTask = new MemoryLoadingThread(jobQueue, readyQueue, memoryManager);
        Thread memLoader = new Thread(memLoaderTask);
        memLoader.start();

        Thread scheduler;
        switch (choice) {
            case 1:
                scheduler = new Thread(new FCFSScheduler(readyQueue, tracker, memoryManager));
                break;
            case 2:
                scheduler = new Thread(new RRScheduler(readyQueue, memoryManager, quantum, tracker));
                break;
            case 3:
                scheduler = new Thread(new PriorityScheduler(readyQueue, memoryManager, tracker));
                break;
            default:
                System.out.println("Invalid choice. Using FCFS by default.");
                scheduler = new Thread(new FCFSScheduler(readyQueue, tracker, memoryManager));
        }
        scheduler.start();

        while (!tracker.isAllProcessesFinished()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fileReaderTask.stopThread();
        memLoaderTask.stopThread();

        System.out.println("\nSimulation completed. All processes finished.");
        
        System.out.println("\nIndividual Process Statistics:");
        System.out.println("-------------------------------------------------------");
        System.out.println("| Process ID | Waiting Time (ms) | Turnaround Time (ms) |");
        System.out.println("-------------------------------------------------------");
        for (PCB pcb : tracker.getFinishedProcesses()) {
            System.out.printf("| %-10d | %-16d | %-20d |\n", 
                             pcb.getProcessId(), 
                             pcb.getWaitingTime(), 
                             pcb.getTurnaroundTime());
        }
        System.out.println("-------------------------------------------------------");
        
        System.out.println("\nAverage Statistics:");
        System.out.println("Average Waiting Time: " + tracker.getAverageWaitingTime() + " ms");
        System.out.println("Average Turnaround Time: " + tracker.getAverageTurnaroundTime() + " ms");

        if (!tracker.getGanttChartEntries().isEmpty()) {
            long simulationStart = tracker.getGanttChartEntries().get(0).getStartTime();
            System.out.println("\nGantt Chart:");
            System.out.println("-------------------------------------------------");
            System.out.println("| Start (ms) | End (ms)  | Process ID |");
            System.out.println("-------------------------------------------------");
            for (GanttChartEntry entry : tracker.getGanttChartEntries()) {
                long relativeStart = entry.getStartTime() - simulationStart;
                long relativeEnd = entry.getEndTime() - simulationStart;
                System.out.printf("| %-10d | %-9d | %-10d |\n", relativeStart, relativeEnd, entry.getProcessId());
            }
            System.out.println("-------------------------------------------------");
        }
    }

    private static void runFCFS(String filePath, int totalMemory) {
        runSimulation(1, filePath, totalMemory, 0);
    }

    private static void runRR(String filePath, int totalMemory, int quantum) {
        runSimulation(2, filePath, totalMemory, quantum);
    }

    private static void runPriority(String filePath, int totalMemory) {
        runSimulation(3, filePath, totalMemory, 0);
    }
}