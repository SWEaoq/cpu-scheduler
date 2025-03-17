public class CPUSchedulerThread implements Runnable {
    private ReadyQueue readyQueue;
    private MemoryManager memoryManager;
    
    public CPUSchedulerThread(ReadyQueue readyQueue, MemoryManager memoryManager) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
    }
    
    @Override
    public void run() {
        while (true) {
            if (!readyQueue.isEmpty()) {
                PCB pcb = readyQueue.pollReadyPCB();
                if (pcb != null) {
                    pcb.setState(ProcessState.RUNNING);
                    // Simulate CPU run/burst here...
                    // After finishing:
                    pcb.setState(ProcessState.TERMINATED);
                    memoryManager.freeMemory(pcb.getMemoryRequired());
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
