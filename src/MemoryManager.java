public class MemoryManager {
    private int totalMemory;
    private int usedMemory;

    public MemoryManager(int totalMemory) {
        this.totalMemory = totalMemory;
        this.usedMemory = 0;
    }

    public synchronized boolean canAllocate(int memRequired) {
        return (usedMemory + memRequired) <= totalMemory;
    }

    public synchronized void allocateMemory(int memRequired) {
        usedMemory += memRequired;
    }

    public synchronized void freeMemory(int memSize) {
        usedMemory -= memSize;
        if (usedMemory < 0) {
            usedMemory = 0; // safety check
        }
    }

    public synchronized int getUsedMemory() {
        return usedMemory;
    }

    public synchronized int getTotalMemory() {
        return totalMemory;
    }
}
