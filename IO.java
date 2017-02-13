/**
 * Created by Vegard on 11/04/16.
 */
public class IO {
    private Queue IOQueue ;
    private Statistics statistics ;
    private long avgIOTime ;
    private Gui gui ;
    private Process activeProcess ;
    private EventQueue eventqueue ;

    public IO(Queue IOQueue, Statistics statistics, Gui gui, EventQueue eventqueue, long avgIOTime) {
        this.IOQueue = IOQueue;
        this.statistics = statistics;
        this.avgIOTime = avgIOTime ;
        this.gui = gui;
        this.eventqueue = eventqueue;
    }

    public void insertProcess(Process process, long clock){
        IOQueue.insert(process);
        process.enterIOQueue(clock);
        statistics.processPlacedInIOQueue++;
    }


    public Process IO_Completed(long clock) {
        Process finishedIoProcess = this.activeProcess;

        if(activeProcess != null) {
            // Update statistics
            activeProcess.leftIO(clock);
            statistics.ioOperations++;

        }
        pop();
        if(activeProcess != null) {
            // If next in line, schedule END_IO event.
            activeProcess.enteredIO(clock);
            eventqueue.insertEvent(new Event(Constants.END_IO, clock+(int)(Math.random() * (1.2*avgIOTime - 0.8*avgIOTime) + 0.8*avgIOTime)));
        }
        return finishedIoProcess;
    }

    public void pop() {
        if(IOQueue.isEmpty()) {
            this.activeProcess = null;
        } else {
            this.activeProcess = (Process)IOQueue.removeNext();
        }
        gui.setIoActive(this.activeProcess);
    }

    public boolean isIdle(){
        return this.activeProcess==null;
    }


    public void timePassed(long timePassed) {
        statistics.ioQueueLengthTime += IOQueue.getQueueLength()*timePassed;
        if (IOQueue.getQueueLength() > statistics.ioQueueLargestLength) {
            statistics.ioQueueLargestLength = IOQueue.getQueueLength();
        }
    }
}
