public class MemoryLoadingThread implements Runnable {
    private JobQueue jobQueue;
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private boolean running = true; // ✅ Flag to stop thread

    public MemoryLoadingThread(JobQueue jobQueue, ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
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
                    // Check if process is too large for memory
                    if (pcb.getMemoryRequired() > memoryManager.getTotalMemory()) {
                        System.err.println("ERROR: Process " + pcb.getProcessId() + 
                                          " requires " + pcb.getMemoryRequired() + 
                                          "MB which exceeds total memory capacity (" + 
                                          memoryManager.getTotalMemory() + "MB). Process skipped.");
                        // Skip this process - don't add it back to the queue
                        continue;
                    }
                    
                    if (SystemCalls.sysAllocMemory(memoryManager, pcb.getMemoryRequired())) {
                        readyQueue.addReadyPCB(pcb);
                        System.out.println("Process " + pcb.getProcessId() + 
                                          " loaded into memory (using " + pcb.getMemoryRequired() + 
                                          "MB, total used: " + memoryManager.getUsedMemory() + 
                                          "MB of " + memoryManager.getTotalMemory() + "MB)");
                    } else {
                        // Put back in queue and try again later
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
