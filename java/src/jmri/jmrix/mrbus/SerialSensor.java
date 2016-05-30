package jmri.jmrix.mrbus;

import jmri.implementation.AbstractSensor;

/**
 * Extend jmri.AbstractSensor for serial systems
 * <P>
 * @author	Bob Jacobsen Copyright (C) 2003, 2006, 2007, 2008
 * @version $Revision$
 */
public class SerialSensor extends AbstractSensor {

    /**
     *
     */
    private static final long serialVersionUID = 5465958868822894698L;

    private int mrbusAddress;
    private int mrbusPktType;
    private int mrbusPktByte;
    private int mrbusPktBit;

    public SerialSensor(String systemName) {
        super(systemName);
        _knownState = UNKNOWN;
    }

    public SerialSensor(String systemName, String userName) {
        super(systemName, userName);
        _knownState = UNKNOWN;
    }

    public void setMRBusAttributes(int addr, int type, int byteNum, int bitNum)
    {
    	mrbusAddress = addr;
    	mrbusPktType = type;
    	mrbusPktByte = byteNum;
    	mrbusPktBit = bitNum;
    
    }

    public int getMRBusAddress()
    {
		return mrbusAddress;
    }
    
    public int getMRBusPktType()
    {
    	return mrbusPktType;
    }

	 public int getMRBusPktByte()
    {
    	return mrbusPktByte;
    }

	 public int getMRBusPktBit()
    {
    	return mrbusPktBit;
    }

    public void dispose() {
    }

    /**
     * Request an update on status.
     * <P>
     * Since status is continually being updated, this isn't active now.
     * Eventually, we may want to have this move the related AIU to the top of
     * the polling queue.
     */
    public void requestUpdateFromLayout() {
    }

}

/* @(#)SerialSensor.java */
