# CPU Scheduler Simulation

This project simulates various CPU scheduling algorithms, including First-Come-First-Serve (FCFS), Round Robin (RR), and Priority Scheduling. It is designed to demonstrate how different scheduling techniques manage processes in a multitasking environment.

## Features

- **Job Queue and Ready Queue**: Handles process management and memory allocation.
- **Scheduling Algorithms**:
  - **FCFS**: Processes are executed in the order they arrive.
  - **Round Robin**: Time-sliced execution with configurable quantum.
  - **Priority Scheduling**: Processes are executed based on priority, with aging to prevent starvation.
- **Memory Management**: Simulates memory allocation and deallocation for processes.
- **Gantt Chart**: Visual representation of process execution.
- **Starvation Detection**: Alerts when a process waits too long in the ready queue.

## Project Structure

- `src/`: Contains all source code files.
- `jobs.text`: Input file containing process details (ID, burst time, priority, memory required).
- `.gitattributes`: Git configuration for text file normalization.

### Key Classes

- **MainSimulator**: Entry point for the simulation.
- **Schedulers**:
  - `FCFSScheduler`
  - `RRScheduler`
  - `PriorityScheduler`
- **Utilities**:
  - `MemoryManager`: Handles memory allocation.
  - `ProcessTracker`: Tracks process states and metrics.
  - `ReadyQueue` and `JobQueue`: Manage processes in different states.
  - `FileReadingThread`: Reads jobs from the input file.
  - `MemoryLoadingThread`: Loads jobs into memory.

## Input File Format

The input file (`jobs.text`) contains process details in the following format:

```
<ProcessID>:<BurstTime>:<Priority>;<MemoryRequired>
```

Example:
```
1:25:4;500
2:13:3;700
3:20:3;100
```

## How to Run

1. **Compile the Project**:
   Use any Java IDE (e.g., IntelliJ, Eclipse) or the command line:
   ```bash
   javac -d bin src/*.java
   ```

2. **Run the Simulator**:
   ```bash
   java -cp bin MainSimulator
   ```

3. **Provide Inputs**:
   - Path to the job file (e.g., `jobs.text`).
   - Total memory available (e.g., `2048` MB).
   - Choose a scheduling algorithm:
     - `[1]` FCFS
     - `[2]` Round Robin
     - `[3]` Priority Scheduling
     - `[4]` Run All Algorithms

4. **For Round Robin**:
   Enter the time quantum in milliseconds.

## Output

- **Average Waiting Time**: Average time processes spent in the ready queue.
- **Average Turnaround Time**: Average time from process arrival to completion.
- **Gantt Chart**: Displays process execution timeline.

Example Gantt Chart:
```
-------------------------------------------------
| Start (ms) | End (ms)  | Process ID |
-------------------------------------------------
| 0          | 250       | 1          |
| 250        | 500       | 2          |
-------------------------------------------------
```

## Customization

- Modify `jobs.text` to add or update processes.
- Adjust constants in the code (e.g., `TIME_UNIT_MS`, `AGING_INTERVAL_MS`) to simulate different scenarios.

## Requirements

- Java 8 or higher.
- A text editor or IDE for editing the input file and source code.

## Future Enhancements

- Add support for additional scheduling algorithms (e.g., Shortest Job Next).
- Implement a graphical user interface (GUI) for better visualization.
- Enhance memory management with paging or segmentation.

## License

This project is for educational purposes and is not licensed for commercial use.

## Author

Abdullah Alqobaisi
