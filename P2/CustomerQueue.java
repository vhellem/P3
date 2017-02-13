package P2;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements a queue of customers as a circular buffer.
 */
import P2.Globals;
public class CustomerQueue {
	/**
	 * Creates a new customer queue.
	 * @param queueLength	The maximum length of the queue.
	 * @param gui			A reference to the GUI interface.
	 */
	Queue<Customer> queue; 
	int queueLength;
	int pointerStart;
	int pointerEnd;
	
	Gui gui;
    public CustomerQueue(int queueLength, Gui gui) {
    	this.gui = gui; 
		queue = new LinkedList<Customer>();
		this.queueLength =queueLength;
		pointerStart=0;
		pointerEnd=0;
	
	}
   
    public synchronized void add(){
    	if (queue.size()==queueLength){
    		try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else{
    	notify();}
    }
    public synchronized void take(){
    	if(queue.size()==0){
    		try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else{
    		notify();
    	}
    }
    
    public synchronized Customer consume(){
    	gui.emptyLoungeChair(pointerStart);
    	Customer c = queue.remove();
    	
    	pointerStart++;
    	if(pointerStart==queueLength){
    		pointerStart=0;
    	}
    	return c;
    }
    
    public synchronized void produce(Customer c){
    	gui.fillLoungeChair(pointerEnd, c);
    	pointerEnd++;
    	if(pointerEnd==queueLength){
    		pointerEnd=0;
    	}
    	
    	
    	queue.add(c);
    }
	// Add more methods as needed
}
