package P2;

/**
 * This class implements the barber's part of the
 * Barbershop thread synchronization example.
 */
public class Barber implements Runnable{
	/**
	 * Creates a new barber.
	 * @param queue		The customer queue.
	 * @param gui		The GUI.
	 * @param pos		The position of this barber's chair
	 */
	
	CustomerQueue queue;
	Gui gui; 
	int pos;
	Thread thread ;
	
	public Barber(CustomerQueue queue, Gui gui, int pos) { 
		this.queue = queue; 
		this.gui = gui; 
		this.pos = pos; 
		
		this.thread=new Thread(this, "barber "+ pos);
	}

	/**
	 * Starts the barber running as a separate thread.
	 */
	public void startThread() {
		thread.start();
	}

	/**
	 * Stops the barber thread.
	 */
	public void stopThread() {
		
	}

	@Override
	public void run() {
		Customer c; 
		
		while(true){
			queue.take();
			c = queue.consume();
			
			gui.fillBarberChair(pos, c);
			gui.println("Customer placed in chair " + pos);
			try {
				Thread.sleep(Globals.barberWork);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.emptyBarberChair(pos);
			gui.println("Customer finished in char " + pos);
			try {
				gui.barberIsSleeping(pos);
				gui.println("Barber " + pos + " is sleeping");
				Thread.sleep(Globals.barberSleep);
				gui.barberIsAwake(pos);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	// Add more methods as needed
}

