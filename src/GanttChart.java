import java.util.List;

public class GanttChart {
    private List<GanttChartEntry> entries;

    public GanttChart(List<GanttChartEntry> entries) {
        this.entries = entries;
        if (!entries.isEmpty()) {
            entries.sort((a, b) -> Long.compare(a.getStartTime(), b.getStartTime()));
        }
    }

    
    public void displayWithTimeline() {
        if (entries.isEmpty()) {
            System.out.println("No processes to display in Gantt Chart.");
            return;
        }
        
        long simulationStart = entries.get(0).getStartTime();
        long simulationEnd = simulationStart; 
        for (GanttChartEntry entry : entries) {
            if (entry.getEndTime() > simulationEnd) {
                simulationEnd = entry.getEndTime();
            }
        }
        
        System.out.println("\nGantt Chart:");
        System.out.println("----------------------------------------------------------");
        System.out.println("| Start | End   | PID | Timeline");
        System.out.println("----------------------------------------------------------");
        

        long duration = simulationEnd - simulationStart;
        int scale = Math.max(1, (int)(duration / 40));
        
        for (GanttChartEntry entry : entries) {
            long relativeStart = entry.getStartTime() - simulationStart;
            long relativeEnd = entry.getEndTime() - simulationStart;
            
            int startPos = (int)(relativeStart / scale);
            int barLength = Math.max(1, (int)((relativeEnd - relativeStart) / scale));
            
            StringBuilder timeline = new StringBuilder();
            timeline.append(" ".repeat(startPos));
            timeline.append("P").append(entry.getProcessId()).append(":");
            timeline.append("=".repeat(barLength));
            
            System.out.printf("| %-5d | %-5d | %-3d | %s\n", 
                           relativeStart, relativeEnd, entry.getProcessId(), timeline);
        }
        
        System.out.println("----------------------------------------------------------");
        System.out.println("Scale: Each '=' represents approximately " + scale + " ms");
    }
}
