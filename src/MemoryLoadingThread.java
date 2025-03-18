public class MemoryLoadingThread implements Runnable {
    private JobQueue jobQueue;
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private ProcessTracker tracker;

    public MemoryLoadingThread(JobQueue jobQueue, ReadyQueue readyQueue, MemoryManager memoryManager, ProcessTracker tracker) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        while (!tracker.isAllProcessesFinished()) {
            if (!jobQueue.isEmpty()) {
                PCB pcb = jobQueue.pollJob();
                if (pcb != null) {
                    if (memoryManager.canAllocate(pcb.getMemoryRequired())) {
                        memoryManager.allocateMemory(pcb.getMemoryRequired());
                        readyQueue.addReadyPCB(pcb);
                    } else {
                        jobQueue.addJob(pcb);  // Not enough memory, reinsert the job
                    }
                }
            }

            try {
                Thread.sleep(100);  // Avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Memory Loader finished.");
    }
}
