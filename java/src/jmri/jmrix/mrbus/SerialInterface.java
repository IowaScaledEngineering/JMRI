// SerialInterface.java
package jmri.jmrix.mrbus;

/**
 * Interface to send/receive serial information
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2006, 2007, 2008
 * @version	$Revision$
 */
public interface SerialInterface {

    public void addSerialListener(SerialListener l);

    public void removeSerialListener(SerialListener l);

    boolean status();   // true if the implementation is operational

    void sendSerialMessage(SerialMessage m, SerialListener l);  // 2nd arg gets the reply
}


/* @(#)SerialInterface.java */
