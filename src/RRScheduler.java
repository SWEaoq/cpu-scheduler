public class RRScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private int timeQuantum;   // in ms

    // For how fast to simulate each CPU time unit
    private static final int TIME_UNIT_MS = 100; 

    public RRScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, int timeQuantum) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.timeQuantum = timeQuantum; 
    }

    @Override
    public void run() {
        while (true) {
            if (!readyQueue.isEmpty()) {
                PCB pcb = readyQueue.pollReadyPCB();
                if (pcb != null) {
                    pcb.setState(ProcessState.RUNNING);

                    if (pcb.getStartTime() == -1) {
                        pcb.setStartTime((int) System.currentTimeMillis());
                    }

                    int remaining = pcb.getRemainingBurstTime();
                    int slice = Math.min(remaining, timeQuantum);

                    // "Run" the process for 'slice' units of CPU time
                    try {
                        Thread.sleep(slice * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update remaining burst time
                    pcb.setRemainingBurstTime(remaining - slice);

                    if (pcb.getRemainingBurstTime() <= 0) {
                        // Process is finished
                        pcb.setFinishTime((int) System.currentTimeMillis());
                        pcb.setState(ProcessState.TERMINATED);

                        int arrival = pcb.getArrivalTime();
                        int finish = pcb.getFinishTime();
                        int start = pcb.getStartTime();

                        pcb.setWaitingTime(start - arrival); 
                        pcb.setTurnaroundTime(finish - arrival);

                        // Free memory
                        memoryManager.freeMemory(pcb.getMemoryRequired());

                        System.out.println("RR: Process " + pcb.getProcessId() + " finished at time: " + finish);

                    } else {
                        // Process not finished, put it back to the ready queue
                        pcb.setState(ProcessState.READY);
                        readyQueue.addReadyPCB(pcb);
                    }
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
