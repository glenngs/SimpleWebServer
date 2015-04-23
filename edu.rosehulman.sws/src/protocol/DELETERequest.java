/*
 * DELETERequest.java
 * Apr 23, 2015
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
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class DELETERequest extends HttpRequest {

	/**
	 * 
	 */
	public DELETERequest() {
		super();
	}

	/* (non-Javadoc)
	 * @see protocol.HttpRequest#generateResponse(java.lang.String)
	 */
	@Override
	public HttpResponse generateResponse(String rootDirectory) {
		
		File file = new File(rootDirectory + getUri());
		
		System.out.println(file.toPath());
		
		try {
			Files.delete(file.toPath());
		} catch (NoSuchFileException x) {
			return HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		} catch (IOException x) {
			x.printStackTrace();
		    return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		
		return HttpResponseFactory.create200OKNoBody(Protocol.CLOSE);
	}

}
