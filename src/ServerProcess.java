import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerProcess implements Runnable {
	private Socket socket;
	private static Pattern HTTPRequestPattern = Pattern.compile("GET /([^ ]*) HTTP/1.1");

	
	public ServerProcess(Socket socket){
		this.socket = socket;
	}
	@Override
	public void run() {
		try{
		
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			OutputStream output = socket.getOutputStream();
			BufferedReader br;
		
			//Reads in the request from the client, and parses the file name from the GET request
			String httpRequest = input.readLine();
			String getRequest = processHTTP(httpRequest);
			
			//Attempts to open the file from GET request, if it doesn't exist it opens index
			//Better implementation is to return 404
			try{
				br = new BufferedReader(new FileReader(getRequest));

			}catch(IOException io){
				System.out.println("Unable to open the file requested: " + io);
				getRequest = "index.html";
				br = new BufferedReader(new FileReader(getRequest));
			}
			
			//Write the contents of the file to the socket
			outputToSocket(br, output);
			
			br.close();
	        output.close();
			input.close();
		}catch(IOException io){
			System.out.println("Error processing request: " + io);
		}
		finally{
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Unable to close client socket: " + e);
			}
		}
		
	}
	
	//Output contents of the file to the socket
	private void outputToSocket(BufferedReader br, OutputStream output) throws IOException{
		String line;
		while((line = br.readLine()) != null){
			output.write(line.getBytes());
		}
	}
	
	//Parses the GET request and returns the file name
	private String processHTTP(String line){
		Matcher match = HTTPRequestPattern.matcher(line);
		if (!match.find() || match.hitEnd()) {
			throw new IllegalArgumentException("Line does not match pattern");
		}
	
		return match.group(1);
	}
}
