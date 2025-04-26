import java.util.concurrent.ConcurrentLinkedQueue;
public class ReadyQueue {
    private ConcurrentLinkedQueue<PCB> queue = new ConcurrentLinkedQueue<>();

    public void addReadyPCB(PCB pcb) {
        pcb.setState(ProcessState.READY);
        queue.add(pcb);
    }

    public PCB pollReadyPCB() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
