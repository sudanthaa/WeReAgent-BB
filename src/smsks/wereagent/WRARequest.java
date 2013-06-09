package smsks.wereagent;

import java.io.DataInputStream;

import smsks.wereagent.util.WRABufferedReader;

public class WRARequest {

	String[] requestLines = null;
	String server = "";
	int lineCount = 0;
	
	private static final int maxLines = 30;
	
	public static WRARequest extract(WRABufferedReader br) {
		
		WRARequest request = new WRARequest();
		
		String line = br.readLine();
		while (line.length() > 0) {
			request.addLine(line);
			line = br.readLine();
		}
		return request;
	}
	
	public WRARequest()	{
		requestLines = new String[maxLines];
	}
	
	public void addLine(String line) {
		requestLines[lineCount++] = line;
	}
	
	public String getFullRequest() {		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < lineCount; i++) {
			sb.append(requestLines[i]);
			sb.append("\r\n");
		}
		
		sb.append("\r\n");
		return sb.toString();
	}
}
