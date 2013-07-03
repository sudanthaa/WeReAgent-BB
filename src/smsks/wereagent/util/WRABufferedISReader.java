package smsks.wereagent.util;

import java.io.IOException;
import java.io.InputStream;

public class WRABufferedISReader {
	
	InputStream is;

	public WRABufferedISReader(InputStream is) {
		this.is = is;
	}
	
	public String readLine() {
		
		int iState = 0;    //   1-First \ read, 2-Both \r read, 3-All \r\ read, 
		StringBuffer sb = new StringBuffer("");
		
		try {
			while (true) {
				int ichar = is.read();
				if (ichar < 0)
					return sb.toString();
				
				char c = (char) ichar;
				
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
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
