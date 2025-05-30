import java.util.concurrent.ConcurrentLinkedQueue;

public class JobQueue {
    private ConcurrentLinkedQueue<PCB> queue = new ConcurrentLinkedQueue<>();

    public void addJob(PCB pcb) {
        queue.add(pcb);
    }

    public PCB pollJob() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
