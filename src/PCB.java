enum ProcessState {
    NEW,
    READY,
    RUNNING,
    WAITING,
    TERMINATED
}

public class PCB {
    
    private int processId;
    private int burstTime;
    private int remainingBurstTime;
    private int priority;
    private int memoryRequired;
    
    private int waitingTime;
    private int turnaroundTime;
    
    private ProcessState state;
    private long arrivalTime;
    private long startTime;
    private long finishTime;

    public PCB(int processId, int burstTime, int priority, int memoryRequired, long arrivalTime) {
        this.processId = processId;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.priority = priority;
        this.memoryRequired = memoryRequired;
        this.arrivalTime = arrivalTime;
        
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.startTime = -1;
        this.finishTime = -1;
        this.state = ProcessState.NEW;
    }

    public int getProcessId() {
        return processId;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(int remainingBurstTime) {
        this.remainingBurstTime = remainingBurstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getMemoryRequired() {
        return memoryRequired;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }


    @Override
    public String toString() {
        return "PCB{" +
               "ID=" + processId +
               ", burstTime=" + burstTime +
               ", remainingBurstTime=" + remainingBurstTime +
               ", priority=" + priority +
               ", memoryRequired=" + memoryRequired +
               ", waitingTime=" + waitingTime +
               ", turnaroundTime=" + turnaroundTime +
               ", state=" + state +
               ", arrivalTime=" + arrivalTime +
               ", startTime=" + startTime +
               ", finishTime=" + finishTime +
               '}';
    }
}
