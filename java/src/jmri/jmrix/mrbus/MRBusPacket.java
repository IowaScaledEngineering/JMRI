// MRBusPacket.java
package jmri.jmrix.mrbus;

import jmri.jmrix.mrbus.SerialReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the data payload of a serial reply packet. Note that its _only_ the
 * payload.
 *
 * @author	Bob Jacobsen Copyright (C) 2002, 2006, 2007, 2008
 * @version $Revision$
 */
public class MRBusPacket
{
	private byte[] data;
	private int numBytes;
	
	public MRBusPacket() {
		numBytes = 0;
		data = new byte[6];
	}
	
	public MRBusPacket(SerialReply s) {
		if (s.isPacket())
		{
			String asciiStr = s.toString().trim().substring(2); // Remove spaces and trim off the "P:" on the front
			int len = asciiStr.length();
			numBytes = 0;
			int bytes = len/3;
			if (bytes < 6)
				bytes = 6;
			
			data = new byte[bytes+1];
			// The first two characters 
			for (int i = 0; i < len && numBytes < 21; i += 3) {
				data[numBytes++] = (byte) ((Character.digit(asciiStr.charAt(i), 16) << 4)
                             + Character.digit(asciiStr.charAt(i+1), 16));
         }
         
      } else {
      	log.info("Packet is not a packet");
      }
	}
	
	public int length()
	{
		return numBytes;
	}
	
	public byte[] getBytes()
	{
		return (data);
	}
	
	public byte getByte(int idx)
	{
		return (data[idx]);
	}	
	
	public int getSrcAddr()
	{
		return data[1];
	}
	
	public int getPktType()
	{
		return data[5];
	}
	
	public String toString()
	{
		StringBuilder s = new StringBuilder("");
		for(int i=0; i<numBytes; i++)
		{
			s.append(Integer.toHexString((int)(data[i] & 0xFF)));
         if (i != numBytes - 1)
				s.append(" ");
		}
		return new String(s);		
	}
	
   private final static Logger log = LoggerFactory.getLogger(MRBusPacket.class.getName());
	
}

