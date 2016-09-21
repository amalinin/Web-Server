import java.io.IOException;


public class Main {
	public static void main(String[] args) throws IOException{
		Server tcp = new Server();
		
		new Thread(tcp).start();
		try {
		    Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();  
		}
		System.out.println("Stopping Server");
		tcp.stop();
	}
}
