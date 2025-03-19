import java.util.ArrayList;
import java.util.List;

public class ProcessTracker {
    private int totalProcesses;
    private int completedProcesses;
    private List<PCB> finishedProcesses;

    public ProcessTracker(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        this.completedProcesses = 0;
        this.finishedProcesses = new ArrayList<>();
    }

    // Called when a process finishes its execution.
    public synchronized void processFinished(PCB pcb) {
        finishedProcesses.add(pcb);
        completedProcesses++;
    }

    // Returns true when all processes have been completed.
    public synchronized boolean isAllProcessesFinished() {
        return completedProcesses >= totalProcesses;
    }
    
    // Calculate average waiting time among finished processes.
    public double getAverageWaitingTime() {
        double sum = 0;
        for (PCB pcb : finishedProcesses) {
            sum += pcb.getWaitingTime();
        }
        return finishedProcesses.size() > 0 ? sum / finishedProcesses.size() : 0;
    }
    
    // Calculate average turnaround time among finished processes.
    public double getAverageTurnaroundTime() {
        double sum = 0;
        for (PCB pcb : finishedProcesses) {
            sum += pcb.getTurnaroundTime();
        }
        return finishedProcesses.size() > 0 ? sum / finishedProcesses.size() : 0;
    }
}