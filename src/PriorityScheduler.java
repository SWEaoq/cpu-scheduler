import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;

    // How fast to simulate CPU time
    private static final int TIME_UNIT_MS = 100; 

    public PriorityScheduler(ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
    }

    @Override
    public void run() {
        while (true) {
            PCB highestPriorityPCB = getHighestPriorityPCB();
            if (highestPriorityPCB != null) {
                highestPriorityPCB.setState(ProcessState.RUNNING);

                if (highestPriorityPCB.getStartTime() == -1) {
                    highestPriorityPCB.setStartTime((int) System.currentTimeMillis());
                }

                int burstTime = highestPriorityPCB.getRemainingBurstTime();

                // Simulate
                try {
                    Thread.sleep(burstTime * TIME_UNIT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                highestPriorityPCB.setRemainingBurstTime(0);
                highestPriorityPCB.setFinishTime((int) System.currentTimeMillis());
                highestPriorityPCB.setState(ProcessState.TERMINATED);

                long arrival = highestPriorityPCB.getArrivalTime();
                long start = highestPriorityPCB.getStartTime();
                long finish = highestPriorityPCB.getFinishTime();

                highestPriorityPCB.setWaitingTime((int)(start - arrival));
                highestPriorityPCB.setTurnaroundTime((int)(finish - arrival));

                // free memory
                memoryManager.freeMemory(highestPriorityPCB.getMemoryRequired());

                System.out.println("PRIORITY: Process " + highestPriorityPCB.getProcessId() 
                                   + " (priority=" + highestPriorityPCB.getPriority() + ") finished.");
            }

            // Sleep a bit to avoid busy-wait
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds and removes the highest-priority PCB from the ReadyQueue.
     * If your priority is “the smaller the number, the higher the priority”,
     * we pick the one with the lowest priority number.
     */
    private PCB getHighestPriorityPCB() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Temporarily hold all PCBs
        List<PCB> allPCBs = new ArrayList<>();
        PCB best = null;

        // Poll everything from the queue
        while (!readyQueue.isEmpty()) {
            PCB pcb = readyQueue.pollReadyPCB();
            if (best == null) {
                best = pcb;
            } else {
                // If pcb's priority < best's priority, it's higher priority
                if (pcb.getPriority() < best.getPriority()) {
                    allPCBs.add(best); // put the old 'best' back in the list
                    best = pcb;
                } else {
                    allPCBs.add(pcb);
                }
            }
        }

        // Re-insert everything except the best
        for (PCB p : allPCBs) {
            readyQueue.addReadyPCB(p);
        }

        return best;
    }
}
