/*
 * GETRequest.java
 * Apr 22, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package protocol;

import java.io.File;

/**
 * 
 * @author Jack Petry
 */
public class GETRequest extends HttpRequest {
	
	public GETRequest() {
		super();
	}

	@Override
	public HttpResponse generateResponse(String rootDirectory) {
//		Map<String, String> header = request.getHeader();
//		String date = header.get("if-modified-since");
//		String hostName = header.get("host");
//		
		// Handling GET request here
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + getUri());
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + getUri() + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					// Lets create 200 OK response
					return new Response200(file, Protocol.CLOSE);
				}
				else {
					// File does not exist so lets create 404 file not found code
					return new Response404(Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				return new Response200(file, Protocol.CLOSE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			return new Response404(Protocol.CLOSE);
		}
	}

}
