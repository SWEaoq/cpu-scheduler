public class SystemCalls {

    public static boolean sysAllocMemory(MemoryManager memoryManager, int memoryRequired) {
        if (memoryManager.canAllocate(memoryRequired)) {
            memoryManager.allocateMemory(memoryRequired);
            return true;
        }
        return false;
    }
    
    public static void sysFreeMemory(MemoryManager memoryManager, int memoryRequired) {
        memoryManager.freeMemory(memoryRequired);
    }
    
    public static void sysChangeProcessState(PCB pcb, ProcessState newState) {
        pcb.setState(newState);
    }
    
    public static void sysProcessFinished(ProcessTracker tracker, PCB pcb) {
        tracker.processFinished(pcb);
    }
}