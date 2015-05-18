import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;


public class Post implements IHttpServlet {

	@Override
	public void handleResponse(HttpRequest httpRequest,
			HttpResponse httpResponse) {
			
			
			String recipeName = httpRequest.getHeader().get("name");
			File f = new File("recipes/" + recipeName + ".rcp");
			char[] content = httpRequest.getBody();
			System.out.println(content);
			String s;
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"))) {
			   writer.write(content);
			   writer.flush();
			   s = "Creation Success";
			   System.out.println("Wrote");
			   System.out.println(content);
			} catch (Exception e1) {
				e1.printStackTrace();
				s = "Error while saving";
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
