package smsks.wereagent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.io.transport.options.TcpCellularOptions;
import net.rim.device.api.util.Arrays;

import smsks.wereagent.util.WRABufferedDISReader;
import smsks.wereagent.util.WRABufferedISReader;

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
		
		Thread closer = new Thread(new Runnable() {
			public void run() {
				closeConnection();
			}
		});
		
		closer.start();
		
		try {
			join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void closeConnection() {
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
				WRABufferedDISReader brDownloader = new WRABufferedDISReader(isDownloader);

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
					break;
				}
				
				setRequest(downloaderRequest.getContent());
				
				WRAWebRequsetResponse webServerResponse = getServerResponse(downloaderRequest);
				if (webServerResponse == null) {
					connDownloader.close();
					continue;
				}
				
				setResponse(webServerResponse.getContent());

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
		
		try {
			
			String connString = "socket://" + downloaderRequest.getServer() + ":80";
			SocketConnection  sc = (SocketConnection)Connector.open(connString);
			
			if (sc == null)
			{
				setStatus("Web server connection failed.");
				return null;
			}
			
			sc.setSocketOption(SocketConnection.LINGER, 5);
			
			setStatus("Connected to server");
			InputStream is  = sc.openInputStream();
			OutputStream os = sc.openOutputStream();
			
			
			setStatus("Writing reqeust to web server");
			os.write(downloaderRequest.getContent().getBytes());
			os.flush();
			
			setStatus("Reading response from web server");
			WRABufferedISReader br = new WRABufferedISReader(is);
			WRAWebRequsetResponse reqeust = WRAWebRequsetResponse.extract(br);
			
			is.close();
			os.close();
			sc.close();
		   
			if (reqeust == null) {
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
