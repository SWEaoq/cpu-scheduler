import java.util.ArrayList;
import java.util.List;

public class ProcessTracker {
    private int totalProcesses;
    private int completedProcesses;
    private List<PCB> finishedProcesses;
    private List<GanttChartEntry> ganttChartEntries;

    public ProcessTracker(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        this.completedProcesses = 0;
        this.finishedProcesses = new ArrayList<>();
        this.ganttChartEntries = new ArrayList<>();
    }

    public synchronized void processFinished(PCB pcb) {
        finishedProcesses.add(pcb);
        completedProcesses++;
    }

    public synchronized boolean isAllProcessesFinished() {
        if (completedProcesses >= totalProcesses) {
            return true;
        } else {
            return false;
        }
    }
    
    public double getAverageWaitingTime() {
        double sum = 0;
        for (PCB pcb : finishedProcesses) {
            sum += pcb.getWaitingTime();
        }
        if (finishedProcesses.size() > 0) {
            return sum / finishedProcesses.size();
        } else {
            return 0;
        }
    }
    
    public double getAverageTurnaroundTime() {
        double sum = 0;
        for (PCB pcb : finishedProcesses) {
            sum += pcb.getTurnaroundTime();
        }
        if (finishedProcesses.size() > 0) {
            return sum / finishedProcesses.size();
        } else {
            return 0;
        }
    }
    
    public synchronized void addGanttChartEntry(GanttChartEntry entry) {
        ganttChartEntries.add(entry);
    }
    
    public synchronized List<GanttChartEntry> getGanttChartEntries() {
        return ganttChartEntries;
    }

    public synchronized List<PCB> getFinishedProcesses() {
        return new ArrayList<>(finishedProcesses);
    }
}