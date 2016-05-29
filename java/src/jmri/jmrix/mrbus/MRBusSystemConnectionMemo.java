package jmri.jmrix.mrbus;

import java.util.ResourceBundle;
import jmri.jmrix.SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Minimum required implementation.
 *
 * @author Randall Wood randall.h.wood@alexandriasoftware.com
 */
public class MRBusSystemConnectionMemo extends SystemConnectionMemo {

    public MRBusSystemConnectionMemo() {
         super("M", "MRBus");
			log.info("MRBus system memo created");

    }

    @Override
    protected ResourceBundle getActionModelResourceBundle() {
        return null;
    }

	private final static Logger log = LoggerFactory.getLogger(MRBusSystemConnectionMemo.class.getName());

}


