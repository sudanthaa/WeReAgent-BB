package smsks.wereagent.util;

import java.io.DataInputStream;
import java.io.IOException;

public class WRABufferedReader {

	DataInputStream dis;
	
	public WRABufferedReader(DataInputStream dis) {
		this.dis = dis;
	}
	
	public String readLine() {
		return "";
	}
	
	public void close() {
		try {
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
