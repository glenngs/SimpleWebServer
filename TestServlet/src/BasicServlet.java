import java.io.BufferedOutputStream;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;


public class BasicServlet implements IHttpServlet {
	
	public void handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
	
		String testWord = httpRequest.getHeader().get("testWord");
		String str = "<!DOCTYPE html><html><head><title>This is a Get Response</title></head><body><p>Got HTTP " + httpRequest.getMethod() 
				+ " request! <br/>This is our test page <br />Test Word: " + testWord + "<br/></p></body></html>";
		// Lets get content length in bytes
		long length = str.length();
		httpResponse.put(Protocol.CONTENT_LENGTH, length + "");
		try {
			BufferedOutputStream out = httpResponse.getWriter();
			out.write(str.getBytes());
	        out.flush();
		} catch (Exception e){
			e.printStackTrace();
		}
	}


}
