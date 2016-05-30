// SerialReply.java
package jmri.jmrix.mrbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the data payload of a serial reply packet. Note that its _only_ the
 * payload.
 *
 * @author	Bob Jacobsen Copyright (C) 2002, 2006, 2007, 2008
 * @version $Revision$
 */
public class SerialReply extends jmri.jmrix.AbstractMRReply {

    // create a new one
    public SerialReply() {
        super();
        setBinary(true);
    }

    public SerialReply(String s) {
        super(s);
        setBinary(true);
    }

    public SerialReply(SerialReply l) {
        super(l);
        setBinary(true);
    }


	public String toString()
	{
		StringBuilder s = new StringBuilder("");
		for(int i=0; i<getNumDataElements(); i++)
		{
			int val = getElement(i);
			if (0x0D == val || 0x0A == val)
				continue;
			s.append((char)val);
		}
		return new String(s);	
	}

    /**
     * Is reply to poll message
     */
    public boolean isPacket() {
        return ((int)'P' == getElement(0));
    }

    public boolean isError() {
        return ((int)'E' == getElement(0));
    }

    public boolean isInfo() {
        return ((int)'I' == getElement(0));
    }


    protected int skipPrefix(int index) {
        // doesn't have to do anything
        return index;
    }

    private final static Logger log = LoggerFactory.getLogger(SerialReply.class.getName());

}

/* @(#)SerialReply.java */
