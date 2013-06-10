package smsks.wereagent;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.io.transport.options.TcpCellularOptions;
import net.rim.device.api.util.Arrays;

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
			while (true) {
				setStatus("Starting server");
		        StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(
		        		"btspp://localhost:" + "00" + ";name=" + "WeReAgent");
		        
		        setStatus("Accepting clients");
		        StreamConnection connDownloader = service.acceptAndOpen();
		        
		        setStatus("Client connected");
		        
		        DataInputStream isDownloader = connDownloader.openDataInputStream();
		        DataOutputStream osDownloader = connDownloader.openDataOutputStream();
		        WRABufferedReader brDownloader = new WRABufferedReader(isDownloader);
		        
		        setStatus("Reading client request");
		        WRARequest downloaderRequest =  WRARequest.extract(brDownloader);
		        
		        setStatus("Request read complete");
		        
		        if (downloaderRequest == null) {
		        	brDownloader.close();
		        	break;
		        }
	        	
		        setStatus("Conntecting to server");
		        
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
	
	private WRARequest getServerResponse(WRARequest downloaderRequest) {

		ConnectionFactory factory = new ConnectionFactory();
		int[] aTransports = { 
				TransportInfo.TRANSPORT_TCP_WIFI,
				TransportInfo.TRANSPORT_WAP2,
				TransportInfo.TRANSPORT_TCP_CELLULAR
			};

		// Remove any transports that are not currently available.
		for (int i = 0; i < aTransports.length ; i++) {
			int transport = aTransports[i];
			if (!TransportInfo.isTransportTypeAvailable(transport)
					|| !TransportInfo.hasSufficientCoverage(transport)) {
				Arrays.removeAt(aTransports, i);
			}
		}

		// Set options for TCP Cellular transport.
		TcpCellularOptions tcpOptions = new TcpCellularOptions();
		if (!TcpCellularOptions.isDefaultAPNSet()) {
			tcpOptions.setApn("dialogbb");
		}

		// Set ConnectionFactory options.
		if (aTransports.length > 0)	{
			factory.setPreferredTransportTypes(aTransports);
		}
		factory.setTransportTypeOptions(
				TransportInfo.TRANSPORT_TCP_CELLULAR, tcpOptions);
		factory.setAttemptsLimit(5);

		String connString = "socket://" + downloaderRequest.server + ":80";
		ConnectionDescriptor cd = factory.getConnection(connString);
		Connection c = cd.getConnection();
		
		return null;
	}

}
