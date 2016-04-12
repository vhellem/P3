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
    private long lastEvent ;

    public CPU(Queue CPUqueue, Statistics statistics, long maxCPUtime, Gui gui, EventQueue eventqueue) {
        this.queue = CPUqueue;
        this.statistics = statistics;
        this.maxCPUtime = maxCPUtime;
        this.gui = gui;
        this.activeProcess = null;
        this.eventQueue = eventqueue;
        lastEvent = 0 ;
    }


    public void insertProcess(Process process, long clock) {
        queue.insert(process);
        this.statistics.cpuQueueInserts++;
        if (activeProcess==null) {
            this.activeProcess = (Process) queue.removeNext();
            process(clock);
        }

    }


    public void process(long clock) {
        if (activeProcess != null) {
            activeProcess.enteredCPU(clock);
            gui.setCpuActive(activeProcess);
            if (activeProcess.getremainingCpuTime() <= activeProcess.getTimeToNextIoOperation()) {
                activeProcess.setCpuTimeSpent(activeProcess.getremainingCpuTime());
                eventQueue.insertEvent(new Event(Constants.END_PROCESS, clock + activeProcess.getremainingCpuTime()));

            } else if (activeProcess.getTimeToNextIoOperation() <= maxCPUtime) {
                activeProcess.setCpuTimeSpent(activeProcess.getTimeToNextIoOperation());
                eventQueue.insertEvent(new Event(Constants.IO_REQUEST, clock + activeProcess.getTimeToNextIoOperation()));

            } else {
                activeProcess.setCpuTimeSpent(maxCPUtime);
                activeProcess.updateTimeToIo(maxCPUtime);
                statistics.forcedSwitched++;

                eventQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + maxCPUtime));
            }

        }
    }

    public Process switchProcess(long clock) {
        Process prev = activeProcess;
        if (prev!=null){
            prev.leftCPU(clock);
        }
        pop(clock);
        if(activeProcess != null){
            activeProcess.enteredCPU(clock);
            process(clock);
        }
        return prev ;


    }

    public void timePassed(long timePassed) {
        statistics.avgCpuQueue += queue.getQueueLength() * timePassed;
        if (queue.getQueueLength() > statistics.largestCpuQueue) {
            statistics.largestCpuQueue = queue.getQueueLength();
        }
    }

    public boolean isIdle(){
        return this.activeProcess==null;
    }

    public void pop(long clock) {

        if(this.activeProcess == null) {
            // Process has been idle
            this.statistics.totalCpuIdleTime += clock - lastEvent;
        } else {
            // Process has been running
            this.statistics.totalCpuProcessingTime += clock - lastEvent;
        }
        lastEvent = clock;

        if(queue.isEmpty()) {
            this.activeProcess = null;
        } else {
            this.activeProcess = (Process)queue.removeNext();
        }

        gui.setCpuActive(this.activeProcess);


    }


    public Process getRunning(long clock) {
        if (activeProcess!= null){
            activeProcess.leftCpuQueue(clock);

        }
        return activeProcess;
    }

}
