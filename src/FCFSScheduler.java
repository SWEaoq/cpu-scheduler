public class FCFSScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private ProcessTracker tracker;

    private static final int TIME_UNIT_MS = 100;  

    public FCFSScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        while (!tracker.isAllProcessesFinished()) {
            if (!readyQueue.isEmpty()) {
                PCB pcb = readyQueue.pollReadyPCB();
                if (pcb != null) {
                    pcb.setState(ProcessState.RUNNING);

                    if (pcb.getStartTime() == -1) {
                        pcb.setStartTime(System.currentTimeMillis());
                    }

                    try {
                        Thread.sleep(pcb.getRemainingBurstTime() * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    pcb.setFinishTime(System.currentTimeMillis());
                    pcb.setRemainingBurstTime(0);
                    pcb.setState(ProcessState.TERMINATED);

                    // Free memory
                    memoryManager.freeMemory(pcb.getMemoryRequired());

                    // Track process completion
                    tracker.processFinished();

                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished.");
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("FCFS Scheduler finished.");
    }
}
