public class FCFSScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private ProcessTracker tracker;
    private static final int TIME_UNIT_MS = 100;
    private MemoryManager memoryManager; // Add this field

    public FCFSScheduler(ReadyQueue readyQueue, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.tracker = tracker;
    }

    // Add a constructor that accepts a memory manager
    public FCFSScheduler(ReadyQueue readyQueue, ProcessTracker tracker, MemoryManager memoryManager) {
        this.readyQueue = readyQueue;
        this.tracker = tracker;
        this.memoryManager = memoryManager;
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
                    
                    long startTime = pcb.getStartTime();
                    int remaining = pcb.getRemainingBurstTime();
                    try {
                        Thread.sleep(remaining * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    pcb.setRemainingBurstTime(0);
                    pcb.setFinishTime(System.currentTimeMillis());
                    long finishTime = pcb.getFinishTime();

                    pcb.setWaitingTime((int) (pcb.getStartTime() - pcb.getArrivalTime()));
                    pcb.setTurnaroundTime((int) (finishTime - pcb.getArrivalTime()));

                    SystemCalls.sysChangeProcessState(pcb, ProcessState.TERMINATED);
                    tracker.addGanttChartEntry(new GanttChartEntry(pcb.getProcessId(), startTime, finishTime));

                    // Add this line to free memory
                    if (memoryManager != null) {
                        SystemCalls.sysFreeMemory(memoryManager, pcb.getMemoryRequired());
                    }

                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished.");
                    SystemCalls.sysProcessFinished(tracker, pcb);
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("FCFS Scheduler finished.");
    }
}