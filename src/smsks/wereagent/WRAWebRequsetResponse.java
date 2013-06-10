package smsks.wereagent;

import smsks.wereagent.util.WRABufferedReader;

public class WRAWebRequsetResponse {

	String[] requestLines = null;
	String server = "";
	int contentLength;
	int lineCount = 0;
	
	private static final int maxLines = 30;
	
	public static WRAWebRequsetResponse extract(WRABufferedReader br) {
		
		WRAWebRequsetResponse request = new WRAWebRequsetResponse();
		
		String line = br.readLine();
		while (line.length() > 0) {
			int devider = line.indexOf(':');
			if (devider > -1) {
				String sKey = (line.substring(0, devider)).trim();
				String sValue = (line.substring(devider + 1)).trim();
				if (sKey == "Host") {
					request.server = sValue;
				}
				else if (sKey == "Content-Length") {
					//
				}
			}
			request.addLine(line);
			line = br.readLine();
		}
		return request;
	}
	
	public WRAWebRequsetResponse()	{
		requestLines = new String[maxLines];
	}
	
	public void addLine(String line) {
		requestLines[lineCount++] = line;
	}
	
	public String getContent() {		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < lineCount; i++) {
			sb.append(requestLines[i] + "\r\n");
		}
		
		sb.append("\r\n");
		return sb.toString();
	}
	
	public String getServer() {
		return server;
	}
}
