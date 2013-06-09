package smsks.wereagent;

public class WRAServer extends Thread implements Runnable {
	
	String status = "";
	int statusSeed = 0;
	int requestSeed = 0;
	int responseSeed = 0;
	WRAServer instance = null;
	
	public WRAServer() {
		new Thread(this);
	}
	
	private synchronized void setStatus(String status) {
		this.status = status;
	}
	
	public synchronized int getStatusSeed() {
		return statusSeed;
	}
	
	public synchronized String getStatus() {
		return status;
	}
	
	public boolean halt() {
		return true;
	}
	
}
