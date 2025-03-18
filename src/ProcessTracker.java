public class ProcessTracker {
    private int totalProcesses;
    private int completedProcesses;

    public ProcessTracker(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        this.completedProcesses = 0;
    }

    public synchronized void processFinished() {
        completedProcesses++;
    }

    public synchronized void incrementTotalProcesses() {
        totalProcesses++; // ✅ Now tracks new jobs from FileReadingThread
    }

    public synchronized boolean isAllProcessesFinished() {
        return completedProcesses >= totalProcesses;
    }
}
