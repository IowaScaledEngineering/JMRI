package jmri.jmrix.mrbus;

import java.util.ResourceBundle;
import javax.swing.JMenu;

/**
 * Create a "Systems" menu containing the Jmri SECSI-specific tools
 *
 * @author	Bob Jacobsen Copyright 2003, 2006, 2007
 * @version $Revision$
 */
public class MRBusMenu extends JMenu {

    /**
     *
     */
    private static final long serialVersionUID = -8675063885996858394L;

    public MRBusMenu(String name) {
        this();
        setText(name);
    }

    public MRBusMenu() {

        super();

        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.mrbus.MRBusBundle");

        setText(rb.getString("MenuSystem"));

        add(new jmri.jmrix.mrbus.serialmon.SerialMonAction(rb.getString("MenuItemCommandMonitor")));
        add(new jmri.jmrix.mrbus.packetgen.SerialPacketGenAction(rb.getString("MenuItemSendCommand")));

    }

}
