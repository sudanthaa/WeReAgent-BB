package smsks.wereagent;

import smsks.wereagent.util.WRABufferedDISReader;
import smsks.wereagent.util.WRABufferedISReader;

public class WRAWebRequsetResponse {

	String[] requestLines = null;
	String server = "";
	String requestType = "";
	int contentLength;
	int lineCount = 0;
	
	private static final int maxLines = 30;
	
	public static WRAWebRequsetResponse extract(WRABufferedDISReader br) {
		
		WRAWebRequsetResponse request = new WRAWebRequsetResponse();
		
		int iLine = 0;
		String line = br.readLine();
		while (line.length() > 0) {
			
			if (iLine == 0) {
				int devider = line.indexOf(' ');
				if (devider > -1) {
					request.requestType = (line.substring(0, devider)).trim();
				}
			}
			
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
			iLine++;
			line = br.readLine();
		}
		return request;
	}
	
	
	public static WRAWebRequsetResponse extract(WRABufferedISReader br) {
		
		WRAWebRequsetResponse request = new WRAWebRequsetResponse();
		
		int iLine = 0;
		String line = br.readLine();
		while (line.length() > 0) {
			
			if (iLine == 0) {
				int devider = line.indexOf(' ');
				if (devider > -1) {
					request.requestType = (line.substring(0, devider)).trim();
				}
			}
			
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
			iLine++;
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
	
	public String getRequestType() {
		return requestType;
	}
}
