public class GanttChartEntry {
    private int processId;
    private long startTime;
    private long endTime;

    public GanttChartEntry(int processId, long startTime, long endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getProcessId() {
        return processId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}