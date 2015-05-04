import java.io.BufferedOutputStream;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.IHttpServlet;
import protocol.Protocol;

public class GetTest implements IHttpServlet {

	public void handleResponse(HttpRequest httpRequest,
			HttpResponse httpResponse) {
		String s = "<!DOCTYPE html>\r\n<html>\r\n<head>\r\n<script>\r\nfunction loadXMLDoc()\r\n{\r\nvar xmlhttp=new XMLHttpRequest();\r\nxmlhttp.onreadystatechange=function()\r\n  {\r\n  if (xmlhttp.readyState==4 && xmlhttp.status==200)\r\n    {\r\n    document.getElementById(\"myDiv\").innerHTML=xmlhttp.responseText;\r\n    }\r\n  }\r\nxmlhttp.open(\"POST\",\"http://localhost:8080/basic/PostTest\",true);\r\nxmlhttp.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\r\nxmlhttp.send(\"word1=Jack&word2=Best&filename=test.txt\");\r\n}\r\n</script>\r\n</head>\r\n<body>\r\n\r\n<h2>AJAX</h2>\r\n<button type=\"button\" onclick=\"loadXMLDoc()\">Request data</button>\r\n<div id=\"myDiv\"></div>\r\n \r\n</body>\r\n</html>";
				
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
