/**
 * Created by Vegard on 11/04/16.
 */
public class CPU {

    private Queue queue;
    private Statistics statistics;
    private long maxCPUtime;
    private Gui gui;
    private Process activeProcess;
    private EventQueue eventQueue;


    public CPU(Queue CPUqueue, Statistics statistics, long maxCPUtime, Gui gui, EventQueue eventqueue) {
        this.queue = CPUqueue;
        this.statistics = statistics;
        this.maxCPUtime = maxCPUtime;
        this.gui = gui;
        this.activeProcess = null;
        this.eventQueue = eventqueue;
    }


    public void insertProcess(Process process, long clock) {
        queue.insert(process);
        if (queue.getQueueLength() == 1) {
            this.activeProcess = (Process) queue.removeNext();
            process(clock);
        }
        this.statistics.cpuQueueInserts++;
    }


    public void process(long clock) {
        if (activeProcess != null) {
            gui.setCpuActive(activeProcess);
            if (activeProcess.getremainingCpuTime() <= activeProcess.getTimeToNextIoOperation()) {
                activeProcess.setCpuTimeSpent(activeProcess.getremainingCpuTime());
                eventQueue.insertEvent(new Event(Constants.END_PROCESS, clock + activeProcess.getremainingCpuTime()));
                eventQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + activeProcess.getremainingCpuTime() + 1));
            } else if (activeProcess.getTimeToNextIoOperation() <= maxCPUtime) {
                activeProcess.setCpuTimeSpent(activeProcess.getTimeToNextIoOperation());
                eventQueue.insertEvent(new Event(Constants.IO_REQUEST, clock + activeProcess.getTimeToNextIoOperation()));
                eventQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + activeProcess.getTimeToNextIoOperation() + 1));
            } else {
                activeProcess.setCpuTimeSpent(maxCPUtime);
                activeProcess.updateTimeToIo(maxCPUtime);
                queue.insert(activeProcess);
                eventQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + maxCPUtime));
            }

        }
    }

    public void switchProcess(long clock) {
        if (queue.getQueueLength() >= 1) {
            this.activeProcess = (Process) queue.removeNext();
            process(clock);
            this.activeProcess.leftCpuQueue(clock);
        }
        else{
            gui.setCpuActive(null);
        }

    }

    public void timePassed(long timePassed) {
        statistics.avgCpuQueue += queue.getQueueLength() * timePassed;
        if (queue.getQueueLength() > statistics.largestCpuQueue) {
            statistics.largestCpuQueue = queue.getQueueLength();
        }
    }

    public Process getActiveProcess() {
        return this.activeProcess;
    }

}
