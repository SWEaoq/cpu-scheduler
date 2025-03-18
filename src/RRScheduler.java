public class RRScheduler implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private int timeQuantum;   // in ms

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
                        pcb.setStartTime(System.currentTimeMillis());
                    }

                    int remaining = pcb.getRemainingBurstTime();
                    int slice = Math.min(remaining, timeQuantum);

                    try {
                        Thread.sleep(slice * TIME_UNIT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    pcb.setRemainingBurstTime(remaining - slice);

                    if (pcb.getRemainingBurstTime() <= 0) {
                        pcb.setFinishTime(System.currentTimeMillis());
                        pcb.setState(ProcessState.TERMINATED);

                        long arrival = pcb.getArrivalTime();
                        long finish = pcb.getFinishTime();
                        long start = pcb.getStartTime();

                        // Debugging: Print timestamp values before calculating
                        System.out.println("Process " + pcb.getProcessId() +
                            " | Arrival: " + arrival +
                            " | Start: " + start +
                            " | Finish: " + finish);

                        pcb.setWaitingTime((int) Math.max(0, start - arrival)); 
                        pcb.setTurnaroundTime((int) Math.max(0, finish - arrival));

                        memoryManager.freeMemory(pcb.getMemoryRequired());

                        System.out.println("RR: Process " + pcb.getProcessId() + " finished at time: " + finish);

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
    }
}
