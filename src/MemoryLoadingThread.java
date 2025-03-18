public class MemoryLoadingThread implements Runnable {
    private JobQueue jobQueue;
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private ProcessTracker tracker;
    private boolean running = true; // ✅ Flag to stop thread

    public MemoryLoadingThread(JobQueue jobQueue, ReadyQueue readyQueue, MemoryManager memoryManager, ProcessTracker tracker) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.tracker = tracker;
    }

    public void stopThread() {
        running = false; // ✅ Stop when simulation is done
    }

    @Override
    public void run() {
        while (running) {
            if (!jobQueue.isEmpty()) {
                PCB pcb = jobQueue.pollJob();
                if (pcb != null) {
                    if (memoryManager.canAllocate(pcb.getMemoryRequired())) {
                        memoryManager.allocateMemory(pcb.getMemoryRequired());
                        readyQueue.addReadyPCB(pcb);
                    } else {
                        jobQueue.addJob(pcb);
                    }
                }
            }

            try {
                Thread.sleep(100);  // Avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("MemoryLoadingThread stopped.");
    }
}
