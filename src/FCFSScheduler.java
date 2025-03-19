public class FCFSScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private ProcessTracker tracker;
    private static final int TIME_UNIT_MS = 100;

    public FCFSScheduler(ReadyQueue readyQueue, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        while (!tracker.isAllProcessesFinished()) {
            if (!readyQueue.isEmpty()) {
                PCB pcb = readyQueue.pollReadyPCB();
                if (pcb != null) {
                    // Set process to RUNNING and initialize start time if needed.
                    pcb.setState(ProcessState.RUNNING);
                    if (pcb.getStartTime() == -1) {
                        pcb.setStartTime(System.currentTimeMillis());
                    }

                    int remaining = pcb.getRemainingBurstTime();
                    try {
                        Thread.sleep(remaining * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Process is finished: update its metrics.
                    pcb.setRemainingBurstTime(0);
                    pcb.setFinishTime(System.currentTimeMillis());

                    long arrival = pcb.getArrivalTime();
                    long start = pcb.getStartTime();
                    long finish = pcb.getFinishTime();
                    pcb.setWaitingTime((int) (start - arrival));
                    pcb.setTurnaroundTime((int) (finish - arrival));

                    // Change state to TERMINATED using system call.
                    SystemCalls.sysChangeProcessState(pcb, ProcessState.TERMINATED);
                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished.");

                    // Record process completion by passing the finished PCB.
                    SystemCalls.sysProcessFinished(tracker, pcb);
                }
            } else {
                try {
                    Thread.sleep(100); // Wait a bit when there's no job ready
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("FCFS Scheduler finished.");
    }
}