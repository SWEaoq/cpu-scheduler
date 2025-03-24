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
                    
                    long startTime = pcb.getStartTime();
                    int remaining = pcb.getRemainingBurstTime();
                    try {
                        Thread.sleep(remaining * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Process is finished: update its metrics.
                    pcb.setRemainingBurstTime(0);
                    pcb.setFinishTime(System.currentTimeMillis());
                    long finishTime = pcb.getFinishTime();

                    pcb.setWaitingTime((int) (pcb.getStartTime() - pcb.getArrivalTime()));
                    pcb.setTurnaroundTime((int) (finishTime - pcb.getArrivalTime()));

                    // Change state to TERMINATED using system call.
                    SystemCalls.sysChangeProcessState(pcb, ProcessState.TERMINATED);

                    // Record Gantt chart entry.
                    tracker.addGanttChartEntry(new GanttChartEntry(pcb.getProcessId(), startTime, finishTime));

                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished.");
                    SystemCalls.sysProcessFinished(tracker, pcb);
                }
            } else {
                try {
                    Thread.sleep(100); // Wait when there's no job ready
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("FCFS Scheduler finished.");
    }
}