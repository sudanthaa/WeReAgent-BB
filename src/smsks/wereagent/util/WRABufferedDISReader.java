package smsks.wereagent.util;

import java.io.DataInputStream;
import java.io.IOException;

public class WRABufferedDISReader {

	DataInputStream dis;

	public WRABufferedDISReader(DataInputStream dis) {
		this.dis = dis;
	}
	
	public String readLine() {
		
		int iState = 0;    //   1-First \ read, 2-Both \r read, 3-All \r\ read, 
		StringBuffer sb = new StringBuffer("");
		
		try {
			while (true) {
				char c = (char) dis.readByte();
				switch (c) {
					case '\r': {
						if (iState == 0)
							iState = 1;
						else if (iState == 2)
							iState = 3;
						else
							iState = 0;
					
						break;
					}
					case '\n':{
						if (iState == 1)
							return sb.toString();
						else if (iState == 3)
							return sb.toString();
						else if (iState == 0)
							return sb.toString();  // LF without CR. 
						else
							iState = 0;
					
						break;
					}
					default: sb.append(c);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
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
