import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


public class Server implements Runnable {
	private ServerSocket serverSocket;
	private static int port = 9000;
	private final int NUM_THREADS = 10;
	private ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
	private boolean isRunning = true;
	
	public void run(){
		//Opens up a server socket
		try{
			this.serverSocket = new ServerSocket(port);
			System.out.println("Server listening on port: " + port);
		}
		catch(IOException io){
			System.out.println("Port error: " + io);
			return;
		}
		
		//Listens on the socket for connection, if a connection is made it tries to accept it
		while(isRunning){
			Socket socket = null;
			try{
				socket = serverSocket.accept();
			}catch(IOException io){
				if(!isRunning)
					break;
				System.out.println("Failed to open a socket with the server: " + io);
			}
			//Start a new thread from a threadpool
			try{
				threadPool.execute(new ServerProcess(socket));
			}catch(RejectedExecutionException re){
				System.out.println("Trying to run tasks after shutdown or too many theads: " + re);
				break;
			}
		}
		
	}
	//Closes socket, shutsdown all of the threads and waits for them to finish
	public synchronized void stop(){
		isRunning = false;
		try{
			serverSocket.close();
		}catch(IOException io){
			System.out.println("Problem closing server: " + io);
		}
		threadPool.shutdown();
        
		try {
            	threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
            	System.out.println("Failing to termiante all threads: " + e);
            }
		
	}
}
