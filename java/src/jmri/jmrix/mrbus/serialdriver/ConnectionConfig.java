// ConnectionConfig.java
package jmri.jmrix.mrbus.serialdriver;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import jmri.jmrix.mrbus.nodeconfig.NodeConfigAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Definition of objects to handle configuring a MRBus layout connection
 *
 * @author Bob Jacobsen Copyright (C) 2003, 2006, 2007
 * @version	$Revision$
 */
public class ConnectionConfig extends jmri.jmrix.AbstractSerialConnectionConfig {

    /**
     * Ctor for an object being created during load process; Swing init is
     * deferred.
     */
    public ConnectionConfig(jmri.jmrix.SerialPortAdapter p) {
        super(p);
        log.info("MRBus Connection Config");
    }

    /**
     * Ctor for a functional Swing object with no prexisting adapter
     */
    public ConnectionConfig() {
        super();
        log.info("MRBus Connection Config");        
    }

    public void loadDetails(JPanel details) {
        // have to embed the usual one in a new JPanel

        JPanel p = new JPanel();
        super.loadDetails(p);

        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(p);

        // add another button
        JButton b = new JButton("Configure nodes");

        details.add(b);

        b.addActionListener(new NodeConfigAction());

    }

    public String name() {
        return "MRBus Layout Bus";
    }

    protected void setInstance() {
        adapter = SerialDriverAdapter.instance();
    }
    
  	private final static Logger log = LoggerFactory.getLogger(ConnectionConfig.class.getName());
}
