import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;


public class Small implements IHttpServlet {
	
	public void handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
	
		File f = new File("C:\\Users\\petryjc\\Desktop\\small.txt");
		// Lets get content length in bytes
		long length = f.length();
		Thread.currentThread().setPriority((int) Math.round(Math.max(Thread.MAX_PRIORITY - Math.log10(length),0)));
		httpResponse.put(Protocol.CONTENT_LENGTH, length + "");
		try {
			BufferedOutputStream out = httpResponse.getWriter();
			out.write(Files.readAllBytes(f.toPath()));
	        out.flush();
		} catch (Exception e){
			e.printStackTrace();
		}
	}


}

