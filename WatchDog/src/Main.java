import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import gui.WebServer;
import httpclient.HttpResponse;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static Process restartProcess(String jFile, String directory) throws IOException {
		System.out.println("java -jar \"" + jFile + "\" \"" + directory + "\"");
		return Runtime.getRuntime().exec("java -jar \"" + jFile + "\" \"" + directory + "\"");
	}
	
	public static void main(String[] args) {

		String directory = args[0];
		String jFile = args[1];

		StringBuffer buffer = new StringBuffer();
		buffer.append("GET /basic/BasicServlet HTTP/1.1\r\n");
		buffer.append("Host: http://localhost:8080\r\n");
		buffer.append("Connection: Keep-Alive\r\n");
		buffer.append("User-Agent: HttpTestClient/1.0\r\n");
		buffer.append("Accept: text/html,text/plain,application/xml,application/json\r\n");
		buffer.append("Accept-Language: en-US,en;q=0.8\r\n");
		buffer.append("\r\n");

		String request = buffer.toString();
	

		try {

			Process process = restartProcess(jFile, directory);
			while (true) {

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Socket s = new Socket();
					s.connect(new InetSocketAddress("localhost", 8080), 10000);
					s.setSoTimeout(3000);
					
					OutputStream out = s.getOutputStream();
					out.write(request.getBytes());
					out.flush();
					
					InputStream in = s.getInputStream();
					HttpResponse response = HttpResponse.read(in);

					s.close();

					if (!response.getStatusLine().equals("HTTP/1.1 200 OK")) {
						process.destroy();
						process = restartProcess(jFile, directory);
						System.out.println("Restarting murdered thread");
					}
					
				} catch (SocketTimeoutException | ConnectException e) {
					process.destroy();
					process = restartProcess(jFile, directory);
					System.out.println("Restarting murdered thread");
				} 

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
