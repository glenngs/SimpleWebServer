/*
 * Response404.java
 * Apr 26, 2015
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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class Response404 extends HttpResponse {

	/**
	 * @param version
	 * @param status
	 * @param phrase
	 * @param header
	 * @param file
	 */
	public Response404(String version, int status, String phrase,
			Map<String, String> header, File file, OutputStream outStream) {
		super(version, status, phrase, header, file, outStream);
	}
	
	public Response404(String connection, OutputStream outStream) {
		this(Protocol.VERSION, Protocol.NOT_FOUND_CODE, 
				Protocol.NOT_FOUND_TEXT, new HashMap<String, String>(), null, outStream);
		
		// Lets fill up the header fields with more information
		fillGeneralHeader(this, connection);
	}

}
