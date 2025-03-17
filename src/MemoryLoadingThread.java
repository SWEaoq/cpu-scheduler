public class MemoryLoadingThread implements Runnable {
    private JobQueue jobQueue;
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;

    public MemoryLoadingThread(JobQueue jobQueue, ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
    }

    @Override
    public void run() {
        while (true) {
            if (!jobQueue.isEmpty()) {
                PCB pcb = jobQueue.pollJob();
                if (pcb != null) {
                    // Check if there's enough memory
                    if (memoryManager.canAllocate(pcb.getMemoryRequired())) {
                        memoryManager.allocateMemory(pcb.getMemoryRequired());
                        readyQueue.addReadyPCB(pcb);
                    } else {
                        // Not enough memory, put the job back or wait?
                        // Option A: You could wait a bit and then recheck
                        // Option B: Immediately re-insert into the job queue
                        // For a simple approach, re-insert:
                        jobQueue.addJob(pcb);
                    }
                }
            }

            // Sleep to avoid busy-wait (tune interval as needed)
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
