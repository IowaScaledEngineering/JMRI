package jmri.jmrix.easydcc;

import jmri.DccLocoAddress;
import jmri.DccThrottle;
import jmri.LocoAddress;
import jmri.jmrix.AbstractThrottleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EasyDCC implementation of a ThrottleManager.
 * <P>
 * Based on early NCE code.
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2005
 * @author Modified by Kelly Loyd
 * @version $Revision$
 */
public class EasyDccThrottleManager extends AbstractThrottleManager {

    /**
     * Constructor.
     */
    public EasyDccThrottleManager(EasyDccSystemConnectionMemo memo) {
        super(memo);
    }

    public void requestThrottleSetup(LocoAddress address, boolean control) {
        // KSL 20040409 - EasyDcc does not require feedback afaik
        // don't quite know if the EasyDcc requires feedback.
        // may need to extend this.
        /* KSL - appears that the first command sent to the Queue in EasyDcc
         is 'lost' - so it may be beneficial to send a 'Send' command 
         just to wake up the command station.
         This was tested on v418 - also appears as an issue with the
         radio throttles. 
         */
        log.debug("new EasyDccThrottle for " + address);
        notifyThrottleKnown(new EasyDccThrottle((EasyDccSystemConnectionMemo) adapterMemo, (DccLocoAddress) address), address);
    }

    // KSL 20040409 - EasyDcc does not have a 'dispatch' function.
    public boolean hasDispatchFunction() {
        return false;
    }

    /**
     * Address 100 and above is a long address
     *
     */
    public boolean canBeLongAddress(int address) {
        return isLongAddress(address);
    }

    /**
     * Address 99 and below is a short address
     *
     */
    public boolean canBeShortAddress(int address) {
        return !isLongAddress(address);
    }

    /**
     * Are there any ambiguous addresses (short vs long) on this system?
     */
    public boolean addressTypeUnique() {
        return true;
    }

    /*
     * Local method for deciding short/long address
     */
    static boolean isLongAddress(int num) {
        return (num >= 100);
    }

    public int supportedSpeedModes() {
        return (DccThrottle.SpeedStepMode128 | DccThrottle.SpeedStepMode28);
    }

    public boolean disposeThrottle(jmri.DccThrottle t, jmri.ThrottleListener l) {
        if (super.disposeThrottle(t, l)) {
            int value = 0;
            DccLocoAddress address = (DccLocoAddress) t.getLocoAddress();
            byte[] result = jmri.NmraPacket.speedStep128Packet(address.getNumber(),
                    address.isLongAddress(), value, t.getIsForward());
            // KSL 20040409 - this is messy, as I only wanted
            // the address to be sent.
            EasyDccMessage m = new EasyDccMessage(7);
            // for EasyDCC, release the loco.
            // D = Dequeue
            // Cx xx (address)
            int i = 0;  // message index counter
            m.setElement(i++, 'D');

            if (address.isLongAddress()) {
                m.setElement(i++, ' ');
                m.addIntAsTwoHex(result[0] & 0xFF, i);
                i = i + 2;
                m.setElement(i++, ' ');
                m.addIntAsTwoHex(result[1] & 0xFF, i);
                i = i + 2;

            } else { // short address
                m.setElement(i++, ' ');
                m.addIntAsTwoHex(0, i);
                i = i + 2;
                m.setElement(i++, ' ');
                m.addIntAsTwoHex(result[0] & 0xFF, i);
                i = i + 2;
            }

            EasyDccTrafficController.instance().sendEasyDccMessage(m, null);
            EasyDccThrottle lnt = (EasyDccThrottle) t;
            lnt.throttleDispose();
            return true;
        }
        return false;
    }

    private final static Logger log = LoggerFactory.getLogger(EasyDccThrottleManager.class.getName());

}
