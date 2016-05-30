package jmri.jmrix.mrbus.serialmon;

import jmri.jmrix.mrbus.SerialListener;
import jmri.jmrix.mrbus.SerialMessage;
import jmri.jmrix.mrbus.SerialReply;
import jmri.jmrix.mrbus.SerialTrafficController;

/**
 * Frame displaying (and logging) serial command messages
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2006, 2007, 2008
 * @version $Revision$
 */
public class SerialMonFrame extends jmri.jmrix.AbstractMonFrame implements SerialListener {

    /**
     *
     */
    private static final long serialVersionUID = -3112238221579394922L;

    public SerialMonFrame() {
        super();
    }

    protected String title() {
        return "MRBus Serial Command Monitor";
    }

    protected void init() {
        // connect to TrafficController
        SerialTrafficController.instance().addSerialListener(this);
    }

    public void dispose() {
        SerialTrafficController.instance().removeSerialListener(this);
        super.dispose();
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SBSC_USE_STRINGBUFFER_CONCATENATION", justification = "string concatenation, efficiency not as important as clarity here")
    public synchronized void message(SerialMessage l) {  // receive a message and log it

			nextLine(l.toString(), l.toString());

    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SBSC_USE_STRINGBUFFER_CONCATENATION", justification = "string concatenation, efficiency not as important as clarity here")
    public synchronized void reply(SerialReply l) {  // receive a reply message and log it

			nextLine(l.toString() + "\n", l.toString() + "\n");
//			nextLine("Received " + l.getNumDataElements() + " bytes\n", "Received " + l.getNumDataElements() + " bytes\n");
    }

}
