package jmri.jmrix.mrbus;

import java.io.DataInputStream;
import jmri.jmrix.AbstractMRListener;
import jmri.jmrix.AbstractMRMessage;
import jmri.jmrix.AbstractMRNodeTrafficController;
import jmri.jmrix.AbstractMRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts Stream-based I/O to/from SECSI serial messages.
 * <P>
 * The "SerialInterface" side sends/receives message objects.
 * <P>
 * The connection to a SerialPortController is via a pair of *Streams, which
 * then carry sequences of characters for transmission. Note that this
 * processing is handled in an independent thread.
 * <P>
 * This handles the state transistions, based on the necessary state in each
 * message.
 * <P>
 * Handles initialization, polling, output, and input for multiple Serial Nodes.
 *
 * @author	Bob Jacobsen Copyright (C) 2003, 2006
 * @author Bob Jacobsen, Dave Duchamp, multiNode extensions, 2004
 */
public class SerialTrafficController extends AbstractMRNodeTrafficController implements SerialInterface {

    public SerialTrafficController() {
        super();

        init(1, 255);

        // not polled at all, so allow unexpected messages, and
        // use poll delay just to spread out startup
        setAllowUnexpectedReply(true);
        mWaitBeforePoll = 1000;  // can take a long time to send
    }

    // The methods to implement the SerialInterface
    public synchronized void addSerialListener(SerialListener l) {
        this.addListener(l);
    }

    public synchronized void removeSerialListener(SerialListener l) {
        this.removeListener(l);
    }

// remove this code when SerialLight is operational - obsoleted and doesn't belong here anyway
    /**
     * Public method to set a SECSI Output bit Note: systemName is of format
     * CNnnnBxxxx where "nnn" is the serial node number (0 - 127) "xxxx' is the
     * bit number within that node (1 thru number of defined bits) state is
     * 'true' for 0, 'false' for 1 The bit is transmitted to the hardware
     * immediately before the next poll packet is sent.
     */
    public void setSerialOutput(String systemName, boolean state) {
        // get the node and bit numbers
        SerialNode node = SerialAddress.getNodeFromSystemName(systemName);
        if (node == null) {
            log.error("bad SerialNode specification in SerialOutput system name:" + systemName);
            return;
        }
        int bit = SerialAddress.getBitFromSystemName(systemName);
        if (bit == 0) {
            log.error("bad output bit specification in SerialOutput system name:" + systemName);
            return;
        }
        // set the bit
        node.setOutputBit(bit, state);
    }
// end of code to be removed

    /**
     * Public method to set up for initialization of a Serial node
     */
    public void initializeSerialNode(SerialNode node) {
        synchronized (this) {
            // find the node in the registered node list
            for (int i = 0; i < getNumNodes(); i++) {
                if (getNode(i) == node) {
                    // found node - set up for initialization
//                    setMustInit(i, true);
                    return;
                }
            }
        }
    }

    protected AbstractMRMessage enterProgMode() {
        log.warn("enterProgMode doesnt make sense for MRBus serial");
        return null;
    }

    protected AbstractMRMessage enterNormalMode() {
        return null;
    }

    /**
     * Forward a SerialMessage to all registered SerialInterface listeners.
     */
    protected void forwardMessage(AbstractMRListener client, AbstractMRMessage m) {
        ((SerialListener) client).message((SerialMessage) m);
    }

    /**
     * Forward a SerialReply to all registered SerialInterface listeners.
     */
    protected void forwardReply(AbstractMRListener client, AbstractMRReply m) {
        ((SerialListener) client).reply((SerialReply) m);
    }

    SerialSensorManager mSensorManager = null;

    public void setSensorManager(SerialSensorManager m) {
			log.info("Setting sensor manager in STC");
        mSensorManager = m;
        this.addSerialListener(m);
    }

    /**
     * Handles initialization, output and polling from within the running thread
     */
    protected synchronized AbstractMRMessage pollMessage() {
    	return null;
    }

    synchronized protected void handleTimeout(AbstractMRMessage m, AbstractMRListener l) {
        // inform node, and if it resets then reinitialize 
        if (getNode(curSerialNodeIndex) != null) {
            if (getNode(curSerialNodeIndex).handleTimeout(m, l)) {
                setMustInit(curSerialNodeIndex, true);
            } else {
                log.warn("Timeout can't be handled due to missing node index=" + curSerialNodeIndex);
            }
        }
    }

    synchronized protected void resetTimeout(AbstractMRMessage m) {
        // inform node
        getNode(curSerialNodeIndex).resetTimeout(m);

    }

    protected AbstractMRListener pollReplyHandler() {
        return mSensorManager;
    }

    /**
     * Forward a preformatted message to the actual interface.
     */
    public void sendSerialMessage(SerialMessage m, SerialListener reply) {
        sendMessage(m, reply);
    }

    /**
     * static function returning the SerialTrafficController instance to use.
     *
     * @return The registered SerialTrafficController instance for general use,
     *         if need be creating one.
     */
    static public SerialTrafficController instance() {
        if (self == null) {
            if (log.isDebugEnabled()) {
                log.debug("creating a new SerialTrafficController object");
            }
            self = new SerialTrafficController();
        }
        return self;
    }

    static volatile protected SerialTrafficController self = null;

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "temporary until mult-system; only set at startup")
    protected void setInstance() {
        self = this;
    }

    protected AbstractMRReply newReply() {
        return new SerialReply();
    }

    protected boolean endOfMessage(AbstractMRReply msg) {
		return(msg.getElement(msg.getNumDataElements()-1) == 0x0A);
    }

    protected int currentAddr = -1; // at startup, can't match
    protected int incomingLength = 0;
    
    protected void loadChars(AbstractMRReply msg, DataInputStream istream)
            throws java.io.IOException 
    {
        int i;
        for (i = 0; i < msg.maxSize(); i++) {
            byte char1 = readByteProtected(istream);

            //if (log.isDebugEnabled()) log.debug("char: "+(char1&0xFF)+" i: "+i);
            // if there was a timeout, flush any char received and start over
            if (flushReceiveChars) {
                log.warn("timeout flushes receive buffer: {}", msg.toString());
                msg.flush();
                i = 0;  // restart
                flushReceiveChars = false;
            }

				if (0x0D == char1)
				{
					i--;
				} else if (canReceive()) {
                msg.setElement(i, char1);
                if (endOfMessage(msg)) {
                    break;
                }
            } else {
                i--; // flush char
                log.error("unsolicited character received: {}", Integer.toHexString(char1));
            }
        }
    }    

    protected void waitForStartOfReply(DataInputStream istream) throws java.io.IOException {
        // does nothing
    }

    /**
     * Add header to the outgoing byte stream.
     *
     * @param msg The output byte stream
     * @return next location in the stream to fill
     */
    protected int addHeaderToOutput(byte[] msg, AbstractMRMessage m) {
        return 0;  // Do nothing
    }

    /**
     * Although this protocol doesn't use a trailer, we implement this method to
     * set the expected reply length and address for this message.
     *
     * @param msg    The output byte stream
     * @param offset the first byte not yet used
     * @param m      the original message
     */
    protected void addTrailerToOutput(byte[] msg, int offset, AbstractMRMessage m) {
        incomingLength = ((SerialMessage) m).getResponseLength();
        currentAddr = ((SerialMessage) m).getAddr();
        return;
    }

    /**
     * Determine how much many bytes the entire message will take, including
     * space for header and trailer
     *
     * @param m The message to be sent
     * @return Number of bytes
     */
    protected int lengthOfByteStream(AbstractMRMessage m) {
        return 5; // All are 5 bytes long
    }

    private final static Logger log = LoggerFactory.getLogger(SerialTrafficController.class.getName());
}
