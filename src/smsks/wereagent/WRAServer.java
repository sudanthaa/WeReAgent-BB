package smsks.wereagent;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import smsks.wereagent.util.WRABufferedReader;

public class WRAServer extends Thread {
	
	String status = "";
	String request = "";
	String response = "";
	int statusSeed = 0;
	int requestSeed = 0;
	int responseSeed = 0;
	
	public WRAServer() {
		setStatus("Not Ready.");
		setResponse("");
		setRequest("");
	}
	
	public void run() {
		serverLoop();
	}
	
	public boolean halt() {
		return true;
	}
	
	private void serverLoop() {
        // Open a connection and wait for client requests
		
		try {
			while (true)
			{
				setStatus("Starting server");
		        StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(
		        		"btspp://localhost:" + "00" + ";name=" + "WeReAgent");
		        
		        setStatus("Accepting clients");
		        StreamConnection connDownloader = service.acceptAndOpen();
		        
		        setStatus("Client connected");
		        
		        DataInputStream isDownloader = connDownloader.openDataInputStream();
		        DataOutputStream osDownloader = connDownloader.openDataOutputStream();
		        
		        WRABufferedReader brDownloader = new WRABufferedReader(isDownloader);
		        WRARequest downloaderRequest =  WRARequest.extract(brDownloader);
		        
		        if (downloaderRequest == null)
		        	break;
	        	
			}
		}
		catch (Exception e) {
        
        }
	}
	
	private synchronized void setStatus(String status) {
		this.status = status;
		statusSeed++;
	}
	
	private synchronized void setResponse(String response) {
		this.response = response;
		responseSeed++;
	}
	
	private synchronized void setRequest(String request) {
		this.request = request;
		requestSeed++;
	}
	
	public synchronized int getStatusSeed() {
		return statusSeed;
	}
	
	public synchronized int getReqestSeed() {
		return requestSeed;
	}
	
	public synchronized int getResponseSeed() {
		return responseSeed;
	}
	
	public synchronized String getStatus() {
		return status;
	}
	
	public synchronized String getRequest() {
		return request;
	}
	
	public synchronized String getResponse() {
		return response;
	}

}
