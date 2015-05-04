import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;

public class PostTest implements IHttpServlet {
	public void handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
		String w1 = httpRequest.getHeader().get("word1");
		String w2 = httpRequest.getHeader().get("word2");
		String f = httpRequest.getHeader().get("filename");
		String s = "Short Post Response: " + w1 + " " + w2;
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"))) {
		   writer.write(s);
		} catch (Exception e1) {
			e1.printStackTrace();
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
