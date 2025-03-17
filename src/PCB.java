/**
 * Enumeration for process states.
 */
enum ProcessState {
    NEW,
    READY,
    RUNNING,
    WAITING,
    TERMINATED
}

public class PCB {
    
    // Basic process info
    private int processId;
    private int burstTime;
    private int remainingBurstTime;  
    private int priority;            // lower number = lower priority
    private int memoryRequired;      // in MB
    
    // Timing metrics
    private int waitingTime;         // how long the process spent waiting in the ready queue
    private int turnaroundTime;      // total time from arrival to completion
    
    // Additional info
    private ProcessState state;      // NEW, READY, RUNNING, WAITING, TERMINATED
    private int arrivalTime;         // to track arrival times
    private int startTime;           // time the process first got the CPU
    private int finishTime;          // time the process finished

    public PCB(int processId, int burstTime, int priority, int memoryRequired, int arrivalTime) {
        this.processId = processId;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime; // to be decremented during scheduling
        this.priority = priority;
        this.memoryRequired = memoryRequired;
        this.arrivalTime = arrivalTime;
        
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.startTime = -1;  // indicates not started yet
        this.finishTime = -1; // indicates not finished yet
        this.state = ProcessState.NEW;
    }

    // -------------------
    // Getters and Setters
    // -------------------

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

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
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
