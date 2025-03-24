import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private ProcessTracker tracker;
    
    private static final int TIME_UNIT_MS = 100; 
    private static final int AGING_INTERVAL_MS = 200; // every 200ms, effective priority increases by 1
    private static final int STARVATION_THRESHOLD_MS = 2000; // threshold to consider a process starved

    public PriorityScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.tracker = tracker;
    }
    
    @Override
    public void run() {
        while (!tracker.isAllProcessesFinished()) {
            PCB pcb = getHighestPriorityPCB();
            if (pcb != null) {
                long currentTime = System.currentTimeMillis();
                long waitingTime = currentTime - pcb.getArrivalTime();
                // If waiting time exceeds threshold, log potential starvation
                if (waitingTime >= STARVATION_THRESHOLD_MS) {
                    System.out.println("STARVATION ALERT: Process " + pcb.getProcessId() 
                                       + " has been waiting for " + waitingTime + " ms.");
                }
                
                SystemCalls.sysChangeProcessState(pcb, ProcessState.RUNNING);

                if (pcb.getStartTime() == -1) {
                    pcb.setStartTime(currentTime);
                }

                long startTime = pcb.getStartTime();
                int burstTime = pcb.getRemainingBurstTime();

                try {
                    Thread.sleep(burstTime * TIME_UNIT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pcb.setRemainingBurstTime(0);
                pcb.setFinishTime(System.currentTimeMillis());
                long finishTime = pcb.getFinishTime();

                pcb.setWaitingTime((int) (startTime - pcb.getArrivalTime()));
                pcb.setTurnaroundTime((int) (finishTime - pcb.getArrivalTime()));

                SystemCalls.sysChangeProcessState(pcb, ProcessState.TERMINATED);
                SystemCalls.sysFreeMemory(memoryManager, pcb.getMemoryRequired());
                SystemCalls.sysProcessFinished(tracker, pcb);

                // Record full process execution in Gantt chart.
                tracker.addGanttChartEntry(new GanttChartEntry(pcb.getProcessId(), startTime, finishTime));

                System.out.println("PRIORITY: Process " + pcb.getProcessId() 
                                   + " (base priority=" + pcb.getPriority() + ") finished.");
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
        long currentTime = System.currentTimeMillis();
        
        while (!readyQueue.isEmpty()) {
            PCB pcb = readyQueue.pollReadyPCB();
            int effectivePriority = pcb.getPriority() + (int)((currentTime - pcb.getArrivalTime()) / AGING_INTERVAL_MS);
            if (best == null) {
                best = pcb;
            } else {
                int bestEffectivePriority = best.getPriority() + (int)((currentTime - best.getArrivalTime()) / AGING_INTERVAL_MS);
                if (effectivePriority > bestEffectivePriority) {
                    allPCBs.add(best);
                    best = pcb;
                } else {
                    allPCBs.add(pcb);
                }
            }
        }
        for (PCB p : allPCBs) {
            readyQueue.addReadyPCB(p);
        }
        return best;
    }
}