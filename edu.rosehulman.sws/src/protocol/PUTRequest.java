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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author Jack Petry
 */
public class PUTRequest extends HttpRequest {
	
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public PUTRequest() {
		super();
	}

	private void parseBody() throws ProtocolException {
		String body = new String(getBody());
		String strippedTags = body.substring(body.indexOf(Protocol.CRLF) + 2, body.lastIndexOf(Protocol.CRLF,body.lastIndexOf(Protocol.CRLF)-1));

		Scanner scanner = new Scanner(strippedTags);
		String line = scanner.nextLine();
		while(!line.equals("")) {
			String[] broken = line.split(";");
			for(String st: broken) {
				String[] key_value = st.split(":|=");
				if (key_value.length != 2) {
					throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
				}
				String key = key_value[0].trim();
				String value = key_value[1].trim().replaceAll("^\"|\"$", "");
				parameters.put(key, value);
			}
			line = scanner.nextLine();
		}
		scanner.useDelimiter("\\z");
		parameters.put("body",scanner.next());
	}

	@Override
	public HttpResponse generateResponse(String rootDirectory) throws ProtocolException {
		parseBody();
		
		if (!parameters.containsKey("body") || !parameters.containsKey("filename")) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		
		File file = new File(rootDirectory + "/" + parameters.get("filename"));

		try {
			FileWriter f = new FileWriter(file,true);
			f.write(parameters.get("body"));
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		//TODO: Create better error responses
		return new Response200(Protocol.CLOSE);
	}

}
