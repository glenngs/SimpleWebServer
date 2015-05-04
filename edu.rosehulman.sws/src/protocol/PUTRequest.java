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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Jack Petry
 */
public class PUTRequest extends HttpRequest {
	
	public PUTRequest() {
		super();
	}

	public void parseParameters() throws UnsupportedEncodingException {
		String[] lines = new String(body).split(System.getProperty("line.separator"));
		List<String> wordList = Arrays.asList(lines);
		Iterator<String> i = wordList.iterator();
		Pattern p = Pattern.compile("Content-Disposition: form-data; name=\"(.*)\"");
		while(i.hasNext()) {
			//ignore the first line
			String line = i.next();
			if(i.hasNext()) {
				line = i.next();
				Matcher m = p.matcher(line);
				m.find();
				String name = m.group(1);
				line = i.next();
				line = i.next();
				String val = line;
				parameters.put(URLDecoder.decode(name, "UTF-8"), URLDecoder.decode(val, "UTF-8"));
			}
		}
	}
	
	public void finishInitialization() {
		try {
			parseParameters();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
