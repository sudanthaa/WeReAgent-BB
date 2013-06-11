package smsks.wereagent;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
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
	String uuid = "";
	int statusSeed = 0;
	int requestSeed = 0;
	int responseSeed = 0;
	
	public WRAServer(String uuid) {
		
		this.uuid = uuid;
		setStatus("Not Ready.");
		setResponse("");
		setRequest("");
	}
	
	public void run() {
		serverLoop();
	}
	
	// Run in UI thread.
	public boolean halt() {
		
		try {
			StreamConnection sc = (StreamConnection)Connector.open(
					"btspp://localhost:" + uuid + ";name=" + "WeReAgent");
			
			DataOutputStream dos = sc.openDataOutputStream();
			String closeReqeust = "CLOSE / HTTP/1.1\r\n\r\n";
			dos.write(closeReqeust.getBytes());
			dos.flush();
			
			dos.close();
			sc.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void serverLoop() {
        // Open a connection and wait for client requests
		
		try {
			setStatus("Starting server");
			StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(
					"btspp://localhost:" + uuid + ";name=" + "WeReAgent");
			
			while (true) {  
				setStatus("Accepting clients");
				StreamConnection connDownloader = service.acceptAndOpen();

				setStatus("Client connected");

				DataInputStream isDownloader = connDownloader.openDataInputStream();
				DataOutputStream osDownloader = connDownloader.openDataOutputStream();
				WRABufferedReader brDownloader = new WRABufferedReader(isDownloader);

				setStatus("Reading client request");
				WRAWebRequsetResponse downloaderRequest =  
						WRAWebRequsetResponse.extract(brDownloader);

				setStatus("Request read complete");

				if (downloaderRequest == null) {
					connDownloader.close();
					brDownloader.close();
					break;
				}
				
				if (downloaderRequest.getRequestType() == "CLOSE") {
					connDownloader.close();
					brDownloader.close();
				}
				
				WRAWebRequsetResponse webServerResponse = getServerResponse(downloaderRequest);
				if (webServerResponse == null) {
					connDownloader.close();
					continue;
				}

				setStatus("Sending the result to downloader");
				osDownloader.write(webServerResponse.getContent().getBytes());

				connDownloader.close();
			}
		} catch (Exception e) {
			setStatus("Server failed: " + e.toString());
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
	
	private WRAWebRequsetResponse getServerResponse(WRAWebRequsetResponse downloaderRequest) {

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
		
		setStatus("Connecting to web server");

		String connString = "socket://" + downloaderRequest.getServer() + ":80";
		ConnectionDescriptor cd = factory.getConnection(connString);
		Connection c = cd.getConnection();
		
		if (c == null)
		{
			setStatus("Web server connection failed.");
			return null;
		}
		
		setStatus("Connected to server");
		OutputConnection oc = (OutputConnection)c;
		InputConnection ic = (InputConnection)c;
		
		try {
			setStatus("Writing reqeust to web server");
			DataOutputStream dos = oc.openDataOutputStream();
			dos.write(downloaderRequest.getContent().getBytes());
			dos.flush();
			
			setStatus("Reading response from web server");
			DataInputStream dis = ic.openDataInputStream();
			WRABufferedReader br = new WRABufferedReader(dis);
			WRAWebRequsetResponse reqeust = WRAWebRequsetResponse.extract(br);
			
			if (reqeust == null) {
				c.close();
				return null;
			}
			
			return reqeust;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

}
