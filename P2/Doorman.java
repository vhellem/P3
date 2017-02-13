package P2;
/**
 * This class implements the doorman's part of the
 * Barbershop thread synchronization example.
 */
import P2.Globals;
public class Doorman implements Runnable{
	/**
	 * Creates a new doorman.
	 * @param queue		The customer queue.
	 * @param gui		A reference to the GUI interface.
	 */
	
	CustomerQueue queue ; 
	Gui gui ;
	Thread thread; 
	public Doorman(CustomerQueue queue, Gui gui) { 
		this.gui = gui; 
		this.queue = queue; 
		this.thread = new Thread(this, "doorman");
	}

	/**
	 * Starts the doorman running as a separate thread.
	 */
	public void startThread() {
		thread.start();
	}

	/**
	 * Stops the doorman thread.
	 */
	public void stopThread() {
		
	}

	@Override
	public void run() {
		Customer c;
		
		while(true){
			c = new Customer();
			queue.add();
			queue.produce(c);
			gui.println("Customer added to queue");
			
			try {
				Thread.sleep(Globals.doormanSleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	// Add more methods as needed
}
