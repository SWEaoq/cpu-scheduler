public class FCFSScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;

    // Used for simulating how long 1 unit of CPU time should “sleep” in ms
    private static final int TIME_UNIT_MS = 100;  

    public FCFSScheduler(ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
    }

    @Override
    public void run() {
        while (true) {
            if (!readyQueue.isEmpty()) {
                PCB pcb = readyQueue.pollReadyPCB();
                if (pcb != null) {
                    // Mark the process as RUNNING
                    pcb.setState(ProcessState.RUNNING);

                    // If it's the first time the process is scheduled
                    if (pcb.getStartTime() == -1) {
                        pcb.setStartTime((int) System.currentTimeMillis());
                    }

                    int burstTime = pcb.getRemainingBurstTime();

                    // Simulate running the entire burst time
                    try {
                        // Sleep for (remaining burstTime * TIME_UNIT_MS)
                        Thread.sleep(burstTime * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Process completes
                    pcb.setFinishTime((int) System.currentTimeMillis());
                    pcb.setRemainingBurstTime(0);
                    pcb.setState(ProcessState.TERMINATED);

                    // Calculate basic metrics
                    long arrival = pcb.getArrivalTime();
                    long start = pcb.getStartTime();
                    long finish = pcb.getFinishTime();
                    pcb.setWaitingTime((int)(start - arrival)); 
                    pcb.setTurnaroundTime((int)(finish - arrival));

                    // Free memory
                    memoryManager.freeMemory(pcb.getMemoryRequired());

                    // (Optional) print out or log finishing
                    System.out.println("FCFS: Process " + pcb.getProcessId() + " finished at time: " + finish);
                }
            }

            // Sleep a bit to reduce busy-wait
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
