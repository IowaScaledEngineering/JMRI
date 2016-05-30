// SerialSensorManager.java
package jmri.jmrix.mrbus;


import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import jmri.Sensor;
import jmri.JmriException;
import jmri.jmrix.mrbus.MRBusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the system-specific Sensor implementation.
 * <P>
 * System names are "VSnnnn", where nnnn is the sensor number without padding.
 * <P>
 * Sensors are numbered from 1.
 * <P>
 * @author	Bob Jacobsen Copyright (C) 2003, 2006, 2007, 2008
 * @author Dave Duchamp, multi node extensions, 2004
 * @version	$Revision$
 */
public class SerialSensorManager extends jmri.managers.AbstractSensorManager
        implements SerialListener {

    /**
     * Number of sensors per address in the naming scheme.
     * <P>
     * The first node address uses sensors from 1 to SENSORSPERNODE-1, the
     * second from SENSORSPERNODE+1 to SENSORSPERNODE+(SENSORSPERNODE-1), etc.
     * <P>
     * Must be more than, and is generally one more than,
     * {@link SerialNode#MAXSENSORS}
     *
     */
    static final int SENSORSPERNODE = 1000;

    SerialTrafficController tc = null;

    private ArrayList<SerialSensor> sensorList = null;

    public SerialSensorManager() {
        super();
        sensorList = new ArrayList<SerialSensor>();
    }

    /**
     * Return the Oak Tree system letter
     */
    public String getSystemPrefix() {
        return "Y";
    }

    // to free resources when no longer used
    public void dispose() {
    }

    public String createSystemName(String curAddress, String prefix) throws JmriException {
        String tmpSName = "";
        
        // Names should be in the form of NN:TT:BB/X  where:
        // NN = node number (hex)
        // TT = Packet type (hex)
        // BB = Byte number (byte 6 is 0)
        // X = Bit number
        
        
        Pattern pattern = Pattern.compile("([0-9A-Z]+):([0-9A-Z]+):([0-9]+)/([0-8])");
		  Matcher matcher = pattern.matcher(curAddress.toUpperCase());
		  if (!matcher.matches()) {
           log.error("Unable to convert " + curAddress + " Hardware Address to a number");
           throw new JmriException("Unable to convert " + curAddress + " to a valid Hardware Address");

        }
        
        int nodeAddr = Integer.parseInt(matcher.group(1), 16);
        int nodePktType = Integer.parseInt(matcher.group(2), 16);
        int byteIdx = Integer.parseInt(matcher.group(3), 10);
        int bitIdx = Integer.parseInt(matcher.group(4), 10);

        tmpSName = prefix + typeLetter() + String.format("%02X%02X%02d%d", nodeAddr, nodePktType, byteIdx, bitIdx);


			log.info("mrbus.createSystemName turned " + curAddress + " into " + tmpSName);
        return tmpSName;
    }

    /**
     * Create a new sensor if all checks are passed System name is normalized to
     * ensure uniqueness.
     */
    public Sensor createNewSensor(String systemName, String userName) {
        Sensor s;
        SerialSensor ss;

			log.info("Creating sensor with sysname="+systemName+" and username="+userName);

        // validate the system name, and normalize it
        String sName = SerialAddress.normalizeSystemName(systemName);
        if (sName.equals("")) {
            // system name is not valid
            log.error("Invalid Sensor system name - " + systemName);
            return null;
        }

        // does this Sensor already exist
        s = getBySystemName(sName);
        if (s != null) {
            log.error("Sensor with this name already exists - " + systemName);
            return null;
        }

        // Sensor system name is valid and Sensor doesn't exist, make a new one
        if (userName == null) {
            s = ss = new SerialSensor(sName);
        } else {
            s = ss = new SerialSensor(sName, userName);
        }  

        Pattern pattern = Pattern.compile("([0-9A-Z][0-9A-Z])([0-9A-Z][0-9A-Z])([0-9][0-9])([0-8])");
		  Matcher matcher = pattern.matcher(sName.toUpperCase().substring(2));
		  if (!matcher.matches()) {
           log.error("Unable to convert " + sName + " Hardware Address to a number");
       } else {
		     int nodeAddr = Integer.parseInt(matcher.group(1), 16);
		     int nodePktType = Integer.parseInt(matcher.group(2), 16);
		     int byteIdx = Integer.parseInt(matcher.group(3), 10);
		     int bitIdx = Integer.parseInt(matcher.group(4), 10);  
  		     ss.setMRBusAttributes(nodeAddr, nodePktType, byteIdx, bitIdx);
     		sensorList.add(ss);

		}  
        
       return s;
    }

    /**
     * Dummy routine
     */
    public void message(SerialMessage r) {
        log.warn("unexpected message");
    }

    /**
     * Process a reply to a poll of Sensors of one node
     */
    public void reply(SerialReply r) {
        // determine which node
//        log.info("Received SerialReply [" + r.toString() + "]");
        MRBusPacket p = new MRBusPacket(r);
        if (null == p)
        	return;
        
//        log.info("Searching sensors...");
		  for(int sn=0; sn < sensorList.size(); sn++)
		  {
		  		SerialSensor s = sensorList.get(sn);
		  		
//		  		log.info("Checking sensor " + sn + " - mrbus addr=" + sensorList.get(sn).getMRBusAddress() + " length=" + p.length());
//				log.info("Sensor is srcAddr="+s.getMRBusAddress()+" pktType="+s.getMRBusPktType()+ " pktByte="+s.getMRBusPktByte() + " pktBit="+s.getMRBusPktBit());		  		
//				log.info("Packet is srcAddr="+p.getSrcAddr()+" pktType="+p.getPktType()+ " pktLen="+p.length());		  		
		  		
		  		if (s.getMRBusAddress() == p.getSrcAddr() && s.getMRBusPktType() == p.getPktType() && (s.getMRBusPktByte() + 6) <= p.length())
		  		{
		  			log.info("Found matching sensor\n");
		  			log.info(r.toString());
		  			
		  			byte fromPkt = p.getByte(s.getMRBusPktByte() + 6);
		  			byte mask = (byte)(0xFF & (1<<s.getMRBusPktBit()));
		  			boolean value = (0 != (fromPkt & mask));
		  			log.info("fromPkt is " + fromPkt + " mask is "+ mask);
					try {
						if (value)
							s.setKnownState(SerialSensor.ACTIVE);
						else
							s.setKnownState(SerialSensor.INACTIVE);

					} catch (JmriException e) { }
		  		}
		  		
				
		  }
        
        
        
//        SerialNode node = (SerialNode) SerialTrafficController.instance().getNodeFromAddress(r.getAddr());
 //       if (node != null) {
  //          node.markChanges(r);
  //      }
    }

    /**
     * Method to register any orphan Sensors when a new Serial Node is created
     */
    public void registerSensorsForNode(SerialNode node) {
        // get list containing all Sensors
        java.util.Iterator<String> iter
                = getSystemNameList().iterator();
        // Iterate through the sensors
        SerialNode tNode = null;
        while (iter.hasNext()) {
            String sName = iter.next();
            if (sName == null) {
                log.error("System name null during register Sensor");
            } else {
                log.debug("system name is " + sName);
                if ((sName.charAt(0) == 'Y') && (sName.charAt(1) == 'S')) {
                    // This is a Sensor
                    tNode = SerialAddress.getNodeFromSystemName(sName);
                    if (tNode == node) {
                        // This sensor is for this new Serial Node - register it
                        node.registerSensor(getBySystemName(sName),
                                (SerialAddress.getBitFromSystemName(sName) - 1));
                    }
                }
            }
        }
    }

    /**
     * static function returning the SerialSensorManager instance to use.
     *
     * @return The registered SerialSensorManager instance for general use, if
     *         need be creating one.
     */
    static public SerialSensorManager instance() {
        if (_instance == null) {
            _instance = new SerialSensorManager();
        }
        return _instance;
    }

    static SerialSensorManager _instance = null;

    private final static Logger log = LoggerFactory.getLogger(SerialSensorManager.class.getName());
}

/* @(#)SerialSensorManager.java */
