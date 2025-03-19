public class RRScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private int timeQuantum;
    private ProcessTracker tracker;

    private static final int TIME_UNIT_MS = 100; 

    public RRScheduler(ReadyQueue readyQueue, MemoryManager memoryManager, int timeQuantum, ProcessTracker tracker) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.timeQuantum = timeQuantum;
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
                        long arrival = pcb.getArrivalTime();
                        long finish = pcb.getFinishTime();
                        pcb.setTurnaroundTime((int) (finish - arrival));
                        // Waiting time = Turnaround time - original burst time
                        pcb.setWaitingTime((int) (finish - arrival - pcb.getBurstTime()));

                        // Debug logging – add these lines
                        System.out.println("Process " + pcb.getProcessId() + 
                        ": arrival=" + arrival + 
                        ", start=" + pcb.getStartTime() + 
                        ", finish=" + finish + 
                        ", original burst=" + pcb.getBurstTime() +
                        ", waiting=" + pcb.getWaitingTime() +
                        ", turnaround=" + pcb.getTurnaroundTime());

                        SystemCalls.sysChangeProcessState(pcb, ProcessState.TERMINATED);
                        SystemCalls.sysFreeMemory(memoryManager, pcb.getMemoryRequired());
                        SystemCalls.sysProcessFinished(tracker, pcb);
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
