public class RRScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private int timeQuantum;
    private ProcessTracker tracker;

    private static final int TIME_UNIT_MS = 100; 

    public RRScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, int timeQuantum, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.timeQuantum = timeQuantum; // Keep dynamic
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

                    int slice = Math.min(pcb.getRemainingBurstTime(), timeQuantum);
                    try {
                        Thread.sleep(slice * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    pcb.setRemainingBurstTime(pcb.getRemainingBurstTime() - slice);

                    if (pcb.getRemainingBurstTime() <= 0) {
                        pcb.setFinishTime(System.currentTimeMillis());
                        pcb.setState(ProcessState.TERMINATED);
                        memoryManager.freeMemory(pcb.getMemoryRequired());
                        tracker.processFinished();
                        System.out.println("RR: Process " + pcb.getProcessId() + " finished.");
                    } else {
                        pcb.setState(ProcessState.READY);
                        readyQueue.addReadyPCB(pcb);
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("RR Scheduler finished.");
    }
}
