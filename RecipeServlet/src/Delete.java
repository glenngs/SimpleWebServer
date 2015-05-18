import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;
import protocol.Response404;


public class Delete implements IHttpServlet {

	@Override
	public void handleResponse(HttpRequest httpRequest,
			HttpResponse httpResponse) {
		
		String recipeName = httpRequest.getHeader().get("name");
		
		Path path = Paths.get("recipes/" + recipeName + ".rcp");
		
		StringBuilder sb = new StringBuilder();
		
		String s = new String();
		
		try {
		    Files.delete(path);
		    sb.append("<!DOCTYPE html><html><head><title>" + recipeName
					+ "</title></head>");
			
		    sb.append("<body><h1> " + recipeName + " has been deleted!</h1></body></html>");
		    
		    s = sb.toString();
		    
		} catch (DirectoryNotEmptyException|NoSuchFileException x) {
			sb.append("<!DOCTYPE html><html><head><title>" + recipeName
					+ "</title></head>");
			
		    sb.append("<body><h1> " + recipeName + " was not found, and therefore could not be deleted!</h1></body></html>");
		    
		    s = sb.toString();
		    
		} catch (IOException x) {
			sb.append("<!DOCTYPE html><html><head><title>" + recipeName
					+ "</title></head>");
			
		    sb.append("<body><h1> " + recipeName + " was found, but you're not allowed to delete it!</h1></body></html>");
		    
		    s = sb.toString();
		    
			System.out.println("BLOCKED");
			System.err.println(x);
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
