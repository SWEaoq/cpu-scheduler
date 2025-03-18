import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private ProcessTracker tracker;

    private static final int TIME_UNIT_MS = 100; 

    public PriorityScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        while (!tracker.isAllProcessesFinished()) {
            PCB highestPriorityPCB = getHighestPriorityPCB();
            if (highestPriorityPCB != null) {
                highestPriorityPCB.setState(ProcessState.RUNNING);

                if (highestPriorityPCB.getStartTime() == -1) {
                    highestPriorityPCB.setStartTime(System.currentTimeMillis());
                }

                int burstTime = highestPriorityPCB.getRemainingBurstTime();

                try {
                    Thread.sleep(burstTime * TIME_UNIT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                highestPriorityPCB.setRemainingBurstTime(0);
                highestPriorityPCB.setFinishTime(System.currentTimeMillis());
                highestPriorityPCB.setState(ProcessState.TERMINATED);

                long arrival = highestPriorityPCB.getArrivalTime();
                long start = highestPriorityPCB.getStartTime();
                long finish = highestPriorityPCB.getFinishTime();

                highestPriorityPCB.setWaitingTime((int) (start - arrival));
                highestPriorityPCB.setTurnaroundTime((int) (finish - arrival));

                memoryManager.freeMemory(highestPriorityPCB.getMemoryRequired());

                tracker.processFinished(); // Track completion

                System.out.println("PRIORITY: Process " + highestPriorityPCB.getProcessId() 
                                   + " (priority=" + highestPriorityPCB.getPriority() + ") finished.");
            }

            try {
                Thread.sleep(50); // Avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Priority Scheduler finished.");
    }

    /**
     * Finds and removes the highest-priority PCB from the ReadyQueue.
     * If priority is “lower number = higher priority”,
     * we pick the one with the smallest priority number.
     */
    private PCB getHighestPriorityPCB() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        List<PCB> allPCBs = new ArrayList<>();
        PCB best = null;

        while (!readyQueue.isEmpty()) {
            PCB pcb = readyQueue.pollReadyPCB();
            if (best == null || pcb.getPriority() < best.getPriority()) {
                if (best != null) {
                    allPCBs.add(best);
                }
                best = pcb;
            } else {
                allPCBs.add(pcb);
            }
        }

        for (PCB p : allPCBs) {
            readyQueue.addReadyPCB(p);
        }

        return best;
    }
}
