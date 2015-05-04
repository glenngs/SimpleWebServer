import java.io.BufferedOutputStream;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;


public class BasicServlet implements IHttpServlet {
	
	public void handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
	
		try {
			BufferedOutputStream out = httpResponse.getWriter();
			out.write("<!DOCTYPE html>".getBytes());
		
	        out.write("<html>".getBytes());
	        out.write("<head>".getBytes());
	        out.write("<title>This is a Get Response</title>".getBytes());            
	        out.write("</head>".getBytes());
	        out.write("<body>".getBytes());
	        
	        String testWord = httpResponse.getHeader().get("testWord");
	        
	        out.write("<p>".getBytes());
	        out.write(("Got HTTP " + httpRequest.getMethod() + " request! <br/>").getBytes());
	        out.write("This is our test page <br />".getBytes());
	        out.write(("Test Word: " + testWord + "<br/>").getBytes());
	        out.write("</p>".getBytes());
	        
	        out.write("<a href=/HelloWorldWeb/>Try Again!</a>".getBytes());
	        
	        out.write("</body>".getBytes());
	        out.write("</html>".getBytes());
		} catch (Exception e){
			e.printStackTrace();
		}
	}


}
