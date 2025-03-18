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

                    pcb.setRemainingBurstTime(0);
                    pcb.setState(ProcessState.TERMINATED);
                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished.");
                    
                    tracker.processFinished();
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