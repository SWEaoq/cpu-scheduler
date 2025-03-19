public class SystemCalls {

    // Simulates a system call to allocate memory for a process.
    // Returns true if memory was successfully allocated.
    public static boolean sysAllocMemory(MemoryManager memoryManager, int memoryRequired) {
        if (memoryManager.canAllocate(memoryRequired)) {
            memoryManager.allocateMemory(memoryRequired);
            return true;
        }
        return false;
    }
    
    // Simulates a system call to free memory when a process terminates.
    public static void sysFreeMemory(MemoryManager memoryManager, int memoryRequired) {
        memoryManager.freeMemory(memoryRequired);
    }
    
    // Simulates a system call to change a process's state.
    public static void sysChangeProcessState(PCB pcb, ProcessState newState) {
        pcb.setState(newState);
    }
    
    // Notifies that a process has finished, recording this in the tracker.
    public static void sysProcessFinished(ProcessTracker tracker, PCB pcb) {
        tracker.processFinished(pcb);
    }
}