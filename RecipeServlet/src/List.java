

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.nio.file.Files;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;
import server.RouteDispatcher;

public class List implements IHttpServlet {

	public void handleResponse(HttpRequest httpRequest,
			HttpResponse httpResponse) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head><title>Recipe List</title></head><body><h1>Here is a list page containing all of our recipes</h1><ul>");
		File dir = new File("recipes");
		File [] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".rcp");
		    }
		});

		for (File rcpfile : files) {
			String name = RouteDispatcher.stripExtension(rcpfile.getName());
		    sb.append("<li><a href=\"Get?name=" + URLEncoder.encode(name) + "\">" + name + "</a>" + "</li>");
		}
		sb.append("</ul></body></html>");
		
		
		
		String s = sb.toString();
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
