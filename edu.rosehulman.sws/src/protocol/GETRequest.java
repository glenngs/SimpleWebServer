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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 
 * @author Jack Petry
 */
public class GETRequest extends HttpRequest {
	
	public GETRequest() {
		super();
	}
	
	public void parseParameters() throws UnsupportedEncodingException {
	    if (!this.uri.contains("?")) {
	    	return;
	    }
	    String[] split = this.uri.split("\\?");
	    this.uri = split[0];
	    String query = split[1];
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        parameters.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    
	    
	}
	
	public void finishInitialization() {
		try {
			parseParameters();
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
	}

}