import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;
import server.RouteDispatcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Get implements IHttpServlet {

	public void handleResponse(HttpRequest httpRequest,
			HttpResponse httpResponse) {
		String s = null;
		if (httpRequest.getHeader().containsKey("name")) {
			String recipeName = httpRequest.getHeader().get("name");
			File f = new File("recipes/" + recipeName + ".rcp");
			if (f.exists()) {
				try {
					StringBuilder sb = new StringBuilder();
					sb.append("<!DOCTYPE html><html><head><title>" + recipeName
							+ "</title></head>");
					sb.append("<script>\r\nfunction save()\r\n{\r\n  var xmlhttp=new XMLHttpRequest();\r\n  xmlhttp.onreadystatechange=function()\r\n  {\r\n  if (xmlhttp.readyState==4 && xmlhttp.status==200)\r\n    {\r\n    alert(xmlhttp.responseText);\r\n    }\r\n  }\r\nxmlhttp.open(\"PUT\",\"http://localhost:8080/recipe/Put?name=" + URLEncoder.encode(recipeName) + "\",true);\r\nxmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\r\nvar ingredients = [];\r\nfor(var i = 0; i < ingredientList.children.length; i++) {\r\n  ingredients.push(ingredientList.children[i].childNodes[0].value);\r\n}\r\nxmlhttp.send(\"{\\\"description\\\":\\\"\" + document.getElementById(\"descriptionArea\").value + \"\\\",\\\"directions\\\":\\\"\" + document.getElementById(\"directionsArea\").value + \"\\\",\\\"ingredients\\\":\" + JSON.stringify(ingredients) + \"}\");\r\n}\r\nfunction addLI() \r\n{\r\n  var ul = document.getElementById(\"ingredientList\");\r\n  var li = document.createElement(\"li\");\r\n  var inp = document.createElement(\"input\");\r\n  var children = ul.children.length + 1;\r\n  li.setAttribute(\"id\", \"element\"+children);\r\n  li.appendChild(inp);\r\n  ul.appendChild(li);\r\n}\r\n</script>\r\n");
					
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(new FileReader(f));
					JSONObject jsonObject = (JSONObject) obj;
					String description = (String) jsonObject.get("description");
					sb.append("<p>Descirption: <br /><textarea id=\"descriptionArea\" style=\"height: 200px;width: 100%\">" + description + "</textarea><br />");
					
					String directions = (String) jsonObject.get("directions");
					sb.append("<p>Directions: <br /><textarea id=\"directionsArea\" style=\"height: 200px;width: 100%\">" + directions + "</textarea><br />");
					
					sb.append("<ul id=\"ingredientList\">");
					
					JSONArray arr = (JSONArray) jsonObject.get("ingredients");
					for (int i = 0; i < arr.size(); i++)
					{
					    String ingredient = (String) arr.get(i);
					    System.out.println(ingredient);
						sb.append("<li><input id=\"element" + i + "\" value=\"" + ingredient + "\"></li>");
					}
					

					sb.append("</ul><button id=\"add\" onclick=\"addLI()\">Add</button><button id=\"save\" onclick=\"save()\">Save</button></body></html>");

					s = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					s = "<!DOCTYPE html><html><head><title>Error</title></head><body><h1>Problem with saved recipe "
							+ recipeName + "</h1></body></html>";
				}
			} else {
				System.out.println(f.getAbsolutePath());
				s = "<!DOCTYPE html><html><head><title>Error</title></head><body><h1>Could not locate "
						+ recipeName + "</h1></body></html>";
			}

		} else {
			
			s = "<!DOCTYPE html><html><head><title>Error</title></head><body><h1>Requires a recipe name</h1></body></html>";
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
