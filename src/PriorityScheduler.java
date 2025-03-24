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
                SystemCalls.sysChangeProcessState(highestPriorityPCB, ProcessState.RUNNING);

                if (highestPriorityPCB.getStartTime() == -1) {
                    highestPriorityPCB.setStartTime(System.currentTimeMillis());
                }

                long startTime = highestPriorityPCB.getStartTime();
                int burstTime = highestPriorityPCB.getRemainingBurstTime();

                try {
                    Thread.sleep(burstTime * TIME_UNIT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                highestPriorityPCB.setRemainingBurstTime(0);
                highestPriorityPCB.setFinishTime(System.currentTimeMillis());
                long finishTime = highestPriorityPCB.getFinishTime();

                highestPriorityPCB.setWaitingTime((int) (startTime - highestPriorityPCB.getArrivalTime()));
                highestPriorityPCB.setTurnaroundTime((int) (finishTime - highestPriorityPCB.getArrivalTime()));

                SystemCalls.sysChangeProcessState(highestPriorityPCB, ProcessState.TERMINATED);
                SystemCalls.sysFreeMemory(memoryManager, highestPriorityPCB.getMemoryRequired());
                SystemCalls.sysProcessFinished(tracker, highestPriorityPCB);

                // Record full process execution in Gantt chart.
                tracker.addGanttChartEntry(new GanttChartEntry(highestPriorityPCB.getProcessId(), startTime, finishTime));

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