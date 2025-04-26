public class MemoryLoadingThread implements Runnable {
    private JobQueue jobQueue;
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    private boolean running = true;

    public MemoryLoadingThread(JobQueue jobQueue, ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            if (!jobQueue.isEmpty()) {
                PCB pcb = jobQueue.pollJob();
                if (pcb != null) {
                    if (pcb.getMemoryRequired() > memoryManager.getTotalMemory()) {
                        System.err.println("ERROR: Process " + pcb.getProcessId() + 
                                          " requires " + pcb.getMemoryRequired() + 
                                          "MB which exceeds total memory capacity (" + 
                                          memoryManager.getTotalMemory() + "MB). Process skipped.");
                        continue;
                    }
                    
                    if (SystemCalls.sysAllocMemory(memoryManager, pcb.getMemoryRequired())) {
                        readyQueue.addReadyPCB(pcb);
                        System.out.println("Process " + pcb.getProcessId() + 
                                          " loaded into memory (using " + pcb.getMemoryRequired() + 
                                          "MB, total used: " + memoryManager.getUsedMemory() + 
                                          "MB of " + memoryManager.getTotalMemory() + "MB)");
                    } else {
                        jobQueue.addJob(pcb);
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("MemoryLoadingThread stopped.");
    }
}
