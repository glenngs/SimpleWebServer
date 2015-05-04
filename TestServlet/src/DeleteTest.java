import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;

public class DeleteTest implements IHttpServlet {
	public void handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
		String w1 = httpRequest.getHeader().get("word1");
		String f = httpRequest.getHeader().get("filename");
		String s = "Short Delete Response: " + w1;
		
		File file = new File(f);
		
		try {
			Files.delete(file.toPath());
		} catch (Exception x) {
			x.printStackTrace();
		} 
				
		// Lets get content length in bytes
		long length = s.length();
		httpResponse.put(Protocol.CONTENT_LENGTH, length + "");
		try {
			BufferedOutputStream out = httpResponse.getWriter();
			out.write(s.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
